package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.employee.EmployeeListDTO;
import frolenko.supermarketweb.enums.employee.EmployeeRole;
import frolenko.supermarketweb.filter.EmployeeFilter;
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
import java.util.Comparator;

import static frolenko.generated.Tables.EMPLOYEE;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(EmployeeQueryRepository.class)
class EmployeeQueryRepositoryTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private EmployeeQueryRepository repository;

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

        dsl.insertInto(EMPLOYEE)
                .set(EMPLOYEE.ID_EMPLOYEE, "EMP003")
                .set(EMPLOYEE.PASSWORD_HASH, "hash3")
                .set(EMPLOYEE.EMPL_SURNAME, "Kovalchuk")
                .set(EMPLOYEE.EMPL_NAME, "Andrii")
                .set(EMPLOYEE.EMPL_ROLE, "CASHIER")
                .set(EMPLOYEE.SALARY, BigDecimal.valueOf(14000))
                .set(EMPLOYEE.DATE_OF_BIRTH, LocalDate.of(1995, 3, 20))
                .set(EMPLOYEE.DATE_OF_START, LocalDate.of(2022, 6, 1))
                .set(EMPLOYEE.PHONE_NUMBER, "+380631112233")
                .set(EMPLOYEE.CITY, "Odesa")
                .set(EMPLOYEE.STREET, "Derybasivska 10")
                .set(EMPLOYEE.ZIP_CODE, "65000")
                .execute();
    }

    @Test
    void findByFilter_emptyFilter_returnsAll() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void findByFilter_bySurname_returnsOnlyMatching() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().surname("Koval").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(e -> e.emplSurname().startsWith("Koval"));
    }

    @Test
    void findByFilter_byName_returnsOnlyMatching() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().name("Ol").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().idEmployee()).isEqualTo("EMP002");
    }

    @Test
    void findByFilter_byPhoneNumber_returnsOnlyMatching() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().phoneNumber("+380991234567").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().idEmployee()).isEqualTo("EMP001");
    }

    @Test
    void findByFilter_byRole_returnsOnlyMatching() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().role(EmployeeRole.CASHIER).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(e -> e.emplRole().equals("CASHIER"));
    }

    @Test
    void findByFilter_bySurnameAndRole_returnsOnlyMatching() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().surname("Koval").role(EmployeeRole.CASHIER).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(e -> e.emplSurname().startsWith("Koval") && e.emplRole().equals("CASHIER"));
    }

    @Test
    void findByFilter_noMatch_returnsEmpty() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().surname("Nonexistent").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByFilter_defaultSort_returnsSortedBySurnameAsc() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(EmployeeListDTO::emplSurname)
        );
    }

    @Test
    void findByFilter_sortByNameAsc_returnsSortedAsc() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "empl_name"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(EmployeeListDTO::emplName)
        );
    }

    @Test
    void findByFilter_sortByRoleAsc_returnsSortedAsc() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "empl_role"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(EmployeeListDTO::emplRole)
        );
    }

    @Test
    void findByFilter_pagination_returnsCorrectPage() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().build(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "empl_surname"))
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByFilter_secondPage_returnsRemainingItems() {
        Page<EmployeeListDTO> result = repository.findByFilter(
                EmployeeFilter.builder().build(),
                PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "empl_surname"))
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().idEmployee()).isEqualTo("EMP001");
    }
}