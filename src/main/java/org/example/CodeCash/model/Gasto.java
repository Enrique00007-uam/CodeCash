package org.example.CodeCash.model;

import groovyjarjarpicocli.CommandLine;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Money;
import org.openxava.annotations.ReadOnly;
import org.openxava.calculators.CurrentLocalDateCalculator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Gasto extends BaseEntity{
    @Required
    @Money
    private Double monto;
    @Required
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    @ReadOnly
    private LocalDate fecha;
    @Required
    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList
    private CategoriaGasto categoria;
    @Required
    @Column(length=100)
    private String concepto;
}
