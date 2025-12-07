package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.openxava.annotations.*;
import org.openxava.calculators.CurrentLocalDateCalculator;
import org.openxava.jpa.XPersistence;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @PostPersist
    @PostUpdate
    @PostRemove
    public void actualizarEntidadesRelacionadas() {
        if (cuenta != null) {
            cuenta.actualizarSaldo();
        }
        actualizarPresupuesto();
    }

    private void actualizarPresupuesto() {
        if (categoria == null || fecha == null) return;

        try {
            int anio = fecha.getYear();
            int mes = fecha.getMonthValue();

            String jpql = "FROM Presupuesto p WHERE p.categoria.id = :catId AND p.anio = :anio AND p.mes = :mes";

            List<Presupuesto> presupuestos = XPersistence.getManager().createQuery(jpql)
                    .setParameter("catId", categoria.getId())
                    .setParameter("anio", anio)
                    .setParameter("mes", mes)
                    .setFlushMode(FlushModeType.COMMIT)
                    .getResultList();

            for (Presupuesto p : presupuestos) {
                p.actualizarTotales();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public CategoriaGasto getCategoria() {
        return categoria;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public String getConcepto() {
        return concepto;
    }
}