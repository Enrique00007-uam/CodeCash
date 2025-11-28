package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;
import org.openxava.calculators.CurrentLocalDateCalculator;

import javax.persistence.*;
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
    private BigDecimal monto;

    @Required
    @Column(length = 100)
    private String concepto;

    // Relación ManyToOne con CategoriaIngreso
    // @DescriptionsList permite seleccionar la categoría desde un desplegable mostrando el nombre
    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    @Required
    private CategoriaIngreso categoria;

    // Relación ManyToOne con Cuenta (necesaria para la composición bidireccional)
    @ManyToOne(fetch = FetchType.LAZY)
    @Required
    private Cuenta cuenta;

    public BigDecimal getMonto() {
        return monto;
    }
}