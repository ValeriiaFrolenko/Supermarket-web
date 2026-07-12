package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.customer_card.CustomerCardListDTO;
import frolenko.supermarketweb.enums.sortby.CustomerCardSortBy;
import frolenko.supermarketweb.filter.CustomerCardFilter;
import frolenko.supermarketweb.utils.JooqConditionUtils;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static frolenko.generated.Tables.CUSTOMER_CARD;

@Repository
@RequiredArgsConstructor
public class CustomerCardQueryRepository {

    private final DSLContext dsl;

    private SortField<?> resolveSortField(CustomerCardSortBy sortBy, boolean asc) {
        Field<?> field = switch (sortBy) {
            case SURNAME -> CUSTOMER_CARD.CUST_SURNAME;
            case DISCOUNT -> CUSTOMER_CARD.PERCENT;
        };
        return asc ? field.asc() : field.desc();
    }

    public List<CustomerCardListDTO> findByFilter(CustomerCardFilter filter) {
        List<Condition> conditions = new ArrayList<>();
        SortField<?> sortField = filter.getSortBy() != null
                ? resolveSortField(filter.getSortBy(), filter.isAsc())
                : CUSTOMER_CARD.CUST_SURNAME.asc();

        JooqConditionUtils.addLikeIfNotNull(conditions, CUSTOMER_CARD.CUST_SURNAME, filter.getSurname());
        JooqConditionUtils.addLikeIfNotNull(conditions, CUSTOMER_CARD.PHONE_NUMBER, filter.getPhoneNumber());
        JooqConditionUtils.addRangeIfNotNull(conditions, CUSTOMER_CARD.PERCENT, filter.getDiscountFrom(), filter.getDiscountTo());

        return dsl.select(
                        CUSTOMER_CARD.CARD_NUMBER,
                        CUSTOMER_CARD.CUST_SURNAME,
                        CUSTOMER_CARD.CUST_NAME,
                        CUSTOMER_CARD.PERCENT,
                        CUSTOMER_CARD.PHONE_NUMBER)
                .from(CUSTOMER_CARD)
                .where(conditions)
                .orderBy(sortField)
                .fetchInto(CustomerCardListDTO.class);
    }
}