package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.message.Message;
import org.apache.poi.hpsf.Decimal;
import org.openxava.annotations.*;
import org.openxava.calculators.BigDecimalCalculator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
@Getter
@Setter
@Table(name = "cuenta", schema = "public")
public class Cuenta extends BaseEntity {
    @Required
    @Column(nullable = false, length = 100)
    private String nombre;
    @Money
    @DefaultValueCalculator(BigDecimalCalculator.class)
    private BigDecimal SaldoInicial;


    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @ListProperties("fecha, categoria.nombre, monto, concepto")
    private Collection<Ingreso> ingresos;


    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @ListProperties("fecha, categoria.nombre, monto, concepto")
    private Collection<Gasto> gastos;


    @Transient
    @Money
    @Depends("saldoInicial, ingresos.monto, gastos.monto")
    public BigDecimal getSaldoTotal() {
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        if (ingresos != null) {
            totalIngresos = ingresos.stream()
                    .map(Ingreso::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (gastos != null) {
            totalGastos = gastos.stream()
                    .map(Gasto::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }


        return (SaldoInicial == null ? BigDecimal.ZERO : SaldoInicial)
                .add(totalIngresos)
                .subtract(totalGastos);
    }
}




