package org.example.CodeCash.actions;

import java.util.*;
import java.math.BigDecimal;
import javax.persistence.Query;

import org.example.CodeCash.model.Gasto;
import org.example.CodeCash.model.Presupuesto;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.jpa.XPersistence;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

public class reportePresupuestoFuncion extends JasperReportBaseAction {

    private Gasto gasto;

    @Override
    public void execute() throws Exception {
        Map key = (Map) getView().getKeyValues();

        if (key == null || key.isEmpty()) {
            addError("Primero selecciona o guarda un gasto.");
            return;
        }

        try {
            this.gasto = (Gasto) MapFacade.findEntity("Gasto", key);
        } catch (Exception e) {
            addError("No se pudo cargar la información del gasto.");
            return;
        }

        if (this.gasto == null) {
            addError("No se encontró el gasto especificado.");
            return;
        }

        super.execute();
    }

    @Override
    protected JRDataSource getDataSource() throws Exception {
        return new JRBeanArrayDataSource(new Object[] { gasto });
    }

    @Override
    protected String getJRXML() throws Exception {
        return "reportes/ReportePresupuesto.jrxml";
    }

    @Override
    protected Map getParameters() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        String desc = (gasto.getConcepto() != null) ? gasto.getConcepto() : "Sin descripción";
        parameters.put("descripcion", desc);

        parameters.put("fechaTransaccion", (gasto.getFecha() != null) ? java.sql.Date.valueOf(gasto.getFecha()) : new Date());

        parameters.put("fechaImpresion", new Date());

        String nombreCuenta = (gasto.getCuenta() != null) ? gasto.getCuenta().getNombre() : "Cuenta Desconocida";
        parameters.put("cuenta", nombreCuenta);

        parameters.put("numeroHoja", "1");
        parameters.put("monto", gasto.getMonto());

        String tipo = "GASTO";
        String origen = "General";

        BigDecimal colActual = BigDecimal.ZERO;
        String lblActual = "";

        BigDecimal colMeta = BigDecimal.ZERO;
        String lblMeta = "";

        BigDecimal colDiferencia = BigDecimal.ZERO;
        String lblDiferencia = "";

        Presupuesto presupuestoAsociado = buscarPresupuesto(gasto);

        if (presupuestoAsociado != null) {
            origen = "Presupuesto: " + presupuestoAsociado.getCategoria().getNombre();

            BigDecimal limite = presupuestoAsociado.getLimite();
            BigDecimal gastado = presupuestoAsociado.getGastadoReal();

            colActual = gastado;
            lblActual = "Total Gastado:";

            colMeta = limite;
            lblMeta = "Límite Mensual:";

            colDiferencia = presupuestoAsociado.getDisponible();
            lblDiferencia = "Saldo Disponible:";

        } else {
            lblActual = "Monto Gasto:";
            colActual = gasto.getMonto();
            lblMeta = "Sin Presupuesto";
            colDiferencia = BigDecimal.ZERO;
        }

        parameters.put("tipo", tipo);
        parameters.put("origen", origen);

        parameters.put("colActual", colActual);
        parameters.put("lblActual", lblActual);

        parameters.put("colMeta", colMeta);
        parameters.put("lblMeta", lblMeta);

        parameters.put("colDiferencia", colDiferencia);
        parameters.put("lblDiferencia", lblDiferencia);

        return parameters;
    }

    private Presupuesto buscarPresupuesto(Gasto g) {
        if (g.getCategoria() == null || g.getFecha() == null) return null;

        int anio = g.getFecha().getYear();
        int mes = g.getFecha().getMonthValue();
        String catId = g.getCategoria().getId();

        try {
            String jpql = "FROM Presupuesto p WHERE p.categoria.id = :catId AND p.anio = :anio AND p.mes = :mes";
            Query query = XPersistence.getManager().createQuery(jpql);
            query.setParameter("catId", catId);
            query.setParameter("anio", anio);
            query.setParameter("mes", mes);

            return (Presupuesto) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}