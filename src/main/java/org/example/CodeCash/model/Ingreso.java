package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;
import org.openxava.calculators.CurrentLocalDateCalculator;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "ingreso", schema = "public")
public class Ingreso extends BaseEntity {

    @Required
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    private LocalDate fecha;

    @Required
    @Money
    @PositiveOrZero
    private BigDecimal monto;

    @Required
    @Column(length = 100)
    private String concepto;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList(descriptionProperties = "nombre", condition = "nombre is not null")
    @Required
    private CategoriaIngreso categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @Required
    private Cuenta cuenta;

    @PostPersist
    @PostUpdate
    @PostRemove
    public void actualizarCuenta() {
        if (cuenta != null) {
            cuenta.actualizarSaldo();
        }
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public CategoriaIngreso getCategoria() {
        return categoria;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }
}