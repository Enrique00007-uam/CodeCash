package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Users;

import javax.validation.constraints.AssertTrue;
import javax.persistence.*;
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
    @DescriptionsList(descriptionProperties = "nombre")
    @Required
    private CategoriaGasto categoria;

    @Money
    @Required
    private BigDecimal limite;


    // validacion para que la fecha ingresada sea menor a la actual

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

    // calculos
    @Stereotype("MONEY")
    public BigDecimal getGastadoReal() {
        if (categoria == null) return BigDecimal.ZERO;
        String jpql = "SELECT SUM(g.monto) FROM Gasto g " +
                "WHERE g.categoria.id = :categoriaId " +
                "AND YEAR(g.fecha) = :anio " +
                "AND MONTH(g.fecha) = :mes ";


        try {
            Query query = XPersistence.getManager().createQuery(jpql);
            query.setParameter("categoriaId", categoria.getId());
            query.setParameter("anio", anio);
            query.setParameter("mes", mes);

            Object resultado = query.getSingleResult();
            return resultado == null ? BigDecimal.ZERO : (BigDecimal) resultado;

        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Stereotype("MONEY")
    @Depends("limite")
    public BigDecimal getDisponible() {
        BigDecimal limiteSeguro = limite != null ? limite : BigDecimal.ZERO;
        return limiteSeguro.subtract(getGastadoReal());
    }
}