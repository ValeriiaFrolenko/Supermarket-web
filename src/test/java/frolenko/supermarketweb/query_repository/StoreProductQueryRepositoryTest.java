package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.store_product.StoreProductListDTO;
import frolenko.supermarketweb.dto.store_product.StoreProductPromoInfoDTO;
import frolenko.supermarketweb.filter.StoreProductFilter;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;

import static frolenko.generated.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(StoreProductQueryRepository.class)
class StoreProductQueryRepositoryTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private StoreProductQueryRepository repository;

    private int dairyId;
    private int bakeryId;
    private int milkId;
    private int breadId;

    @BeforeEach
    void setUp() {
        dsl.insertInto(CATEGORY).set(CATEGORY.CATEGORY_NAME, "Dairy").execute();
        dsl.insertInto(CATEGORY).set(CATEGORY.CATEGORY_NAME, "Bakery").execute();

        dairyId = dsl.select(CATEGORY.CATEGORY_NUMBER)
                .from(CATEGORY).where(CATEGORY.CATEGORY_NAME.eq("Dairy"))
                .fetchOne(CATEGORY.CATEGORY_NUMBER);

        bakeryId = dsl.select(CATEGORY.CATEGORY_NUMBER)
                .from(CATEGORY).where(CATEGORY.CATEGORY_NAME.eq("Bakery"))
                .fetchOne(CATEGORY.CATEGORY_NUMBER);

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, dairyId)
                .set(PRODUCT.PRODUCT_NAME, "Milk")
                .set(PRODUCT.MANUFACTURER, "Manufacturer A")
                .set(PRODUCT.CHARACTERISTICS, "1L")
                .execute();

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, bakeryId)
                .set(PRODUCT.PRODUCT_NAME, "Bread")
                .set(PRODUCT.MANUFACTURER, "Manufacturer B")
                .set(PRODUCT.CHARACTERISTICS, "500g")
                .execute();

        milkId = dsl.select(PRODUCT.ID_PRODUCT)
                .from(PRODUCT).where(PRODUCT.PRODUCT_NAME.eq("Milk"))
                .fetchOne(PRODUCT.ID_PRODUCT);

        breadId = dsl.select(PRODUCT.ID_PRODUCT)
                .from(PRODUCT).where(PRODUCT.PRODUCT_NAME.eq("Bread"))
                .fetchOne(PRODUCT.ID_PRODUCT);

        // базовий товар — молоко звичайне
        dsl.insertInto(STORE_PRODUCT)
                .set(STORE_PRODUCT.UPC, "111111111111")
                .set(STORE_PRODUCT.ID_PRODUCT, milkId)
                .set(STORE_PRODUCT.SELLING_PRICE, BigDecimal.valueOf(50))
                .set(STORE_PRODUCT.PRODUCTS_NUMBER, 100)
                .set(STORE_PRODUCT.PROMOTIONAL_PRODUCT, false)
                .execute();

        // промо товар — молоко зі знижкою, посилається на базове
        dsl.insertInto(STORE_PRODUCT)
                .set(STORE_PRODUCT.UPC, "111111111112")
                .set(STORE_PRODUCT.UPC_PROM, "111111111111")
                .set(STORE_PRODUCT.ID_PRODUCT, milkId)
                .set(STORE_PRODUCT.SELLING_PRICE, BigDecimal.valueOf(40))
                .set(STORE_PRODUCT.PRODUCTS_NUMBER, 20)
                .set(STORE_PRODUCT.PROMOTIONAL_PRODUCT, true)
                .execute();

        // базовий товар — хліб, не має промо
        dsl.insertInto(STORE_PRODUCT)
                .set(STORE_PRODUCT.UPC, "222222222222")
                .set(STORE_PRODUCT.ID_PRODUCT, breadId)
                .set(STORE_PRODUCT.SELLING_PRICE, BigDecimal.valueOf(30))
                .set(STORE_PRODUCT.PRODUCTS_NUMBER, 50)
                .set(STORE_PRODUCT.PROMOTIONAL_PRODUCT, false)
                .execute();
    }

    @Test
    void findByFilter_emptyFilter_returnsAll() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void findByFilter_byUpc_returnsOnlyMatching() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().upc("1111").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.upc().startsWith("1111"));
    }

    @Test
    void findByFilter_byProductName_returnsOnlyMatching() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().productName("Milk").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.productName().equals("Milk"));
    }

    @Test
    void findByFilter_byProductNameExactMatch_doesNotReturnPartial() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().productName("Mil").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void findByFilter_byCategoryId_returnsOnlyMatching() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().categoryId(dairyId).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.productName().equals("Milk"));
    }

    @Test
    void findByFilter_byPromotionalTrue_returnsOnlyPromo() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().promotional(true).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().upc()).isEqualTo("111111111112");
        assertThat(result.getContent().getFirst().promotionalProduct()).isTrue();
    }

    @Test
    void findByFilter_byPromotionalFalse_returnsOnlyNonPromo() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().promotional(false).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> !p.promotionalProduct());
    }

    @Test
    void findByFilter_byProductNameAndPromotional_returnsOnlyMatching() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().productName("Milk").promotional(true).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().upc()).isEqualTo("111111111112");
    }

    @Test
    void findByFilter_noMatch_returnsEmpty() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().upc("999999999999").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByFilter_defaultSort_returnsSortedByProductNameAsc() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(StoreProductListDTO::productName)
        );
    }

    @Test
    void findByFilter_sortBySellingPriceAsc_returnsSortedAsc() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "selling_price"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparingDouble(StoreProductListDTO::sellingPrice)
        );
    }

    @Test
    void findByFilter_sortByProductsNumberDesc_returnsSortedDesc() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "products_number"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                (a, b) -> Integer.compare(b.productNumber(), a.productNumber())
        );
    }

    @Test
    void findByFilter_pagination_returnsCorrectPage() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().build(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "product_name"))
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByFilter_secondPage_returnsRemainingItems() {
        Page<StoreProductListDTO> result = repository.findByFilter(
                StoreProductFilter.builder().build(),
                PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "product_name"))
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findPromoInfo_upcIsUsedAsPromoBase_returnsTrue() {
        Optional<StoreProductPromoInfoDTO> result = repository.findPromoInfo("111111111111");
        assertThat(result).isPresent();
        assertThat(result.get().idProduct()).isEqualTo(milkId);
        assertThat(result.get().usedAsPromoBase()).isTrue();
    }

    @Test
    void findPromoInfo_upcIsNotUsedAsPromoBase_returnsFalse() {
        Optional<StoreProductPromoInfoDTO> result = repository.findPromoInfo("222222222222");
        assertThat(result).isPresent();
        assertThat(result.get().idProduct()).isEqualTo(breadId);
        assertThat(result.get().usedAsPromoBase()).isFalse();
    }

    @Test
    void findPromoInfo_nonExistentUpc_returnsEmpty() {
        Optional<StoreProductPromoInfoDTO> result = repository.findPromoInfo("999999999999");
        assertThat(result).isEmpty();
    }
}