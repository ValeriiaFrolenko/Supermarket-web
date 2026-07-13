package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.customer_card.CustomerCardListDTO;
import frolenko.supermarketweb.filter.CustomerCardFilter;
import frolenko.supermarketweb.utils.JooqConditionUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static frolenko.generated.Tables.CUSTOMER_CARD;

@Repository
@RequiredArgsConstructor
public class CustomerCardQueryRepository {

    private final DSLContext dsl;

    public Page<CustomerCardListDTO> findByFilter(CustomerCardFilter filter, Pageable pageable) {
        List<Condition> conditions = new ArrayList<>();

        JooqConditionUtils.addLikeIfNotNull(conditions, CUSTOMER_CARD.CUST_SURNAME, filter.getSurname());
        JooqConditionUtils.addLikeIfNotNull(conditions, CUSTOMER_CARD.PHONE_NUMBER, filter.getPhoneNumber());
        JooqConditionUtils.addRangeIfNotNull(conditions, CUSTOMER_CARD.PERCENT, filter.getDiscountFrom(), filter.getDiscountTo());

        List<SortField<?>> sortFields = JooqConditionUtils.resolveSortFields(
                pageable, CUSTOMER_CARD.CUST_SURNAME.asc(), CUSTOMER_CARD);

        List<CustomerCardListDTO> content = dsl.select(
                        CUSTOMER_CARD.CARD_NUMBER,
                        CUSTOMER_CARD.CUST_SURNAME,
                        CUSTOMER_CARD.CUST_NAME,
                        CUSTOMER_CARD.PERCENT,
                        CUSTOMER_CARD.PHONE_NUMBER)
                .from(CUSTOMER_CARD)
                .where(conditions)
                .orderBy(sortFields)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(CustomerCardListDTO.class);

        long total = dsl.selectCount()
                .from(CUSTOMER_CARD)
                .where(conditions)
                .fetchOptional(0, Long.class)
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}