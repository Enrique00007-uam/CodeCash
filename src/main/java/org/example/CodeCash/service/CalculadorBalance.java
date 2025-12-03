package org.example.CodeCash.service;

import org.openxava.calculators.ICalculator;
import java.math.BigDecimal;

public class CalculadorBalance implements ICalculator {

    @Override
    public Object calculate()  {
        BigDecimal ingresos = (BigDecimal) new CalculadorTotalIngresos().calculate();
        BigDecimal gastos = (BigDecimal) new CalculadorTotalGastos().calculate();

        if (ingresos == null) ingresos = BigDecimal.ZERO;
        if (gastos == null) gastos = BigDecimal.ZERO;

        return ingresos.subtract(gastos);
    }
}