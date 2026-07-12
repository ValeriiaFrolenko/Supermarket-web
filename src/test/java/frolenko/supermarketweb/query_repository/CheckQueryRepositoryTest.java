package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.check.CheckDetailsDTO;
import frolenko.supermarketweb.dto.check.CheckListDTO;
import frolenko.supermarketweb.enums.sortby.CheckSortBy;
import frolenko.supermarketweb.filter.CheckFilter;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
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
    }

    @Test
    void findByFilter_emptyFilter_returnsAll() {
        List<CheckListDTO> result = repository.findByFilter(CheckFilter.builder().build());
        assertThat(result).hasSize(3);
    }

    @Test
    void findByFilter_byCheckNumber_returnsOnlyMatching() {
        CheckFilter filter = CheckFilter.builder()
                .checkNumber("CH0")
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(c -> c.checkNumber().startsWith("CH0"));
    }

    @Test
    void findByFilter_byCashierSurname_returnsOnlyMatching() {
        CheckFilter filter = CheckFilter.builder()
                .cashierSurname("Koval")
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.employeeName().startsWith("Kovalenko"));
    }

    @Test
    void findByFilter_byEmployeeId_returnsOnlyMatching() {
        CheckFilter filter = CheckFilter.builder()
                .employeeId("EMP002")
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).checkNumber()).isEqualTo("CH003");
    }

    @Test
    void findByFilter_byDateFrom_returnsOnlyMatching() {
        CheckFilter filter = CheckFilter.builder()
                .dateFrom(LocalDate.of(2024, 4, 1))
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> !c.printDate().isBefore(LocalDateTime.of(2024, 4, 1, 0, 0)));
    }

    @Test
    void findByFilter_byDateTo_returnsOnlyMatching() {
        CheckFilter filter = CheckFilter.builder()
                .dateTo(LocalDate.of(2024, 4, 30))
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> !c.printDate().isAfter(LocalDateTime.of(2024, 4, 30, 23, 59, 59)));
    }

    @Test
    void findByFilter_byDateRange_returnsOnlyMatching() {
        CheckFilter filter = CheckFilter.builder()
                .dateFrom(LocalDate.of(2024, 4, 1))
                .dateTo(LocalDate.of(2024, 4, 30))
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).checkNumber()).isEqualTo("CH002");
    }

    @Test
    void findByFilter_noMatch_returnsEmpty() {
        CheckFilter filter = CheckFilter.builder()
                .cashierSurname("Nonexistent")
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).isEmpty();
    }

    @Test
    void findByFilter_defaultSort_returnsSortedByDateDesc() {
        List<CheckListDTO> result = repository.findByFilter(CheckFilter.builder().build());
        assertThat(result).isSortedAccordingTo(
                Comparator.comparing(CheckListDTO::printDate).reversed()
        );
    }

    @Test
    void findByFilter_sortByDateAsc_returnsSortedAsc() {
        CheckFilter filter = CheckFilter.builder()
                .sortBy(CheckSortBy.DATE)
                .asc(true)
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).isSortedAccordingTo(
                Comparator.comparing(CheckListDTO::printDate)
        );
    }

    @Test
    void findByFilter_sortBySumTotalDesc_returnsSortedDesc() {
        CheckFilter filter = CheckFilter.builder()
                .sortBy(CheckSortBy.SUM_TOTAL)
                .asc(false)
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).isSortedAccordingTo(
                (a, b) -> Double.compare(b.sumTotal(), a.sumTotal())
        );
    }

    @Test
    void findByFilter_sortByEmployeeSurnameAsc_returnsSortedAsc() {
        CheckFilter filter = CheckFilter.builder()
                .sortBy(CheckSortBy.EMPLOYEE)
                .asc(true)
                .build();
        List<CheckListDTO> result = repository.findByFilter(filter);
        assertThat(result).isSortedAccordingTo(
                Comparator.comparing(CheckListDTO::employeeName)
        );
    }

    @Test
    void findById_existingCheck_returnsCorrectDetails() {
        Optional<CheckDetailsDTO> result = repository.findById("CH001");
        assertThat(result).isPresent();
        assertThat(result.get().checkNumber()).isEqualTo("CH001");
        assertThat(result.get().employeeName()).isEqualTo("Kovalenko Ivan");
        assertThat(result.get().cardNumber()).isEqualTo("CC001");
        assertThat(result.get().customerName()).isEqualTo("Petrenko Maria");
        assertThat(result.get().sumTotal()).isEqualTo(90.0);
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
    void findById_nonExistentCheck_returnsEmpty() {
        Optional<CheckDetailsDTO> result = repository.findById("NONEXISTENT");
        assertThat(result).isEmpty();
    }
}