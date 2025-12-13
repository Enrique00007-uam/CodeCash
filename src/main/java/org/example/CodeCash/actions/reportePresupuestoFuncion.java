package org.example.CodeCash.actions;

import java.util.*;
import java.math.BigDecimal;
import javax.persistence.Query;

import org.example.CodeCash.model.Presupuesto;
import org.example.CodeCash.model.Gasto;
import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.jpa.XPersistence;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;

public class reportePresupuestoFuncion extends JasperReportBaseAction {

    private Presupuesto presupuesto;
    // Lista de Mapas: Es la forma más compatible para evitar errores de serialización de objetos complejos
    private List<Map<String, Object>> listaGastosDetalle = new ArrayList<>();

    @Override
    public void execute() throws Exception {
        Map key = (Map) getView().getKeyValues();

        // Validación: Asegurar que hay un presupuesto seleccionado
        if (key == null || key.isEmpty()) {
            addError("Primero selecciona un presupuesto.");
            return;
        }

        try {
            this.presupuesto = (Presupuesto) MapFacade.findEntity("Presupuesto", key);
        } catch (Exception e) {
            addError("No se pudo cargar el presupuesto.");
            return;
        }

        if (this.presupuesto == null) {
            addError("No se encontró el presupuesto.");
            return;
        }

        cargarGastosRelacionados();
        super.execute();
    }

    private void cargarGastosRelacionados() {
        if (presupuesto.getCategoria() == null) return;

        // Consulta JPQL para obtener solo los gastos del mes/año y categoría del presupuesto
        String jpql = "SELECT g FROM Gasto g WHERE g.categoria.id = :catId " +
                "AND YEAR(g.fecha) = :anio AND MONTH(g.fecha) = :mes ORDER BY g.fecha DESC";

        Query query = XPersistence.getManager().createQuery(jpql);
        query.setParameter("catId", presupuesto.getCategoria().getId());
        query.setParameter("anio", presupuesto.getAnio());
        query.setParameter("mes", presupuesto.getMes());

        List<Gasto> gastos = query.getResultList();

        // Transformación de datos a Map para JasperReports
        for (Gasto g : gastos) {
            Map<String, Object> map = new HashMap<>();
            map.put("concepto", g.getConcepto());
            map.put("monto", g.getMonto());
            // Conversión explícita a java.sql.Date para máxima compatibilidad
            map.put("fecha", java.sql.Date.valueOf(g.getFecha()));
            // Manejo de nulos en la cuenta
            map.put("cuenta", g.getCuenta() != null ? g.getCuenta().getNombre() : "Sin Cuenta");
            listaGastosDetalle.add(map);
        }
    }

    @Override
    protected JRDataSource getDataSource() throws Exception {
        return new JRBeanCollectionDataSource(listaGastosDetalle);
    }

    @Override
    protected String getJRXML() throws Exception {
        return "reportes/ReportePresupuesto.jrxml";
    }

    @Override
    protected Map getParameters() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("tituloReporte", "Reporte de Ejecución Presupuestaria");
        parameters.put("fechaImpresion", new Date());

        String nombreCategoria = (presupuesto.getCategoria() != null) ? presupuesto.getCategoria().getNombre() : "Sin Categoría";
        String periodo = presupuesto.getMes() + "/" + presupuesto.getAnio();

        parameters.put("p_categoria", nombreCategoria);
        parameters.put("p_periodo", periodo);

        // Uso de valores por defecto si los campos numéricos son nulos
        parameters.put("p_limite", presupuesto.getLimite() != null ? presupuesto.getLimite() : BigDecimal.ZERO);
        parameters.put("p_gastado", presupuesto.getGastadoReal() != null ? presupuesto.getGastadoReal() : BigDecimal.ZERO);
        parameters.put("p_disponible", presupuesto.getDisponible() != null ? presupuesto.getDisponible() : BigDecimal.ZERO);

        return parameters;
    }
}