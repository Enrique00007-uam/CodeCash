package org.example.CodeCash.model;

import groovyjarjarpicocli.CommandLine;
import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.openxava.calculators.CurrentLocalDateCalculator;

@Getter
@Setter
@Entity
@Table(name = "gasto", schema = "public")

public class Gasto extends BaseEntity{
    @Required
    @Money
    private BigDecimal monto;
    @Required
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    @ReadOnly
    private LocalDate fecha;



    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    @Required
    private CategoriaGasto categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @Required
    private Cuenta cuenta;


    @Required
    @Column(length=100)
    private String concepto;

    public BigDecimal getMonto() {
        return monto;
    }
}
