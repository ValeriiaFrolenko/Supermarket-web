package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.customer_card.CustomerCardListDTO;
import frolenko.supermarketweb.enums.sortby.CustomerCardSortBy;
import frolenko.supermarketweb.filter.CustomerCardFilter;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jooq.test.autoconfigure.JooqTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.List;

import static frolenko.generated.Tables.CUSTOMER_CARD;
import static org.assertj.core.api.Assertions.assertThat;

@JooqTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CustomerCardQueryRepository.class)
class CustomerCardQueryRepositoryTest {

    @Autowired
    private DSLContext dsl;

    @Autowired
    private CustomerCardQueryRepository repository;

    @BeforeEach
    void setUp() {
        dsl.insertInto(CUSTOMER_CARD)
                .set(CUSTOMER_CARD.CARD_NUMBER, "CC001")
                .set(CUSTOMER_CARD.CUST_SURNAME, "Andriienko")
                .set(CUSTOMER_CARD.CUST_NAME, "Olena")
                .set(CUSTOMER_CARD.PERCENT, 10)
                .set(CUSTOMER_CARD.PHONE_NUMBER, "+380991234567")
                .execute();

        dsl.insertInto(CUSTOMER_CARD)
                .set(CUSTOMER_CARD.CARD_NUMBER, "CC002")
                .set(CUSTOMER_CARD.CUST_SURNAME, "Boiko")
                .set(CUSTOMER_CARD.CUST_NAME, "Ivan")
                .set(CUSTOMER_CARD.PERCENT, 5)
                .set(CUSTOMER_CARD.PHONE_NUMBER, "+380507654321")
                .execute();

        dsl.insertInto(CUSTOMER_CARD)
                .set(CUSTOMER_CARD.CARD_NUMBER, "CC003")
                .set(CUSTOMER_CARD.CUST_SURNAME, "Andriiets")
                .set(CUSTOMER_CARD.CUST_NAME, "Petro")
                .set(CUSTOMER_CARD.PERCENT, 15)
                .set(CUSTOMER_CARD.PHONE_NUMBER, "+380631112233")
                .execute();
    }

    @Test
    void findByFilter_emptyFilter_returnsAll() {
        List<CustomerCardListDTO> result = repository.findByFilter(CustomerCardFilter.builder().build());
        assertThat(result).hasSize(3);
    }

    @Test
    void findByFilter_bySurname_returnsOnlyMatching() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .surname("Andr")
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.custSurname().startsWith("Andr"));
    }

    @Test
    void findByFilter_byPhoneNumber_returnsOnlyMatching() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .phoneNumber("+380991234567")
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().cardNumber()).isEqualTo("CC001");
    }

    @Test
    void findByFilter_byDiscountFrom_returnsOnlyMatching() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .discountFrom(10)
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.percent() >= 10);
    }

    @Test
    void findByFilter_byDiscountTo_returnsOnlyMatching() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .discountTo(10)
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.percent() <= 10);
    }

    @Test
    void findByFilter_byDiscountRange_returnsOnlyMatching() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .discountFrom(8)
                .discountTo(12)
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().cardNumber()).isEqualTo("CC001");
    }

    @Test
    void findByFilter_bySurnameAndDiscountRange_returnsOnlyMatching() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .surname("Andr")
                .discountFrom(12)
                .discountTo(20)
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().cardNumber()).isEqualTo("CC003");
    }

    @Test
    void findByFilter_noMatch_returnsEmpty() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .surname("Nonexistent")
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).isEmpty();
    }

    @Test
    void findByFilter_defaultSort_returnsSortedBySurnameAsc() {
        List<CustomerCardListDTO> result = repository.findByFilter(CustomerCardFilter.builder().build());
        assertThat(result).isSortedAccordingTo(
                Comparator.comparing(CustomerCardListDTO::custSurname)
        );
    }

    @Test
    void findByFilter_sortBySurnameAsc_returnsSortedAsc() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .sortBy(CustomerCardSortBy.SURNAME)
                .asc(true)
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).isSortedAccordingTo(
                Comparator.comparing(CustomerCardListDTO::custSurname)
        );
    }

    @Test
    void findByFilter_sortByPercentAsc_returnsSortedAsc() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .sortBy(CustomerCardSortBy.DISCOUNT)
                .asc(true)
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).isSortedAccordingTo(
                Comparator.comparingInt(CustomerCardListDTO::percent)
        );
    }

    @Test
    void findByFilter_sortByPercentDesc_returnsSortedDesc() {
        CustomerCardFilter filter = CustomerCardFilter.builder()
                .sortBy(CustomerCardSortBy.DISCOUNT)
                .asc(false)
                .build();
        List<CustomerCardListDTO> result = repository.findByFilter(filter);
        assertThat(result).isSortedAccordingTo(
                (a, b) -> Integer.compare(b.percent(), a.percent())
        );
    }
}