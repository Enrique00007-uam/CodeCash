package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.example.CodeCash.service.CalculadorBalance;
import org.example.CodeCash.service.CalculadorTotalGastos;
import org.example.CodeCash.service.CalculadorTotalIngresos;
import org.openxava.annotations.*;
import org.openxava.calculators.CurrentLocalDateCalculator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "reporte_financiero", schema = "public")
public class ReporteFinanciero extends BaseEntity {

    @Required
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    private LocalDate fecha;

    @Money
    @DefaultValueCalculator(value = CalculadorTotalIngresos.class)
    private BigDecimal totalIngresos;

    @Money
    @DefaultValueCalculator(value = CalculadorTotalGastos.class)
    private BigDecimal totalGastos;

    @Money
    @ReadOnly
    @DefaultValueCalculator(value = CalculadorBalance.class)
    private BigDecimal balance;

}