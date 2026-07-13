package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.product.ProductListDTO;
import frolenko.supermarketweb.filter.ProductFilter;
import frolenko.supermarketweb.utils.JooqConditionUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static frolenko.generated.Tables.CATEGORY;
import static frolenko.generated.Tables.PRODUCT;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final DSLContext dsl;

    public Page<ProductListDTO> findByFilter(ProductFilter filter, Pageable pageable) {
        List<Condition> conditions = new ArrayList<>();

        JooqConditionUtils.addContainsIfNotNull(conditions, PRODUCT.PRODUCT_NAME, filter.getName());
        JooqConditionUtils.addIfNotNull(conditions, PRODUCT.CATEGORY_NUMBER, filter.getCategoryId());

        List<SortField<?>> sortFields = JooqConditionUtils.resolveSortFields(
                pageable, PRODUCT.PRODUCT_NAME.asc(), PRODUCT, CATEGORY);

        List<ProductListDTO> content = dsl.select(
                        PRODUCT.ID_PRODUCT,
                        PRODUCT.PRODUCT_NAME,
                        CATEGORY.CATEGORY_NAME)
                .from(PRODUCT)
                .join(CATEGORY).on(PRODUCT.CATEGORY_NUMBER.eq(CATEGORY.CATEGORY_NUMBER))
                .where(conditions)
                .orderBy(sortFields)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(ProductListDTO.class);

        long total = dsl.selectCount()
                .from(PRODUCT)
                .join(CATEGORY).on(PRODUCT.CATEGORY_NUMBER.eq(CATEGORY.CATEGORY_NUMBER))
                .where(conditions)
                .fetchOptional(0, Long.class)
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}