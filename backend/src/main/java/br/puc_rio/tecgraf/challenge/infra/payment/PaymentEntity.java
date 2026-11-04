package br.puc_rio.tecgraf.challenge.infra.payment;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "payment")
@Getter
@Setter
@Accessors(chain = true)
public class PaymentEntity {
    @EmbeddedId
    private Id id;

    @Column(name = "payment")
    private BigDecimal amount;

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    @Accessors(chain = true)
    public static class Id implements Serializable {
        @Column(name = "id_employee")
        private String employeeId;

        @JdbcTypeCode(SqlTypes.NUMERIC)
        private int month;

        @JdbcTypeCode(SqlTypes.NUMERIC)
        private int year;
    }
}
