package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.message.Message;
import org.apache.poi.hpsf.Decimal;
import org.openxava.annotations.*;
import org.openxava.calculators.BigDecimalCalculator;
import org.openxava.jpa.XPersistence;

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


    @Money
    @ReadOnly
    private BigDecimal saldoTotal;

    public void actualizarSaldo() {
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        try {
            String jpqlIngresos = "SELECT SUM(i.monto) FROM Ingreso i WHERE i.cuenta.id = :cuentaId";
            Object resIngresos = XPersistence.getManager().createQuery(jpqlIngresos)
                    .setParameter("cuentaId", this.getId())
                    .getSingleResult();
            if (resIngresos != null) totalIngresos = (BigDecimal) resIngresos;

            String jpqlGastos = "SELECT SUM(g.monto) FROM Gasto g WHERE g.cuenta.id = :cuentaId";
            Object resGastos = XPersistence.getManager().createQuery(jpqlGastos)
                    .setParameter("cuentaId", this.getId())
                    .getSingleResult();
            if (resGastos != null) totalGastos = (BigDecimal) resGastos;

        } catch (Exception e) {
            e.printStackTrace();
        }

        BigDecimal inicial = this.SaldoInicial != null ? this.SaldoInicial : BigDecimal.ZERO;
        this.saldoTotal = inicial.add(totalIngresos).subtract(totalGastos);
    }
}