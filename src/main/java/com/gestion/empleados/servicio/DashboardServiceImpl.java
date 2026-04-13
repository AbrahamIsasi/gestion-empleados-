package com.gestion.empleados.servicio;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import java.io.OutputStream;
import com.gestion.empleados.entidades.Asistencia;
import com.gestion.empleados.entidades.Empleado;
import com.gestion.empleados.entidades.Supervisor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.OutputStream;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private AsistenciaService asistenciaService;

    @Override
    @Transactional(readOnly = true)
    public void exportarAsistenciaExcel(
            Long sedeId,
            int mes,
            int anio,
            OutputStream outputStream
    ) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Asistencia");

            // =========================
            // ESTILOS
            // =========================
            CellStyle tituloStyle = workbook.createCellStyle();
            Font tituloFont = workbook.createFont();
            tituloFont.setBold(true);
            tituloFont.setFontHeightInPoints((short) 14);
            tituloStyle.setFont(tituloFont);
            tituloStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle supervisorStyle = workbook.createCellStyle();
            Font supFont = workbook.createFont();
            supFont.setBold(true);
            supervisorStyle.setFont(supFont);

            CellStyle cellStyle = crearEstiloSimple(workbook);

            // =========================
            // FECHAS
            // =========================
            YearMonth yearMonth = YearMonth.of(anio, mes);
            int diasMes = yearMonth.lengthOfMonth();

            // =========================
            // DATOS
            // =========================
            List<Supervisor> supervisores = supervisorService.listarPorSede(sedeId);
            List<Empleado> empleados = empleadoService.findBySedeId(sedeId);

            List<Asistencia> asistencias = asistenciaService.obtenerPorSedeYMes(
                    sedeId,
                    java.sql.Date.valueOf(yearMonth.atDay(1)),
                    java.sql.Date.valueOf(yearMonth.atEndOfMonth())
            );

            // =========================
            // TITULO GENERAL
            // =========================
            String nombreSede = supervisores.isEmpty()
                    ? "SIN SEDE"
                    : supervisores.get(0).getSede().getNombre();

            Row tituloRow = sheet.createRow(0);
            tituloRow.setHeightInPoints(28);

            Cell tituloCell = tituloRow.createCell(0);
            tituloCell.setCellValue(
                    "REPORTE DE ASISTENCIA – SEDE " + nombreSede +
                            " (" + String.format("%02d", mes) + "-" + anio + ")"
            );
            tituloCell.setCellStyle(tituloStyle);

            sheet.addMergedRegion(
                    new CellRangeAddress(
                            0,
                            0,
                            0,
                            diasMes + 2
                    )
            );

            int rowNum = 2; // dejamos una fila en blanco

            for (Supervisor sup : supervisores) {

                // ===== SUPERVISOR =====
                Row supRow = sheet.createRow(rowNum++);
                supRow.setHeightInPoints(22);

                Cell supCell = supRow.createCell(0);
                supCell.setCellValue(
                        "SUPERVISOR: " + sup.getNombre() + "  " + sup.getApellido()
                );
                supCell.setCellStyle(supervisorStyle);

                sheet.addMergedRegion(
                        new CellRangeAddress(
                                supRow.getRowNum(),
                                supRow.getRowNum(),
                                0,
                                diasMes + 2
                        )
                );

                // ===== HEADER =====
                Row header = sheet.createRow(rowNum++);
                header.setHeightInPoints(22);

                header.createCell(0).setCellValue("AGENTE");
                header.createCell(1).setCellValue("DNI");

                for (int d = 1; d <= diasMes; d++) {
                    header.createCell(d + 1).setCellValue(d);
                }

                header.createCell(diasMes + 2).setCellValue("FALTAS");

                for (Cell c : header) {
                    c.setCellStyle(headerStyle);
                }

                // ===== EMPLEADOS =====
                for (Empleado emp : empleados) {

                    if (emp.getSupervisor() == null ||
                            !emp.getSupervisor().getIdSupervisor()
                                    .equals(sup.getIdSupervisor())) {
                        continue;
                    }

                    Row row = sheet.createRow(rowNum++);
                    row.setHeightInPoints(20);

                    row.createCell(0).setCellValue(emp.getNombreCompleto());
                    row.createCell(1).setCellValue(emp.getDni());

                    int faltas = 0;

                    for (int d = 1; d <= diasMes; d++) {

                        Cell cell = row.createCell(d + 1);
                        cell.setCellStyle(cellStyle);

                        for (Asistencia a : asistencias) {

                            if (!a.getEmpleado().getId().equals(emp.getId())) {
                                continue;
                            }

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(a.getFecha());

                            if (cal.get(Calendar.DAY_OF_MONTH) == d) {

                                String estado = a.getEstadoAsistencia().name();
                                String codigo = obtenerCodigoAsistencia(estado);

                                cell.setCellValue(codigo);

                                if (estado.contains("FALTA")) {
                                    faltas++;
                                }
                            }
                        }
                    }

                    Cell fCell = row.createCell(diasMes + 2);
                    fCell.setCellValue(faltas);
                    fCell.setCellStyle(cellStyle);
                }
            }

            // =========================
            // ANCHOS FIJOS
            // =========================
            sheet.setColumnWidth(0, 8000); // AGENTE
            sheet.setColumnWidth(1, 3000); // DNI

            for (int i = 2; i <= diasMes + 1; i++) {
                sheet.setColumnWidth(i, 1750);
            }

            sheet.setColumnWidth(diasMes + 2, 1750); // FALTAS

            workbook.write(outputStream);

        } catch (Exception e) {
            throw new RuntimeException("Error al exportar Excel", e);
        }
    }

    private String obtenerCodigoAsistencia(String estado) {

        switch (estado) {
            case "ASISTIO": return "A";
            case "CESADO": return "C";
            case "COMISIONISTA": return "COM";
            case "DESCANSO_MEDICO": return "DM";
            case "FALTA_1_DIA_DESCUENTO": return "F1";
            case "FALTA_2_DIAS_DESCUENTO": return "FF";
            case "LICENCIA_SIN_GOCE": return "LSG";
            case "PAGO_1_DIA": return "P1D";
            case "PAGO_DOBLE": return "PD";
            case "FALLECIMIENTO_FAMILIAR_DIRECTO": return "FFD";
            case "PAGO_POR_HORAS": return "PH";
            case "VACACIONES": return "V";
            default: return "";
        }
    }

    private CellStyle crearEstiloSimple(Workbook wb) {

        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        return cs;
    }
}