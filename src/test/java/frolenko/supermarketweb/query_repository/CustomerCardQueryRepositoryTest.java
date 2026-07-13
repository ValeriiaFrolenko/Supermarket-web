package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.customer_card.CustomerCardListDTO;
import frolenko.supermarketweb.filter.CustomerCardFilter;
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
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void findByFilter_bySurname_returnsOnlyMatching() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().surname("Andr").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(c -> c.custSurname().startsWith("Andr"));
    }

    @Test
    void findByFilter_byPhoneNumber_returnsOnlyMatching() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().phoneNumber("+380991234567").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().cardNumber()).isEqualTo("CC001");
    }

    @Test
    void findByFilter_byDiscountFrom_returnsOnlyMatching() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().discountFrom(10).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(c -> c.percent() >= 10);
    }

    @Test
    void findByFilter_byDiscountTo_returnsOnlyMatching() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().discountTo(10).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(c -> c.percent() <= 10);
    }

    @Test
    void findByFilter_byDiscountRange_returnsOnlyMatching() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().discountFrom(8).discountTo(12).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().cardNumber()).isEqualTo("CC001");
    }

    @Test
    void findByFilter_bySurnameAndDiscountRange_returnsOnlyMatching() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().surname("Andr").discountFrom(12).discountTo(20).build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().cardNumber()).isEqualTo("CC003");
    }

    @Test
    void findByFilter_noMatch_returnsEmpty() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().surname("Nonexistent").build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByFilter_defaultSort_returnsSortedBySurnameAsc() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().build(),
                PageRequest.of(0, 10)
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(CustomerCardListDTO::custSurname)
        );
    }

    @Test
    void findByFilter_sortBySurnameAsc_returnsSortedAsc() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "cust_surname"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparing(CustomerCardListDTO::custSurname)
        );
    }

    @Test
    void findByFilter_sortByDiscountAsc_returnsSortedAsc() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "percent"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                Comparator.comparingInt(CustomerCardListDTO::percent)
        );
    }

    @Test
    void findByFilter_sortByDiscountDesc_returnsSortedDesc() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().build(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "percent"))
        );
        assertThat(result.getContent()).isSortedAccordingTo(
                (a, b) -> Integer.compare(b.percent(), a.percent())
        );
    }

    @Test
    void findByFilter_pagination_returnsCorrectPage() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().build(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "cust_surname"))
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByFilter_secondPage_returnsRemainingItems() {
        Page<CustomerCardListDTO> result = repository.findByFilter(
                CustomerCardFilter.builder().build(),
                PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "cust_surname"))
        );
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).hasSize(1);
    }
}