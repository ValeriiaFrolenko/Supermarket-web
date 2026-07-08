package frolenko.supermarketweb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SaleId implements Serializable {
    private static final long serialVersionUID = 2161512202798343607L;
    @Column(name = "upc", nullable = false, length = 12)
    private String upc;

    @Column(name = "check_number", nullable = false, length = 10)
    private String checkNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SaleId entity = (SaleId) o;
        return Objects.equals(this.checkNumber, entity.checkNumber) &&
                Objects.equals(this.upc, entity.upc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkNumber, upc);
    }

}