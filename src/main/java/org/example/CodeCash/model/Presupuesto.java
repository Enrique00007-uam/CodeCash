package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.XPersistence;

import javax.validation.constraints.AssertTrue;
import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
@Table(name = "presupuesto", schema = "public")
public class Presupuesto extends BaseEntity {

    @Column(length=4)
    @Required
    @DefaultValueCalculator(CurrentYearCalculator.class)
    private int anio;

    @Column(length=2)
    @Required
    @Range(min=1, max=12)
    @DefaultValueCalculator(CurrentMonthCalculator.class)
    private int mes;

    @ManyToOne(fetch = FetchType.LAZY)
    @DescriptionsList(descriptionProperties = "nombre", condition = "nombre is not null")
    @Required
    private CategoriaGasto categoria;

    @Money
    @Required
    @PositiveOrZero
    private BigDecimal limite;

    @Money
    @ReadOnly
    private BigDecimal gastadoReal;

    @AssertTrue(message = "El presupuesto no puede ser para una fecha en el pasado")
    private boolean isPeriodoValido() {
        try {
            LocalDate fechaPresupuesto = LocalDate.of(anio, mes, 1);
            LocalDate primerDiaMesActual = LocalDate.now().withDayOfMonth(1);
            return !fechaPresupuesto.isBefore(primerDiaMesActual);
        } catch (Exception e) {
            return false;
        }
    }

    public void actualizarTotales() {
        if (categoria == null) {
            this.gastadoReal = BigDecimal.ZERO;
            return;
        }

        String jpql = "SELECT SUM(g.monto) FROM Gasto g " +
                "WHERE g.categoria.id = :categoriaId " +
                "AND YEAR(g.fecha) = :anio " +
                "AND MONTH(g.fecha) = :mes ";

        try {
            Query query = XPersistence.getManager().createQuery(jpql);
            query.setParameter("categoriaId", categoria.getId());
            query.setParameter("anio", anio);
            query.setParameter("mes", mes);

            query.setFlushMode(FlushModeType.COMMIT);

            Object resultado = query.getSingleResult();
            this.gastadoReal = resultado == null ? BigDecimal.ZERO : (BigDecimal) resultado;

        } catch (Exception e) {
            e.printStackTrace();
            this.gastadoReal = BigDecimal.ZERO;
        }
    }

    @Stereotype("MONEY")
    @Depends("limite, gastadoReal")
    public BigDecimal getDisponible() {
        BigDecimal limiteSeguro = limite != null ? limite : BigDecimal.ZERO;
        BigDecimal gastadoSeguro = gastadoReal != null ? gastadoReal : BigDecimal.ZERO;
        return limiteSeguro.subtract(gastadoSeguro);
    }


    public int getAnio() {
        return anio;
    }

    public int getMes() {
        return mes;
    }

    public CategoriaGasto getCategoria() {
        return categoria;
    }

    public BigDecimal getGastadoReal() {
        return gastadoReal;
    }

    public BigDecimal getLimite() {
        return limite;
    }
}
