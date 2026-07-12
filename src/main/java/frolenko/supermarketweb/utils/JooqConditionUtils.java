package frolenko.supermarketweb.utils;

import frolenko.supermarketweb.enums.sortby.SortBy;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public static SortField<?> toSortField(SortBy sortBy, boolean asc) {
        Field<?> field = DSL.field(DSL.name(sortBy.getColumn()));
        return asc ? field.asc() : field.desc();
    }

    public static <T extends Number & Comparable<?>> void addRangeIfNotNull(List<Condition> conditions, Field<T> field, T from, T to) {
        if (from != null) {
            conditions.add(field.greaterOrEqual(from));
        }
        if (to != null) {
            conditions.add(field.lessOrEqual(to));
        }
    }
}