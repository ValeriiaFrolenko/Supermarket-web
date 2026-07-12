package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.check.CheckDetailsDTO;
import frolenko.supermarketweb.dto.check.CheckListDTO;
import frolenko.supermarketweb.enums.sortby.CheckSortBy;
import frolenko.supermarketweb.filter.CheckFilter;
import frolenko.supermarketweb.utils.JooqConditionUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static frolenko.generated.Tables.*;

@Repository
@RequiredArgsConstructor
public class CheckQueryRepository {

    private final DSLContext dsl;

    private SortField<?> resolveSortField(CheckSortBy sortBy, boolean asc) {
        Field<?> field = switch (sortBy) {
            case DATE -> CHECK_TABLE.PRINT_DATE;
            case EMPLOYEE -> EMPLOYEE.EMPL_SURNAME;
            case SUM_TOTAL -> CHECK_TABLE.SUM_TOTAL;
        };
        return asc ? field.asc() : field.desc();
    }

    public Optional<CheckDetailsDTO> findById(String id) {

        var employeeName = DSL.concat(EMPLOYEE.EMPL_SURNAME, DSL.val(" "), EMPLOYEE.EMPL_NAME).as("employeeName");

        var customerName = DSL.case_()
                .when(CUSTOMER_CARD.CARD_NUMBER.isNull(), DSL.val((String) null))
                .otherwise(DSL.concat(CUSTOMER_CARD.CUST_SURNAME, DSL.val(" "), CUSTOMER_CARD.CUST_NAME))
                .as("customerName");

        var baseSumSubquery = DSL.field(
                dsl.select(DSL.coalesce(DSL.sum(SALE.SELLING_PRICE.mul(SALE.PRODUCT_NUMBER)), DSL.val(0)))
                        .from(SALE)
                        .where(SALE.CHECK_NUMBER.eq(CHECK_TABLE.CHECK_NUMBER))
        );

        var baseSum = baseSumSubquery.as("baseSum");
        var discount = baseSumSubquery.sub(CHECK_TABLE.SUM_TOTAL).as("discountAmount");

        return dsl.select(
                        CHECK_TABLE.CHECK_NUMBER,
                        employeeName,
                        CHECK_TABLE.CARD_NUMBER,
                        customerName,
                        CHECK_TABLE.PRINT_DATE,
                        CHECK_TABLE.SUM_TOTAL,
                        CHECK_TABLE.VAT,
                        baseSum,
                        discount)
                .from(CHECK_TABLE)
                .join(EMPLOYEE).on(CHECK_TABLE.ID_EMPLOYEE.eq(EMPLOYEE.ID_EMPLOYEE))
                .leftJoin(CUSTOMER_CARD).on(CHECK_TABLE.CARD_NUMBER.eq(CUSTOMER_CARD.CARD_NUMBER))
                .where(CHECK_TABLE.CHECK_NUMBER.eq(id))
                .fetchOptionalInto(CheckDetailsDTO.class);
    }

    public List<CheckListDTO> findByFilter(CheckFilter filter) {
        List<Condition> conditions = new ArrayList<>();
        SortField<?> sortField = filter.getSortBy() != null
                ? resolveSortField(filter.getSortBy(), filter.isAsc())
                : CHECK_TABLE.PRINT_DATE.desc();

        JooqConditionUtils.addLikeIfNotNull(conditions, CHECK_TABLE.CHECK_NUMBER, filter.getCheckNumber());
        JooqConditionUtils.addLikeIfNotNull(conditions, EMPLOYEE.EMPL_SURNAME, filter.getCashierSurname());
        JooqConditionUtils.addIfNotNull(conditions, EMPLOYEE.ID_EMPLOYEE, filter.getEmployeeId());
        JooqConditionUtils.addDateRangeIfNotNull(conditions, CHECK_TABLE.PRINT_DATE, filter.getDateFrom(), filter.getDateTo());

        return dsl.select(
                        CHECK_TABLE.CHECK_NUMBER,
                        DSL.concat(EMPLOYEE.EMPL_SURNAME, DSL.val(" "), EMPLOYEE.EMPL_NAME).as("employeeName"),
                        CHECK_TABLE.PRINT_DATE,
                        CHECK_TABLE.SUM_TOTAL)
                .from(CHECK_TABLE)
                .join(EMPLOYEE).on(CHECK_TABLE.ID_EMPLOYEE.eq(EMPLOYEE.ID_EMPLOYEE))
                .where(conditions)
                .orderBy(sortField)
                .fetchInto(CheckListDTO.class);
    }
}