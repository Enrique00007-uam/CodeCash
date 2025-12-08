package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
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

    @Money
    @ReadOnly
    private BigDecimal saldoTotal;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @ListProperties("fecha, categoria.nombre, monto, concepto")
    private Collection<Ingreso> ingresos;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @ListProperties("fecha, categoria.nombre, monto, concepto")
    private Collection<Gasto> gastos;

    public void actualizarSaldo() {
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        try {
            String jpqlIngresos = "SELECT SUM(i.monto) FROM Ingreso i WHERE i.cuenta.id = :cuentaId";
            Query queryIngresos = XPersistence.getManager().createQuery(jpqlIngresos);
            queryIngresos.setParameter("cuentaId", this.getId());
            queryIngresos.setFlushMode(FlushModeType.COMMIT);

            Object resIngresos = queryIngresos.getSingleResult();
            if (resIngresos != null) totalIngresos = (BigDecimal) resIngresos;

            String jpqlGastos = "SELECT SUM(g.monto) FROM Gasto g WHERE g.cuenta.id = :cuentaId";
            Query queryGastos = XPersistence.getManager().createQuery(jpqlGastos);
            queryGastos.setParameter("cuentaId", this.getId());
            queryGastos.setFlushMode(FlushModeType.COMMIT);

            Object resGastos = queryGastos.getSingleResult();
            if (resGastos != null) totalGastos = (BigDecimal) resGastos;

        } catch (Exception e) {
            e.printStackTrace();
        }

        BigDecimal inicial = this.SaldoInicial != null ? this.SaldoInicial : BigDecimal.ZERO;
        this.saldoTotal = inicial.add(totalIngresos).subtract(totalGastos);
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getSaldoTotal() {
        return saldoTotal;
    }

    public BigDecimal getSaldoInicial() {
        return SaldoInicial;
    }

    public Collection<Ingreso> getIngresos() {
        return ingresos;
    }

    public Collection<Gasto> getGastos() {
        return gastos;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSaldoTotal(BigDecimal saldoTotal) {
        this.saldoTotal = saldoTotal;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        SaldoInicial = saldoInicial;
    }
}