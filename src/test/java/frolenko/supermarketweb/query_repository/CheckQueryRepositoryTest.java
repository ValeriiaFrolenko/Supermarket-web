package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.check.CheckDetailsDTO;
import frolenko.supermarketweb.dto.check.CheckListDTO;
import frolenko.supermarketweb.filter.CheckFilter;
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
import java.util.Comparator;
import java.util.Optional;

import static frolenko.generated.Tables.*;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CheckQueryRepository.class)
class CheckQueryRepositoryTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private CheckQueryRepository repository;

    @BeforeEach
    void setUp() {
        dsl.insertInto(EMPLOYEE)
                .set(EMPLOYEE.ID_EMPLOYEE, "EMP001")
                .set(EMPLOYEE.PASSWORD_HASH, "hash1")
                .set(EMPLOYEE.EMPL_SURNAME, "Kovalenko")
                .set(EMPLOYEE.EMPL_NAME, "Ivan")
                .set(EMPLOYEE.EMPL_ROLE, "CASHIER")
                .set(EMPLOYEE.SALARY, BigDecimal.valueOf(15000))
                .set(EMPLOYEE.DATE_OF_BIRTH, LocalDate.of(1990, 1, 1))
                .set(EMPLOYEE.DATE_OF_START, LocalDate.of(2020, 1, 1))
                .set(EMPLOYEE.PHONE_NUMBER, "+380991234567")
                .set(EMPLOYEE.CITY, "Kyiv")
                .set(EMPLOYEE.STREET, "Shevchenka 1")
                .set(EMPLOYEE.ZIP_CODE, "01001")
                .execute();

        dsl.insertInto(EMPLOYEE)
                .set(EMPLOYEE.ID_EMPLOYEE, "EMP002")
                .set(EMPLOYEE.PASSWORD_HASH, "hash2")
                .set(EMPLOYEE.EMPL_SURNAME, "Bondarenko")
                .set(EMPLOYEE.EMPL_NAME, "Olena")
                .set(EMPLOYEE.EMPL_ROLE, "MANAGER")
                .set(EMPLOYEE.SALARY, BigDecimal.valueOf(20000))
                .set(EMPLOYEE.DATE_OF_BIRTH, LocalDate.of(1985, 5, 15))
                .set(EMPLOYEE.DATE_OF_START, LocalDate.of(2018, 3, 1))
                .set(EMPLOYEE.PHONE_NUMBER, "+380507654321")
                .set(EMPLOYEE.CITY, "Lviv")
                .set(EMPLOYEE.STREET, "Franka 5")
                .set(EMPLOYEE.ZIP_CODE, "79000")
                .execute();

        dsl.insertInto(CUSTOMER_CARD)
                .set(CUSTOMER_CARD.CARD_NUMBER, "CC001")
                .set(CUSTOMER_CARD.CUST_SURNAME, "Petrenko")
                .set(CUSTOMER_CARD.CUST_NAME, "Maria")
                .set(CUSTOMER_CARD.PHONE_NUMBER, "+380631112233")
                .set(CUSTOMER_CARD.PERCENT, 10)
                .execute();

        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CH001")
                .set(CHECK_TABLE.ID_EMPLOYEE, "EMP001")
                .set(CHECK_TABLE.CARD_NUMBER, "CC001")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 3, 15, 10, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(90))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(18))
                .execute();

        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CH002")
                .set(CHECK_TABLE.ID_EMPLOYEE, "EMP001")
                .set(CHECK_TABLE.CARD_NUMBER, (String) null)
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 4, 20, 14, 30))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(200))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(40))
                .execute();

        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CH003")
                .set(CHECK_TABLE.ID_EMPLOYEE, "EMP002")
                .set(CHECK_TABLE.CARD_NUMBER, (String) null)
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 5, 10, 9, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(150))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(30))
                .execute();

        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "XX001")
                .set(CHECK_TABLE.ID_EMPLOYEE, "EMP001")
                .set(CHECK_TABLE.CARD_NUMBER, (String) null)
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 6, 1, 10, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(50))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(10))
                .execute();

        dsl.insertInto(CATEGORY)
                .set(CATEGORY.CATEGORY_NAME, "Food")
                .execute();

        int categoryId = dsl.select(CATEGORY.CATEGORY_NUMBER)
                .from(CATEGORY)
                .orderBy(CATEGORY.CATEGORY_NUMBER.desc())
                .limit(1)
                .fetchOne(CATEGORY.CATEGORY_NUMBER);

        dsl.insertInto(PRODUCT)
                .set(PRODUCT.CATEGORY_NUMBER, categoryId)
                .set(PRODUCT.PRODUCT_NAME, "Milk")
                .set(PRODUCT.MANUFACTURER, "Manufacturer A")
                .set(PRODUCT.CHARACTERISTICS, "1L")
                .execute();

        int productId = dsl.select(PRODUCT.ID_PRODUCT)
                .from(PRODUCT)
                .orderBy(PRODUCT.ID_PRODUCT.desc())
                .limit(1)
                .fetchOne(PRODUCT.ID_PRODUCT);

        dsl.insertInto(STORE_PRODUCT)
                .set(STORE_PRODUCT.UPC, "123456789012")
                .set(STORE_PRODUCT.ID_PRODUCT, productId)
                .set(STORE_PRODUCT.SELLING_PRICE, BigDecimal.valueOf(50))
                .set(STORE_PRODUCT.PRODUCTS_NUMBER, 100)
                .set(STORE_PRODUCT.PROMOTIONAL_PRODUCT, false)
                .execute();

        dsl.insertInto(SALE)
                .set(SALE.UPC, "123456789012")
                .set(SALE.CHECK_NUMBER, "CH001")
                .set(SALE.PRODUCT_NUMBER, 2)
                .set(SALE.SELLING_PRICE, BigDecimal.valueOf(50))
                .execute();

        dsl.insertInto(SALE)
                .set(SALE.UPC, "123456789012")
                .set(SALE.CHECK_NUMBER, "CH002")
                .set(SALE.PRODUCT_NUMBER, 4)
                .set(SALE.SELLING_PRICE, BigDecimal.valueOf(50))
                .execute();
    }

    @Test
    void findByFilter_emptyFilter_returnsAll() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(4);
    }

    @Test
    void findByFilter_byCheckNumber_returnsOnlyMatching() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().checkNumber("CH0").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).allMatch(c -> c.checkNumber().startsWith("CH0"));
        assertThat(result.getContent()).noneMatch(c -> c.checkNumber().startsWith("XX"));
    }

    @Test
    void findByFilter_byCashierSurname_returnsOnlyMatching() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().cashierSurname("Koval").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).allMatch(c -> c.employeeName().startsWith("Kovalenko"));
    }

    @Test
    void findByFilter_byEmployeeId_returnsOnlyMatching() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().employeeId("EMP002").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().checkNumber()).isEqualTo("CH003");
    }

    @Test
    void findByFilter_byDateFrom_returnsOnlyMatching() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().dateFrom(LocalDate.of(2024, 4, 1)).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).allMatch(c -> !c.printDate().isBefore(LocalDateTime.of(2024, 4, 1, 0, 0)));
    }

    @Test
    void findByFilter_byDateTo_returnsOnlyMatching() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().dateTo(LocalDate.of(2024, 4, 30)).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(c -> !c.printDate().isAfter(LocalDateTime.of(2024, 4, 30, 23, 59, 59)));
    }

    @Test
    void findByFilter_byDateRange_returnsOnlyMatching() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder()
                        .dateFrom(LocalDate.of(2024, 4, 1))
                        .dateTo(LocalDate.of(2024, 4, 30))
                        .build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().checkNumber()).isEqualTo("CH002");
    }

    @Test
    void findByFilter_byCashierSurnameAndDateRange_returnsOnlyMatching() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder()
                        .cashierSurname("Koval")
                        .dateFrom(LocalDate.of(2024, 4, 1))
                        .dateTo(LocalDate.of(2024, 4, 30))
                        .build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().checkNumber()).isEqualTo("CH002");
    }

    @Test
    void findByFilter_noMatch_returnsEmpty() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().cashierSurname("Nonexistent").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByFilter_defaultSort_returnsSortedByDateDesc() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(CheckListDTO::printDate).reversed()
        );
    }

    @Test
    void findByFilter_sortByDateAsc_returnsSortedAsc() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "print_date"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(CheckListDTO::printDate)
        );
    }

    @Test
    void findByFilter_sortBySumTotalDesc_returnsSortedDesc() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "sum_total"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                (a, b) -> Double.compare(b.sumTotal(), a.sumTotal())
        );
    }

    @Test
    void findByFilter_sortByEmployeeSurnameAsc_firstResultIsBondarenko() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "empl_surname"))
        );
        assertThat(result.getContent().getFirst().employeeName()).startsWith("Bondarenko");
    }

    @Test
    void findByFilter_pagination_returnsCorrectPage() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().build(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "print_date"))
        );
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByFilter_secondPage_returnsRemainingItems() {
        Page<CheckListDTO> result = repository.findByFilter(
                CheckFilter.builder().build(),
                PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "print_date"))
        );
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void findById_existingCheckWithCustomerCard_returnsCorrectDetails() {
        Optional<CheckDetailsDTO> result = repository.findById("CH001");
        assertThat(result).isPresent();
        assertThat(result.get().checkNumber()).isEqualTo("CH001");
        assertThat(result.get().employeeName()).isEqualTo("Kovalenko Ivan");
        assertThat(result.get().cardNumber()).isEqualTo("CC001");
        assertThat(result.get().customerName()).isEqualTo("Petrenko Maria");
        assertThat(result.get().printDate()).isEqualTo(LocalDateTime.of(2024, 3, 15, 10, 0));
        assertThat(result.get().sumTotal()).isEqualTo(90.0);
        assertThat(result.get().vat()).isEqualTo(18.0);
        assertThat(result.get().baseSum()).isEqualTo(100.0);
        assertThat(result.get().discountAmount()).isEqualTo(10.0);
    }

    @Test
    void findById_checkWithoutCustomerCard_returnsNullCustomerName() {
        Optional<CheckDetailsDTO> result = repository.findById("CH002");
        assertThat(result).isPresent();
        assertThat(result.get().cardNumber()).isNull();
        assertThat(result.get().customerName()).isNull();
    }

    @Test
    void findById_checkWithNoDiscount_returnsZeroDiscountAmount() {
        Optional<CheckDetailsDTO> result = repository.findById("CH002");
        assertThat(result).isPresent();
        assertThat(result.get().baseSum()).isEqualTo(200.0);
        assertThat(result.get().discountAmount()).isEqualTo(0.0);
    }

    @Test
    void findById_nonExistentCheck_returnsEmpty() {
        Optional<CheckDetailsDTO> result = repository.findById("NONEXISTENT");
        assertThat(result).isEmpty();
    }
}