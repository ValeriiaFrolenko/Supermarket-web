package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.analytics.SalesAnalyticsDTO;
import frolenko.supermarketweb.filter.SalesAnalyticsFilter;
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
public class SalesAnalyticsQueryRepository {

    private final DSLContext dsl;

    public Page<SalesAnalyticsDTO> findByFilter(SalesAnalyticsFilter filter, Pageable pageable) {
        List<Condition> conditions = new ArrayList<>();

        JooqConditionUtils.addDateRangeIfNotNull(conditions, CHECK_TABLE.PRINT_DATE, filter.getDateFrom(), filter.getDateTo());
        JooqConditionUtils.addIfNotNull(conditions, PRODUCT.ID_PRODUCT, filter.getProductId());
        JooqConditionUtils.addIfNotNull(conditions, EMPLOYEE.ID_EMPLOYEE, filter.getEmployeeId());

        List<SortField<?>> sortFields = JooqConditionUtils.resolveSortFields(
                pageable,
                CHECK_TABLE.PRINT_DATE.desc(),
                SALE, STORE_PRODUCT, PRODUCT, CHECK_TABLE, EMPLOYEE
        );

        List<SalesAnalyticsDTO> content = dsl
                .select(
                        PRODUCT.PRODUCT_NAME.as("productName"),
                        DSL.sum(SALE.PRODUCT_NUMBER).as("quantitySold"),
                        DSL.sum(SALE.PRODUCT_NUMBER.mul(SALE.SELLING_PRICE)).as("totalAmount"),
                        CHECK_TABLE.CHECK_NUMBER.as("checkNumber"),
                        DSL.concat(EMPLOYEE.EMPL_SURNAME, DSL.val(" "), EMPLOYEE.EMPL_NAME).as("cashierName"),
                        CHECK_TABLE.PRINT_DATE.as("printDate")
                )
                .from(SALE)
                .join(STORE_PRODUCT).on(SALE.UPC.eq(STORE_PRODUCT.UPC))
                .join(PRODUCT).on(STORE_PRODUCT.ID_PRODUCT.eq(PRODUCT.ID_PRODUCT))
                .join(CHECK_TABLE).on(SALE.CHECK_NUMBER.eq(CHECK_TABLE.CHECK_NUMBER))
                .join(EMPLOYEE).on(CHECK_TABLE.ID_EMPLOYEE.eq(EMPLOYEE.ID_EMPLOYEE))
                .where(conditions)
                .groupBy(
                        PRODUCT.ID_PRODUCT,
                        PRODUCT.PRODUCT_NAME,
                        CHECK_TABLE.CHECK_NUMBER,
                        EMPLOYEE.ID_EMPLOYEE,
                        EMPLOYEE.EMPL_SURNAME,
                        EMPLOYEE.EMPL_NAME,
                        CHECK_TABLE.PRINT_DATE
                )
                .orderBy(sortFields)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(SalesAnalyticsDTO.class);

        long total = dsl
                .select(DSL.countDistinct(CHECK_TABLE.CHECK_NUMBER, PRODUCT.ID_PRODUCT))
                .from(SALE)
                .join(STORE_PRODUCT).on(SALE.UPC.eq(STORE_PRODUCT.UPC))
                .join(PRODUCT).on(STORE_PRODUCT.ID_PRODUCT.eq(PRODUCT.ID_PRODUCT))
                .join(CHECK_TABLE).on(SALE.CHECK_NUMBER.eq(CHECK_TABLE.CHECK_NUMBER))
                .join(EMPLOYEE).on(CHECK_TABLE.ID_EMPLOYEE.eq(EMPLOYEE.ID_EMPLOYEE))
                .where(conditions)
                .fetchOptional(0, Long.class)
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}