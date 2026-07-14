package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.analytics.EmployeePerformanceDTO;
import frolenko.supermarketweb.filter.EmployeePerformanceFilter;
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
@Import(EmployeePerformanceQueryRepository.class)
class EmployeePerformanceQueryRepositoryTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private EmployeePerformanceQueryRepository repository;

    // Касир A: 2 чеки — один з карткою, один без. Сума: 100 + 200 = 300
    // Касир B: 1 чек з карткою. Сума: 150
    // Касир C: 1 чек без картки. Сума: 50. Дата — поза діапазоном тестів

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

        dsl.insertInto(EMPLOYEE)
                .set(EMPLOYEE.ID_EMPLOYEE, "E003")
                .set(EMPLOYEE.PASSWORD_HASH, "hash")
                .set(EMPLOYEE.EMPL_SURNAME, "Sydorenko")
                .set(EMPLOYEE.EMPL_NAME, "Petro")
                .set(EMPLOYEE.EMPL_ROLE, "CASHIER")
                .set(EMPLOYEE.SALARY, BigDecimal.valueOf(5000))
                .set(EMPLOYEE.DATE_OF_BIRTH, LocalDate.of(1988, 8, 20))
                .set(EMPLOYEE.DATE_OF_START, LocalDate.of(2019, 6, 1))
                .set(EMPLOYEE.PHONE_NUMBER, "+380993334455")
                .set(EMPLOYEE.CITY, "Odesa")
                .set(EMPLOYEE.STREET, "Third St")
                .set(EMPLOYEE.ZIP_CODE, "65000")
                .execute();

        dsl.insertInto(CUSTOMER_CARD)
                .set(CUSTOMER_CARD.CARD_NUMBER, "CARD0000001")
                .set(CUSTOMER_CARD.CUST_SURNAME, "Petrenko")
                .set(CUSTOMER_CARD.CUST_NAME, "Maria")
                .set(CUSTOMER_CARD.PHONE_NUMBER, "+380991111111")
                .set(CUSTOMER_CARD.PERCENT, 5)
                .execute();

        // E001: чек без картки — 2024-03-10
        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CHK001")
                .set(CHECK_TABLE.ID_EMPLOYEE, "E001")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 3, 10, 10, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(100))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(20))
                .execute();

        // E001: чек з карткою — 2024-03-15
        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CHK002")
                .set(CHECK_TABLE.ID_EMPLOYEE, "E001")
                .set(CHECK_TABLE.CARD_NUMBER, "CARD0000001")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 3, 15, 12, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(200))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(40))
                .execute();

        // E002: чек з карткою — 2024-03-12
        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CHK003")
                .set(CHECK_TABLE.ID_EMPLOYEE, "E002")
                .set(CHECK_TABLE.CARD_NUMBER, "CARD0000001")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 3, 12, 9, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(150))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(30))
                .execute();

        // E003: чек без картки — 2024-01-05 (поза березнем)
        dsl.insertInto(CHECK_TABLE)
                .set(CHECK_TABLE.CHECK_NUMBER, "CHK004")
                .set(CHECK_TABLE.ID_EMPLOYEE, "E003")
                .set(CHECK_TABLE.PRINT_DATE, LocalDateTime.of(2024, 1, 5, 8, 0))
                .set(CHECK_TABLE.SUM_TOTAL, BigDecimal.valueOf(50))
                .set(CHECK_TABLE.VAT, BigDecimal.valueOf(10))
                .execute();
    }

    @Test
    void findByFilter_emptyFilter_returnsAllCashiersWithChecks() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByFilter_emptyFilter_aggregatesCorrectly() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "idEmployee"))
        );
        EmployeePerformanceDTO e001 = result.getContent().stream()
                .filter(d -> d.idEmployee().equals("E001"))
                .findFirst().orElseThrow();
        assertThat(e001.receiptCount()).isEqualTo(2);
        assertThat(e001.totalAmount()).isEqualTo(300.0);
        assertThat(e001.cashierName()).isEqualTo("Kovalenko Ivan");
    }

    @Test
    void findByFilter_dateFrom_excludesChecksBefore() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder()
                        .dateFrom(LocalDate.of(2024, 3, 1))
                        .build(),
                PageRequest.of(0, 10)
        );
        // E003 має чек тільки в січні — не потрапляє
        assertThat(result.getContent()).noneMatch(d -> d.idEmployee().equals("E003"));
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByFilter_dateTo_excludesChecksAfter() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder()
                        .dateTo(LocalDate.of(2024, 3, 11))
                        .build(),
                PageRequest.of(0, 10)
        );
        // Тільки CHK001 (E001, 10 берез) і CHK004 (E003, 5 січ) потрапляють
        // E002 має чек 12 березня — не потрапляє
        assertThat(result.getContent()).noneMatch(d -> d.idEmployee().equals("E002"));
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByFilter_dateRange_returnsOnlyChecksInRange() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder()
                        .dateFrom(LocalDate.of(2024, 3, 1))
                        .dateTo(LocalDate.of(2024, 3, 31))
                        .build(),
                PageRequest.of(0, 10)
        );
        // E003 поза діапазоном
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).noneMatch(d -> d.idEmployee().equals("E003"));
    }

    @Test
    void findByFilter_dateRange_countsOnlyChecksInRange() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder()
                        .dateFrom(LocalDate.of(2024, 3, 13))
                        .dateTo(LocalDate.of(2024, 3, 31))
                        .build(),
                PageRequest.of(0, 10)
        );
        // E001 має тільки CHK002 в цьому діапазоні (CHK001 — 10 берез, поза)
        EmployeePerformanceDTO e001 = result.getContent().stream()
                .filter(d -> d.idEmployee().equals("E001"))
                .findFirst().orElseThrow();
        assertThat(e001.receiptCount()).isEqualTo(1);
        assertThat(e001.totalAmount()).isEqualTo(200.0);
    }

    @Test
    void findByFilter_onlyWithCardAlways_excludesCashierWithAnyCheckWithoutCard() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder()
                        .onlyWithCardAlways(true)
                        .build(),
                PageRequest.of(0, 10)
        );
        // E001 має CHK001 без картки — не потрапляє
        // E003 має CHK004 без картки — не потрапляє
        // E002 всі чеки з карткою — потрапляє
        assertThat(result.getContent()).noneMatch(d -> d.idEmployee().equals("E001"));
        assertThat(result.getContent()).noneMatch(d -> d.idEmployee().equals("E003"));
        assertThat(result.getContent()).anyMatch(d -> d.idEmployee().equals("E002"));
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByFilter_onlyWithCardAlways_withDateRange_appliesDateFilterToSubquery() {
        // В діапазоні березня E001 має CHK001 (без картки) і CHK002 (з карткою) — не проходить
        // E003 поза березнем — не потрапляє взагалі (немає чеків в березні)
        // E002 в березні тільки CHK003 з карткою — проходить
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder()
                        .dateFrom(LocalDate.of(2024, 3, 1))
                        .dateTo(LocalDate.of(2024, 3, 31))
                        .onlyWithCardAlways(true)
                        .build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().idEmployee()).isEqualTo("E002");
    }

    @Test
    void findByFilter_sortByTotalAmountDesc_returnsCorrectOrder() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "totalAmount"))
        );
        assertThat(result.getContent().getFirst().idEmployee()).isEqualTo("E001");
        assertThat(result.getContent().getLast().idEmployee()).isEqualTo("E003");
    }

    @Test
    void findByFilter_sortByReceiptCountAsc_returnsCorrectOrder() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "receiptCount"))
        );
        // E002 і E003 мають по 1 чеку, E001 має 2
        assertThat(result.getContent().getLast().idEmployee()).isEqualTo("E001");
    }

    @Test
    void findByFilter_sortByCashierNameAsc_returnsAlphabeticalOrder() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "cashierName"))
        );
        assertThat(result.getContent().getFirst().cashierName()).isEqualTo("Bondarenko Olga");
        assertThat(result.getContent().getLast().cashierName()).isEqualTo("Sydorenko Petro");
    }

    @Test
    void findByFilter_pagination_returnsCorrectPage() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder().build(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "idEmployee"))
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByFilter_secondPage_returnsRemainingItems() {
        Page<EmployeePerformanceDTO> result = repository.findByFilter(
                EmployeePerformanceFilter.builder().build(),
                PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "idEmployee"))
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().idEmployee()).isEqualTo("E003");
    }
}