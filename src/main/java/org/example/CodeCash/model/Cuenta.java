package org.example.CodeCash.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.message.Message;
import org.apache.poi.hpsf.Decimal;
import org.openxava.annotations.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Cuenta extends BaseEntity {
    @Required
    @Column(nullable = false, length = 100)
    private String nombre;
    @Money
    private BigDecimal SaldoInicial;


    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @ListProperties("fecha, categoriaIngreso.nombre, monto")
    private Collection<Ingreso> ingresos;


   /* @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @ListProperties("fecha, categoriaGasto.nombre, monto")
    private Collection<Gasto> gastos;
*/




}
