package com.gestion.empleados.util.reportes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gestion.empleados.entidades.Empleado;

public class EmpleadoExporterExcel {

    private XSSFWorkbook libro;
    private XSSFSheet hoja;
    private List<Empleado> listaEmpleados;
    private String nombreSede;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Constructor con sede
    public EmpleadoExporterExcel(List<Empleado> listaEmpleados, String nombreSede) {
        this.listaEmpleados = listaEmpleados;
        this.nombreSede = (nombreSede != null && !nombreSede.isEmpty()) ? nombreSede : "Reporte General";
        libro = new XSSFWorkbook();
        hoja = libro.createSheet("Empleados - " + this.nombreSede);
    }

    // Constructor sin sede (compatibilidad)
    public EmpleadoExporterExcel(List<Empleado> listaEmpleados) {
        this(listaEmpleados, "Reporte General");
    }

    private void escribirCabeceraDeTabla() {
        // Fila 0: título principal
        Row filaTitulo = hoja.createRow(0);
        CellStyle estiloTitulo = libro.createCellStyle();
        XSSFFont fuenteTitulo = libro.createFont();
        fuenteTitulo.setBold(true);
        fuenteTitulo.setFontHeight(16);
        estiloTitulo.setFont(fuenteTitulo);

        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue(nombreSede);
        celdaTitulo.setCellStyle(estiloTitulo);

        // Fila 1: encabezados de columna
        Row filaCabecera = hoja.createRow(1);
        CellStyle estiloCabecera = libro.createCellStyle();
        XSSFFont fuenteCabecera = libro.createFont();
        fuenteCabecera.setBold(true);
        fuenteCabecera.setFontHeight(14);
        estiloCabecera.setFont(fuenteCabecera);

        String[] columnas = {
                "ID", "Nombre", "Apellido", "Email", "Fecha Ingreso", "Teléfono", "DNI", "Condición","Estado", "Sede"
        };

        for (int i = 0; i < columnas.length; i++) {
            Cell celda = filaCabecera.createCell(i);
            celda.setCellValue(columnas[i]);
            celda.setCellStyle(estiloCabecera);
        }
    }

    private void escribirDatosDeLaTabla() {
        int numFila = 2;

        CellStyle estiloDatos = libro.createCellStyle();
        XSSFFont fuente = libro.createFont();
        fuente.setFontHeight(12);
        estiloDatos.setFont(fuente);

        for (Empleado empleado : listaEmpleados) {
            Row fila = hoja.createRow(numFila++);

            crearCelda(fila, 0, empleado.getId(), estiloDatos);
            crearCelda(fila, 1, empleado.getNombreCompleto(), estiloDatos);
            crearCelda(fila, 2, empleado.getNombreCorto(), estiloDatos);
            crearCelda(fila, 3, empleado.getEmail(), estiloDatos);
            crearCelda(fila, 4, empleado.getFecha(), estiloDatos); // Date tratado en crearCelda
            crearCelda(fila, 5, empleado.getTelefono(), estiloDatos); // puede ser Long/String
            crearCelda(fila, 6, empleado.getDni(), estiloDatos);
            crearCelda(fila, 7, empleado.getCondicion(), estiloDatos); // BigDecimal / Double / Integer
            crearCelda(fila,8, empleado.getTipoEstado(), estiloDatos);
            crearCelda(fila, 9, empleado.getSede() != null ? empleado.getSede().getNombre() : "Sin sede", estiloDatos);
        }

        // Autoajustar columnas
        for (int i = 0; i < 9; i++) {
            hoja.autoSizeColumn(i);
        }
    }

    /**
     * Crea una celda y escribe de forma segura cualquier tipo de valor.
     * - Date -> formatea como yyyy-MM-dd
     * - Number/BigDecimal/etc -> se transforma a String con toString()
     * - null -> string vacío
     */
    private void crearCelda(Row fila, int columna, Object valor, CellStyle estilo) {
        Cell celda = fila.createCell(columna);
        String texto;

        if (valor == null) {
            texto = "";
        } else if (valor instanceof Date) {
            texto = DATE_FORMAT.format((Date) valor);
        } else {
            // convierte cualquier otro objeto a texto (números, BigDecimal, Long, Integer, String, etc.)
            texto = String.valueOf(valor);
        }

        celda.setCellValue(texto);
        celda.setCellStyle(estilo);
    }

    public void exportar(HttpServletResponse response) throws IOException {
        escribirCabeceraDeTabla();
        escribirDatosDeLaTabla();

        ServletOutputStream outputStream = response.getOutputStream();
        libro.write(outputStream);
        libro.close();
        outputStream.close();
    }
}
