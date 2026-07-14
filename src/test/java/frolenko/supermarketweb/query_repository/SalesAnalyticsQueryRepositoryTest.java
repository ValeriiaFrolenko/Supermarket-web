package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.analytics.SalesAnalyticsDTO;
import frolenko.supermarketweb.filter.SalesAnalyticsFilter;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static frolenko.generated.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SalesAnalyticsQueryRepository.class)
class SalesAnalyticsQueryRepositoryTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private SalesAnalyticsQueryRepository repository;

    private int milkId;
    private int breadId;

    // Дані:
    // CHK001: E001, 2024-03-10 — продаж Milk x2 по 50 = 100
    // CHK002: E001, 2024-03-15 — продаж Bread x1 по 30 = 30
    // CHK003: E002, 2024-03-12 — продаж Milk x3 по 50 = 150
    // CHK004: E002, 2024-01-05 — продаж Bread x2 по 30 = 60

    @BeforeEach
    void setUp() {
        dsl.insertInto(EMPLOYEE)
                .set(EMPLOYEE.ID_EMPLOYEE, "E001")
                .set(EMPLOYEE.PASSWORD_HASH, "hash")
                .set(EMPLOYEE.EMPL_SURNAME, "Kovalenko")
                .set(EMPLOYEE.EMPL_NAME, "Ivan")
                .set(EMPLOYEE.EMPL_ROLE, "CASHIER")
                .set(EMPLOYEE.SALARY, BigDecimal.valueOf(5000))
                .set(EMPLOYEE.DATE_OF_BIRTH, LocalDate.of(1990, 1, 1))
                .set(EMPLOYEE.DATE_OF_START, LocalDate.of(2020, 1, 1))
                .set(EMPLOYEE.PHONE_NUMBER, "+380991234567")
                .set(EMPLOYEE.CITY, "Kyiv")
                .set(EMPLOYEE.STREET, "Main St")
                .set(EMPLOYEE.ZIP_CODE, "01001")
                .execute();

        dsl.insertInto(EMPLOYEE)
                .set(EMPLOYEE.ID_EMPLOYEE, "E002")
                .set(EMPLOYEE.PASSWORD_HASH, "hash")
                .set(EMPLOYEE.EMPL_SURNAME, "Bondarenko")
                .set(EMPLOYEE.EMPL_NAME, "Olga")
                .set(EMPLOYEE.EMPL_ROLE, "CASHIER")
                .set(EMPLOYEE.SALARY, BigDecimal.valueOf(5000))
                .set(EMPLOYEE.DATE_OF_BIRTH, LocalDate.of(1992, 5, 15))
                .set(EMPLOYEE.DATE_OF_START, LocalDate.of(2021, 3, 1))
                .set(EMPLOYEE.PHONE_NUMBER, "+380997654321")
                .set(EMPLOYEE.CITY, "Lviv")
                .set(EMPLOYEE.STREET, "Second St")
                .set(EMPLOYEE.ZIP_CODE, "79000")
                .execute();

        dsl.insertInto(CATEGORY)
                .set(CATEGORY.CATEGORY_NAME, "Dairy")
                .execute();

        int categoryId = dsl.select(CATEGORY.CATEGORY_NUMBER)
                .from(CATEGORY)
                .where(CATEGORY.CATEGORY_NAME.eq("Dairy"))
                .fetchOne(CATEGORY.CATEGORY_NUMBER);

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, categoryId)
                .set(PRODUCT.PRODUCT_NAME, "Milk")
                .set(PRODUCT.MANUFACTURER, "Manufacturer A")
                .set(PRODUCT.CHARACTERISTICS, "1L")
                .execute();

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, categoryId)
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

        dsl.insertInto(STORE_PRODUCT)
                .set(STORE_PRODUCT.UPC, "111111111111")
                .set(STORE_PRODUCT.ID_PRODUCT, milkId)
                .set(STORE_PRODUCT.SELLING_PRICE, BigDecimal.valueOf(50))
                .set(STORE_PRODUCT.PRODUCTS_NUMBER, 100)
                .set(STORE_PRODUCT.PROMOTIONAL_PRODUCT, false)
                .execute();

        dsl.insertInto(STORE_PRODUCT)
                .set(STORE_PRODUCT.UPC, "222222222222")
                .set(STORE_PRODUCT.ID_PRODUCT, breadId)
                .set(STORE_PRODUCT.SELLING_PRICE, BigDecimal.valueOf(30))
                .set(STORE_PRODUCT.PRODUCTS_NUMBER, 50)
                .set(STORE_PRODUCT.PROMOTIONAL_PRODUCT, false)
                .execute();

        // CHK001: E001, 2024-03-10
        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CHK001")
                .set(CHECK_TABLE.ID_EMPLOYEE, "E001")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 3, 10, 10, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(100))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(20))
                .execute();

        dsl.insertInto(SALE)
                .set(SALE.UPC, "111111111111")
                .set(SALE.CHECK_NUMBER, "CHK001")
                .set(SALE.PRODUCT_NUMBER, 2)
                .set(SALE.SELLING_PRICE, BigDecimal.valueOf(50))
                .execute();

        // CHK002: E001, 2024-03-15
        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CHK002")
                .set(CHECK_TABLE.ID_EMPLOYEE, "E001")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 3, 15, 12, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(30))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(6))
                .execute();

        dsl.insertInto(SALE)
                .set(SALE.UPC, "222222222222")
                .set(SALE.CHECK_NUMBER, "CHK002")
                .set(SALE.PRODUCT_NUMBER, 1)
                .set(SALE.SELLING_PRICE, BigDecimal.valueOf(30))
                .execute();

        // CHK003: E002, 2024-03-12
        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CHK003")
                .set(CHECK_TABLE.ID_EMPLOYEE, "E002")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 3, 12, 9, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(150))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(30))
                .execute();

        dsl.insertInto(SALE)
                .set(SALE.UPC, "111111111111")
                .set(SALE.CHECK_NUMBER, "CHK003")
                .set(SALE.PRODUCT_NUMBER, 3)
                .set(SALE.SELLING_PRICE, BigDecimal.valueOf(50))
                .execute();

        // CHK004: E002, 2024-01-05
        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CHK004")
                .set(CHECK_TABLE.ID_EMPLOYEE, "E002")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 1, 5, 8, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(60))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(12))
                .execute();

        dsl.insertInto(SALE)
                .set(SALE.UPC, "222222222222")
                .set(SALE.CHECK_NUMBER, "CHK004")
                .set(SALE.PRODUCT_NUMBER, 2)
                .set(SALE.SELLING_PRICE, BigDecimal.valueOf(30))
                .execute();
    }

    @Test
    void findByFilter_emptyFilter_returnsAllRows() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        // 4 рядки: CHK001/Milk, CHK002/Bread, CHK003/Milk, CHK004/Bread
        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    @Test
    void findByFilter_emptyFilter_aggregatesCorrectly() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "checkNumber"))
        );
        SalesAnalyticsDTO chk001 = result.getContent().stream()
                .filter(d -> d.checkNumber().equals("CHK001"))
                .findFirst().orElseThrow();
        assertThat(chk001.productName()).isEqualTo("Milk");
        assertThat(chk001.quantitySold()).isEqualTo(2);
        assertThat(chk001.totalAmount()).isEqualTo(100.0);
        assertThat(chk001.cashierName()).isEqualTo("Kovalenko Ivan");
    }

    @Test
    void findByFilter_dateFrom_excludesRowsBefore() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder()
                        .dateFrom(LocalDate.of(2024, 3, 1))
                        .build(),
                PageRequest.of(0, 10)
        );
        // CHK004 (січень) не потрапляє
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).noneMatch(d -> d.checkNumber().equals("CHK004"));
    }

    @Test
    void findByFilter_dateTo_excludesRowsAfter() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder()
                        .dateTo(LocalDate.of(2024, 3, 11))
                        .build(),
                PageRequest.of(0, 10)
        );
        // Тільки CHK001 (10 берез) і CHK004 (5 січ)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).noneMatch(d -> d.checkNumber().equals("CHK002"));
        assertThat(result.getContent()).noneMatch(d -> d.checkNumber().equals("CHK003"));
    }

    @Test
    void findByFilter_dateRange_returnsOnlyRowsInRange() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder()
                        .dateFrom(LocalDate.of(2024, 3, 1))
                        .dateTo(LocalDate.of(2024, 3, 31))
                        .build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).noneMatch(d -> d.checkNumber().equals("CHK004"));
    }

    @Test
    void findByFilter_byProductId_returnsOnlyThatProduct() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().productId(milkId).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(d -> d.productName().equals("Milk"));
    }

    @Test
    void findByFilter_byProductId_doesNotReturnOtherProducts() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().productId(breadId).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).noneMatch(d -> d.productName().equals("Milk"));
    }

    @Test
    void findByFilter_byEmployeeId_returnsOnlyThatEmployee() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().employeeId("E001").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(d -> d.cashierName().equals("Kovalenko Ivan"));
    }

    @Test
    void findByFilter_byEmployeeId_doesNotReturnOtherEmployees() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().employeeId("E002").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).noneMatch(d -> d.cashierName().equals("Kovalenko Ivan"));
    }

    @Test
    void findByFilter_byProductIdAndEmployeeId_returnsIntersection() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder()
                        .productId(milkId)
                        .employeeId("E001")
                        .build(),
                PageRequest.of(0, 10)
        );
        // Тільки CHK001: E001 продав Milk
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().checkNumber()).isEqualTo("CHK001");
    }

    @Test
    void findByFilter_noMatch_returnsEmpty() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder()
                        .dateFrom(LocalDate.of(2025, 1, 1))
                        .build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByFilter_sortByTotalAmountDesc_returnsCorrectOrder() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "totalAmount"))
        );
        assertThat(result.getContent().getFirst().totalAmount()).isEqualTo(150.0);
        assertThat(result.getContent().getLast().totalAmount()).isEqualTo(30.0);
    }

    @Test
    void findByFilter_sortByQuantitySoldAsc_returnsCorrectOrder() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "quantitySold"))
        );
        assertThat(result.getContent().getFirst().quantitySold()).isEqualTo(1);
        assertThat(result.getContent().getLast().quantitySold()).isEqualTo(3);
    }

    @Test
    void findByFilter_sortByPrintDateAsc_returnsChronologicalOrder() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "printDate"))
        );
        assertThat(result.getContent().getFirst().checkNumber()).isEqualTo("CHK004");
        assertThat(result.getContent().getLast().checkNumber()).isEqualTo("CHK002");
    }

    @Test
    void findByFilter_pagination_returnsCorrectPage() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().build(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "checkNumber"))
        );
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByFilter_secondPage_returnsRemainingItems() {
        Page<SalesAnalyticsDTO> result = repository.findByFilter(
                SalesAnalyticsFilter.builder().build(),
                PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "checkNumber"))
        );
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().getFirst().checkNumber()).isEqualTo("CHK003");
    }
}