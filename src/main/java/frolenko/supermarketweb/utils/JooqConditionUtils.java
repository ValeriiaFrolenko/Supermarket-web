package frolenko.supermarketweb.utils;

import org.jooq.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JooqConditionUtils {

    public static <T> void addIfNotNull(List<Condition> conditions, Field<T> field, T value) {
        if (value != null) {
            conditions.add(field.eq(value));
        }
    }

    public static void addLikeIfNotNull(List<Condition> conditions, Field<String> field, String value) {
        if (value != null) {
            conditions.add(field.like(value + "%"));
        }
    }

    public static void addDateRangeIfNotNull(List<Condition> conditions, Field<LocalDateTime> field, LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom != null) {
            conditions.add(field.greaterOrEqual(dateFrom.atStartOfDay()));
        }
        if (dateTo != null) {
            conditions.add(field.lessOrEqual(dateTo.atTime(23, 59, 59)));
        }
    }

    public static <T extends Number & Comparable<?>> void addRangeIfNotNull(List<Condition> conditions, Field<T> field, T from, T to) {
        if (from != null) {
            conditions.add(field.greaterOrEqual(from));
        }
        if (to != null) {
            conditions.add(field.lessOrEqual(to));
        }
    }

    public static List<SortField<?>> resolveSortFields(Pageable pageable, SortField<?> defaultField, Table<?>... tables) {
        List<SortField<?>> sortFields = pageable.getSort().stream()
                .map(order -> {
                    Field<?> field = Arrays.stream(tables)
                            .map(t -> t.field(order.getProperty()))
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);
                    if (field == null) return null;
                    return order.isAscending() ? field.asc() : field.desc();
                })
                .filter(Objects::nonNull)
                .toList();

        return sortFields.isEmpty() ? List.of(defaultField) : sortFields;
    }
}