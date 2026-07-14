package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.analytics.EmployeePerformanceDTO;
import frolenko.supermarketweb.filter.EmployeePerformanceFilter;
import frolenko.supermarketweb.utils.JooqConditionUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static frolenko.generated.Tables.*;

@Repository
@RequiredArgsConstructor
public class EmployeePerformanceQueryRepository {

    private final DSLContext dsl;

    public Page<EmployeePerformanceDTO> findByFilter(EmployeePerformanceFilter filter, Pageable pageable) {
        List<Condition> conditions = new ArrayList<>();

        JooqConditionUtils.addDateRangeIfNotNull(conditions, CHECK_TABLE.PRINT_DATE, filter.getDateFrom(), filter.getDateTo());

        if (filter.isOnlyWithCardAlways()) {
            var c2 = CHECK_TABLE.as("c2");

            List<Condition> innerConditions = new ArrayList<>();
            JooqConditionUtils.addDateRangeIfNotNull(innerConditions, c2.PRINT_DATE, filter.getDateFrom(), filter.getDateTo());

            conditions.add(
                    DSL.notExists(
                            dsl.selectOne()
                                    .from(c2)
                                    .where(c2.ID_EMPLOYEE.eq(EMPLOYEE.ID_EMPLOYEE))
                                    .and(innerConditions.isEmpty() ? DSL.trueCondition() : DSL.and(innerConditions))
                                    .andNotExists(
                                            dsl.selectOne()
                                                    .from(CUSTOMER_CARD)
                                                    .where(CUSTOMER_CARD.CARD_NUMBER.eq(c2.CARD_NUMBER))
                                    )
                    )
            );
        }

        List<SortField<?>> sortFields = JooqConditionUtils.resolveSortFields(
                pageable,
                DSL.field(DSL.name("totalAmount")).desc(),
                EMPLOYEE, CHECK_TABLE
        );

        List<EmployeePerformanceDTO> content = dsl
                .select(
                        EMPLOYEE.ID_EMPLOYEE.as("idEmployee"),
                        DSL.concat(EMPLOYEE.EMPL_SURNAME, DSL.val(" "), EMPLOYEE.EMPL_NAME).as("cashierName"),
                        DSL.count(CHECK_TABLE.CHECK_NUMBER).as("receiptCount"),
                        DSL.coalesce(DSL.sum(CHECK_TABLE.SUM_TOTAL), DSL.val(0)).as("totalAmount")
                )
                .from(EMPLOYEE)
                .join(CHECK_TABLE).on(EMPLOYEE.ID_EMPLOYEE.eq(CHECK_TABLE.ID_EMPLOYEE))
                .where(conditions)
                .groupBy(EMPLOYEE.ID_EMPLOYEE, EMPLOYEE.EMPL_SURNAME, EMPLOYEE.EMPL_NAME)
                .orderBy(sortFields)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(EmployeePerformanceDTO.class);

        long total = dsl
                .select(DSL.countDistinct(EMPLOYEE.ID_EMPLOYEE))
                .from(EMPLOYEE)
                .join(CHECK_TABLE).on(EMPLOYEE.ID_EMPLOYEE.eq(CHECK_TABLE.ID_EMPLOYEE))
                .where(conditions)
                .fetchOptional(0, Long.class)
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}