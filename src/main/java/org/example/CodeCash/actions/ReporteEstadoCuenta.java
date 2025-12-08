package org.example.CodeCash.actions;

import java.util.*;
import java.math.BigDecimal;
import javax.persistence.Query;

import org.example.CodeCash.model.Cuenta;
import org.example.CodeCash.model.Ingreso;
import org.example.CodeCash.model.Gasto;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.jpa.XPersistence;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

public class ReporteEstadoCuenta extends JasperReportBaseAction {

    private Cuenta cuenta;
    private List<Map<String, Object>> listaMovimientos = new ArrayList<>();

    @Override
    public void execute() throws Exception {
        Map key = (Map) getView().getKeyValues();

        if (key == null || key.isEmpty()) {
            addError("Primero selecciona una cuenta.");
            return;
        }

        try {
            this.cuenta = (Cuenta) MapFacade.findEntity("Cuenta", key);
        } catch (Exception e) {
            addError("No se pudo cargar la cuenta.");
            return;
        }

        if (this.cuenta == null) {
            addError("No se encontró la cuenta.");
            return;
        }

        cargarMovimientos();
        super.execute();
    }

    private void cargarMovimientos() {
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        String jpqlIngresos = "SELECT i FROM Ingreso i WHERE i.cuenta.id = :cuentaId";
        Query qIngresos = XPersistence.getManager().createQuery(jpqlIngresos);
        qIngresos.setParameter("cuentaId", cuenta.getId());
        List<Ingreso> ingresos = qIngresos.getResultList();

        for (Ingreso i : ingresos) {
            Map<String, Object> map = new HashMap<>();
            map.put("fecha", java.sql.Date.valueOf(i.getFecha()));
            map.put("concepto", i.getConcepto());
            map.put("categoria", i.getCategoria() != null ? i.getCategoria().getNombre() : "Sin Categoría");
            map.put("tipo", "INGRESO");
            map.put("monto", i.getMonto());
            listaMovimientos.add(map);

            if(i.getMonto() != null) totalIngresos = totalIngresos.add(i.getMonto());
        }

        String jpqlGastos = "SELECT g FROM Gasto g WHERE g.cuenta.id = :cuentaId";
        Query qGastos = XPersistence.getManager().createQuery(jpqlGastos);
        qGastos.setParameter("cuentaId", cuenta.getId());
        List<Gasto> gastos = qGastos.getResultList();

        for (Gasto g : gastos) {
            Map<String, Object> map = new HashMap<>();
            map.put("fecha", java.sql.Date.valueOf(g.getFecha()));
            map.put("concepto", g.getConcepto());
            map.put("categoria", g.getCategoria() != null ? g.getCategoria().getNombre() : "Sin Categoría");
            map.put("tipo", "GASTO");
            map.put("monto", g.getMonto());
            listaMovimientos.add(map);

            if(g.getMonto() != null) totalGastos = totalGastos.add(g.getMonto());
        }

        Collections.sort(listaMovimientos, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                Date d1 = (Date) m1.get("fecha");
                Date d2 = (Date) m2.get("fecha");
                return d2.compareTo(d1);
            }
        });

        this.cuenta.setSaldoTotal(cuenta.getSaldoInicial().add(totalIngresos).subtract(totalGastos));
    }

    @Override
    protected JRDataSource getDataSource() throws Exception {
        return new JRBeanCollectionDataSource(listaMovimientos);
    }

    @Override
    protected String getJRXML() throws Exception {
        return "reportes/ReporteEstadoCuenta.jrxml";
    }

    @Override
    protected Map getParameters() throws Exception {
        BigDecimal tIng = BigDecimal.ZERO;
        BigDecimal tGas = BigDecimal.ZERO;

        for(Map<String, Object> m : listaMovimientos) {
            String tipo = (String) m.get("tipo");
            BigDecimal monto = (BigDecimal) m.get("monto");
            if("INGRESO".equals(tipo)) tIng = tIng.add(monto);
            else if("GASTO".equals(tipo)) tGas = tGas.add(monto);
        }

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("tituloReporte", "Estado de Cuenta Detallado");
        parameters.put("fechaImpresion", new Date());
        parameters.put("p_nombreCuenta", cuenta.getNombre());

        BigDecimal sInicial = cuenta.getSaldoInicial() != null ? cuenta.getSaldoInicial() : BigDecimal.ZERO;
        BigDecimal sActual = cuenta.getSaldoTotal() != null ? cuenta.getSaldoTotal() : BigDecimal.ZERO;
        BigDecimal flujo = sActual.subtract(sInicial);

        parameters.put("p_saldoInicial", sInicial);
        parameters.put("p_saldoActual", sActual);
        parameters.put("p_flujoNeto", flujo);

        parameters.put("p_ingresos", tIng);
        parameters.put("p_gastos", tGas);
        parameters.put("p_saldoAcumulado", sInicial.add(tIng));

        return parameters;
    }
}