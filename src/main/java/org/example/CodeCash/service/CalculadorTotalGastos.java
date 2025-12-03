package org.example.CodeCash.service;

import org.openxava.calculators.ICalculator;
import org.openxava.jpa.XPersistence;
import java.math.BigDecimal;

public class CalculadorTotalGastos implements ICalculator {

    @Override
    public Object calculate() {
        Number suma = (Number) XPersistence.getManager()
                .createQuery("select sum(g.monto) from Gasto g")
                .getSingleResult();

        return suma == null ? BigDecimal.ZERO : new BigDecimal(suma.toString());
    }
}