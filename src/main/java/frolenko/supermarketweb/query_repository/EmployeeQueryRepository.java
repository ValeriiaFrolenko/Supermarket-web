package frolenko.supermarketweb.query_repository;

import frolenko.supermarketweb.dto.employee.EmployeeListDTO;
import frolenko.supermarketweb.enums.employee.EmployeeRole;
import frolenko.supermarketweb.filter.EmployeeFilter;
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

import static frolenko.generated.Tables.EMPLOYEE;

@Repository
@RequiredArgsConstructor
public class EmployeeQueryRepository {

    private final DSLContext dsl;

    public Page<EmployeeListDTO> findByFilter(EmployeeFilter filter, Pageable pageable) {
        List<Condition> conditions = new ArrayList<>();

        JooqConditionUtils.addLikeIfNotNull(conditions, EMPLOYEE.EMPL_SURNAME, filter.getSurname());
        JooqConditionUtils.addLikeIfNotNull(conditions, EMPLOYEE.EMPL_NAME, filter.getName());
        JooqConditionUtils.addLikeIfNotNull(conditions, EMPLOYEE.PHONE_NUMBER, filter.getPhoneNumber());
        if (filter.getRole() != null) {
            JooqConditionUtils.addIfNotNull(conditions, EMPLOYEE.EMPL_ROLE, filter.getRole().name());
        }

        List<SortField<?>> sortFields = JooqConditionUtils.resolveSortFields(
                pageable, EMPLOYEE.EMPL_SURNAME.asc(), EMPLOYEE);

        List<EmployeeListDTO> content = dsl.select(
                        EMPLOYEE.ID_EMPLOYEE,
                        EMPLOYEE.EMPL_SURNAME,
                        EMPLOYEE.EMPL_NAME,
                        EMPLOYEE.EMPL_ROLE,
                        EMPLOYEE.PHONE_NUMBER)
                .from(EMPLOYEE)
                .where(conditions)
                .orderBy(sortFields)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(EmployeeListDTO.class);

        long total = dsl.selectCount()
                .from(EMPLOYEE)
                .where(conditions)
                .fetchOptional(0, Long.class)
                .orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}