package com.gestion.empleados.controlador;

import com.gestion.empleados.entidades.Asistencia;
import com.gestion.empleados.entidades.Empleado;
import com.gestion.empleados.entidades.Supervisor;
import com.gestion.empleados.security.CustomUserDetails;
import com.gestion.empleados.servicio.AsistenciaService;
import com.gestion.empleados.servicio.DashboardService;
import com.gestion.empleados.servicio.EmpleadoService;
import com.gestion.empleados.servicio.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.stream.Collectors;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private DashboardService dashboardService;

    // 🔐 SEDE DEL USUARIO LOGUEADO
    private Long obtenerSedeIdPorUsuario(Principal principal) {

        if (principal.getName().equals("programador")) {
            return 1L;
        }

        Authentication auth = (Authentication) principal;
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        return userDetails.getSupervisor()
                .getSede()
                .getIdSede();
    }

    // 📊 DASHBOARD
    @GetMapping
    public String verDashboard(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Long sede, //agregado recien
            Model model,
            Principal principal
    ) {

        LocalDate hoy = LocalDate.now();
        int mesFinal = (mes != null) ? mes : hoy.getMonthValue();
        int anioFinal = (anio != null) ? anio : hoy.getYear();

        YearMonth yearMonth = YearMonth.of(anioFinal, mesFinal);
        LocalDate inicioMes = yearMonth.atDay(1);
        LocalDate finMes = yearMonth.atEndOfMonth();

        Long sedeId = (sede != null)    //agrgado recien
                ? sede                  //agrgado recien
                : obtenerSedeIdPorUsuario(principal);       //agrgado recien

        if (principal.getName().equals("programador")) {
            sedeId = (sede != null) ? sede : 1L;
        } else {
            sedeId = obtenerSedeIdPorUsuario(principal);
        }

        model.addAttribute("sedeSeleccionada", sedeId); //agregago recien

        List<Supervisor> supervisores = supervisorService.listarPorSede(sedeId);

        String nombreSede = supervisores.isEmpty()
                ? ""
                : supervisores.get(0).getSede().getNombre();

        model.addAttribute("nombreSede", nombreSede);

        List<Empleado> empleados = empleadoService.findBySedeId(sedeId)
                .stream()
                .filter(emp -> {

                    // ✔ Activos: siempre visibles
                    if (emp.getFechaCese() == null) {
                        return true;
                    }

                    // ✔ Cesados: solo visibles si el cese cae DENTRO del mes consultado
                    LocalDate fechaCese = emp.getFechaCese();

                    return !fechaCese.isBefore(inicioMes)
                            && !fechaCese.isAfter(finMes);
                })
                .collect(Collectors.toList());



        List<Supervisor> supervisoresConAgentes = supervisores.stream()
                .filter(sup ->
                        empleados.stream().anyMatch(emp ->
                                emp.getSupervisor() != null &&
                                        emp.getSupervisor().getIdSupervisor()
                                                .equals(sup.getIdSupervisor())
                        )
                )
                .collect(Collectors.toList());

        List<Asistencia> asistenciasMes =
                asistenciaService.obtenerPorSedeYMes(
                        sedeId,
                        java.sql.Date.valueOf(inicioMes),
                        java.sql.Date.valueOf(finMes)
                );

        Map<Long, Integer> faltasPorEmpleado = new HashMap<>();

        for (Empleado emp : empleados) {
            int faltas = (int) asistenciasMes.stream()
                    .filter(a -> a.getEmpleado().getId().equals(emp.getId()))
                    .filter(a ->
                            a.getEstadoAsistencia().name().contains("FALTA")
                                    || a.getEstadoAsistencia().name().contains("LICENCIA_SIN_GOCE")
                    )
                    .filter(a -> {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(a.getFecha());
                        return cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
                    })
                    .count();

            faltasPorEmpleado.put(emp.getId(), faltas);
        }

        model.addAttribute("supervisores", supervisoresConAgentes);
        model.addAttribute("empleados", empleados);
        model.addAttribute("asistencias", asistenciasMes);
        model.addAttribute("inicioMes", inicioMes);
        model.addAttribute("mes", mesFinal);
        model.addAttribute("anio", anioFinal);
        model.addAttribute("faltasPorEmpleado", faltasPorEmpleado);

        model.addAttribute("meses", List.of(
                Map.of("value",1,"nombre","Enero"),
                Map.of("value",2,"nombre","Febrero"),
                Map.of("value",3,"nombre","Marzo"),
                Map.of("value",4,"nombre","Abril"),
                Map.of("value",5,"nombre","Mayo"),
                Map.of("value",6,"nombre","Junio"),
                Map.of("value",7,"nombre","Julio"),
                Map.of("value",8,"nombre","Agosto"),
                Map.of("value",9,"nombre","Septiembre"),
                Map.of("value",10,"nombre","Octubre"),
                Map.of("value",11,"nombre","Noviembre"),
                Map.of("value",12,"nombre","Diciembre")
        ));

        model.addAttribute("anios",
                IntStream.rangeClosed(hoy.getYear() - 3, hoy.getYear() + 4)
                        .boxed()
                        .collect(Collectors.toList())
        );

        return "dashboard";
    }

    // ❌ MÉTODO ANTIGUO (NO SE TOCA)
    @GetMapping("/dashboard/exportar-excel")
    public void exportarExcel(
            @RequestParam int mes,
            @RequestParam int anio,
            @RequestParam(required = false) Long sede,
            HttpServletResponse response
    ) throws IOException {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Reporte_Asistencia_" + mes + "_" + anio + ".xlsx"
        );

        dashboardService.exportarAsistenciaExcel(
                sede,
                mes,
                anio,
                response.getOutputStream()
        );
    }


    @GetMapping("/exportar-excel")
    public void exportarExcelPorUsuario(
            @RequestParam int mes,
            @RequestParam int anio,
            Principal principal,
            HttpServletResponse response
    ) throws IOException {

        Long sedeId = obtenerSedeIdPorUsuario(principal);

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Asistencia_Sede_" + sedeId  + "_" + mes + "_" + anio + ".xlsx"
        );

        dashboardService.exportarAsistenciaExcel(
                sedeId,
                mes,
                anio,
                response.getOutputStream()
        );
    }
}
