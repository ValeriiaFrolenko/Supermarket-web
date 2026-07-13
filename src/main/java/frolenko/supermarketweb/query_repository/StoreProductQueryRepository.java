package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.store_product.StoreProductListDTO;
import frolenko.supermarketweb.dto.store_product.StoreProductPromoInfoDTO;
import frolenko.supermarketweb.filter.StoreProductFilter;
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
import java.util.Optional;

import static frolenko.generated.Tables.PRODUCT;
import static frolenko.generated.Tables.STORE_PRODUCT;

@Repository
@RequiredArgsConstructor
public class StoreProductQueryRepository {

    private final DSLContext dsl;

    public Page<StoreProductListDTO> findByFilter(StoreProductFilter filter, Pageable pageable) {
        List<Condition> conditions = new ArrayList<>();

        JooqConditionUtils.addLikeIfNotNull(conditions, STORE_PRODUCT.UPC, filter.getUpc());
        JooqConditionUtils.addIfNotNull(conditions, PRODUCT.PRODUCT_NAME, filter.getProductName());
        JooqConditionUtils.addIfNotNull(conditions, PRODUCT.CATEGORY_NUMBER, filter.getCategoryId());
        JooqConditionUtils.addIfNotNull(conditions, STORE_PRODUCT.PROMOTIONAL_PRODUCT, filter.getPromotional());

        List<SortField<?>> sortFields = JooqConditionUtils.resolveSortFields(
                pageable, PRODUCT.PRODUCT_NAME.asc(), STORE_PRODUCT, PRODUCT);

        List<StoreProductListDTO> content = dsl.select(
                        STORE_PRODUCT.UPC,
                        PRODUCT.PRODUCT_NAME,
                        STORE_PRODUCT.SELLING_PRICE,
                        STORE_PRODUCT.PRODUCTS_NUMBER,
                        STORE_PRODUCT.PROMOTIONAL_PRODUCT)
                .from(STORE_PRODUCT)
                .join(PRODUCT).on(STORE_PRODUCT.ID_PRODUCT.eq(PRODUCT.ID_PRODUCT))
                .where(conditions)
                .orderBy(sortFields)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(StoreProductListDTO.class);

        long total = dsl.selectCount()
                .from(STORE_PRODUCT)
                .join(PRODUCT).on(STORE_PRODUCT.ID_PRODUCT.eq(PRODUCT.ID_PRODUCT))
                .where(conditions)
                .fetchOptional(0, Long.class)
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    public Optional<StoreProductPromoInfoDTO> findPromoInfo(String upc) {
        return dsl.select(
                        PRODUCT.ID_PRODUCT,
                        DSL.exists(
                                dsl.selectOne()
                                        .from(STORE_PRODUCT.as("base"))
                                        .where(STORE_PRODUCT.as("base").UPC_PROM.eq(upc))
                        ).as("usedAsPromoBase"))
                .from(STORE_PRODUCT)
                .join(PRODUCT).on(STORE_PRODUCT.ID_PRODUCT.eq(PRODUCT.ID_PRODUCT))
                .where(STORE_PRODUCT.UPC.eq(upc))
                .fetchOptionalInto(StoreProductPromoInfoDTO.class);
    }
}