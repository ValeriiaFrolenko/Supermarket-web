package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.product.ProductListDTO;
import frolenko.supermarketweb.filter.ProductFilter;
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

import java.util.Comparator;

import static frolenko.generated.Tables.CATEGORY;
import static frolenko.generated.Tables.PRODUCT;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProductQueryRepository.class)
class ProductQueryRepositoryTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private ProductQueryRepository repository;

    private int category1Id;
    private int category2Id;

    @BeforeEach
    void setUp() {
        dsl.insertInto(CATEGORY)
                .set(CATEGORY.CATEGORY_NAME, "Dairy")
                .execute();

        dsl.insertInto(CATEGORY)
                .set(CATEGORY.CATEGORY_NAME, "Bakery")
                .execute();

        category1Id = dsl.select(CATEGORY.CATEGORY_NUMBER)
                .from(CATEGORY)
                .where(CATEGORY.CATEGORY_NAME.eq("Dairy"))
                .fetchOne(CATEGORY.CATEGORY_NUMBER);

        category2Id = dsl.select(CATEGORY.CATEGORY_NUMBER)
                .from(CATEGORY)
                .where(CATEGORY.CATEGORY_NAME.eq("Bakery"))
                .fetchOne(CATEGORY.CATEGORY_NUMBER);

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, category1Id)
                .set(PRODUCT.PRODUCT_NAME, "Milk")
                .set(PRODUCT.MANUFACTURER, "Manufacturer A")
                .set(PRODUCT.CHARACTERISTICS, "1L")
                .execute();

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, category1Id)
                .set(PRODUCT.PRODUCT_NAME, "Butter")
                .set(PRODUCT.MANUFACTURER, "Manufacturer B")
                .set(PRODUCT.CHARACTERISTICS, "200g")
                .execute();

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, category2Id)
                .set(PRODUCT.PRODUCT_NAME, "White Bread")
                .set(PRODUCT.MANUFACTURER, "Manufacturer C")
                .set(PRODUCT.CHARACTERISTICS, "500g")
                .execute();

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, category2Id)
                .set(PRODUCT.PRODUCT_NAME, "Rye Bread")
                .set(PRODUCT.MANUFACTURER, "Manufacturer C")
                .set(PRODUCT.CHARACTERISTICS, "400g")
                .execute();
    }

    @Test
    void findByFilter_emptyFilter_returnsAll() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(4);
    }

    @Test
    void findByFilter_byName_returnsOnlyMatching() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().name("Bread").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.productName().contains("Bread"));
    }

    @Test
    void findByFilter_byNameCaseInsensitive_returnsOnlyMatching() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().name("bread").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.productName().toLowerCase().contains("bread"));
    }

    @Test
    void findByFilter_byNameSubstring_returnsOnlyMatching() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().name("ilk").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().productName()).isEqualTo("Milk");
    }

    @Test
    void findByFilter_byCategoryId_returnsOnlyMatching() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().categoryId(category1Id).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.categoryName().equals("Dairy"));
    }

    @Test
    void findByFilter_byNameAndCategoryId_returnsOnlyMatching() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().name("Bread").categoryId(category2Id).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.categoryName().equals("Bakery"));
    }

    @Test
    void findByFilter_noMatch_returnsEmpty() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().name("Nonexistent").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByFilter_defaultSort_returnsSortedByProductNameAsc() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(ProductListDTO::productName)
        );
    }

    @Test
    void findByFilter_sortByProductNameDesc_returnsSortedDesc() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "product_name"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(ProductListDTO::productName).reversed()
        );
    }

    @Test
    void findByFilter_sortByCategoryNameAsc_returnsSortedAsc() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "category_name"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(ProductListDTO::categoryName)
        );
    }

    @Test
    void findByFilter_pagination_returnsCorrectPage() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().build(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "product_name"))
        );
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByFilter_secondPage_returnsRemainingItems() {
        Page<ProductListDTO> result = repository.findByFilter(
                ProductFilter.builder().build(),
                PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "product_name"))
        );
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(2);
    }
}