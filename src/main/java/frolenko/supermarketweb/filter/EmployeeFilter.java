package frolenko.supermarketweb.filter;

import frolenko.supermarketweb.enums.employee.EmployeeRole;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmployeeFilter {
    private String surname;
    private String name;
    private String phoneNumber;
    private EmployeeRole role;

    public boolean isEmpty() {
        return surname == null &&
                name == null &&
                getPhoneNumber() == null &&
                role == null;
    }

    public String getPhoneNumber() {
        if (phoneNumber == null) return null;
        if(phoneNumber.startsWith("+380")) return phoneNumber;
        if(phoneNumber.startsWith("380")) return "+" + phoneNumber;
        if(phoneNumber.startsWith("0")) return "+38" + phoneNumber;
        return phoneNumber;
    }
}