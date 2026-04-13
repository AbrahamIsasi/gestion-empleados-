package com.gestion.empleados.controlador;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.stream.Collectors;

import com.gestion.empleados.entidades.EstadoAsistencia;
import com.gestion.empleados.entidades.Supervisor;
import com.gestion.empleados.security.CustomUserDetails;
import com.gestion.empleados.servicio.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.gestion.empleados.entidades.Asistencia;
import com.gestion.empleados.entidades.Empleado;
import com.gestion.empleados.servicio.AsistenciaService;
import com.gestion.empleados.servicio.EmpleadoService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;


@Controller
@RequestMapping("/asistencias")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private SupervisorService supervisorService;

    // 🔐 SEDE SEGÚN USUARIO
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

    /**
     * 📋 LISTAR ASISTENCIAS
     */
    @GetMapping
    public String listarAsistencias(
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha,
            @RequestParam(name = "sede", required = false) Long sedeSeleccionada,
            @RequestParam(name = "supervisor", required = false) Long supervisorSeleccionado,
            Model modelo,
            Principal principal) {

        if (fecha == null) {
            fecha = new Date();
        }

        Long sedeId = principal.getName().equals("programador")
                ? (sedeSeleccionada != null ? sedeSeleccionada : 1L)
                : obtenerSedeIdPorUsuario(principal);

        List<Supervisor> supervisores = supervisorService.listarPorSede(sedeId);

        List<Empleado> empleados = (supervisorSeleccionado != null)
                ? empleadoService
                .findBySupervisorIdAndSedeId(supervisorSeleccionado, sedeId)
                .stream()
                .filter(e -> Boolean.TRUE.equals(e.getTipoEstado()))
                .collect(Collectors.toList())
                : new ArrayList<>();

        modelo.addAttribute("supervisores", supervisores);
        modelo.addAttribute("supervisorSeleccionado", supervisorSeleccionado);
        modelo.addAttribute("empleados", empleados);
        modelo.addAttribute("asistencias", asistenciaService.findByFecha(fecha));
        modelo.addAttribute("fechaSeleccionada", fecha);
        modelo.addAttribute("sedeSeleccionada", sedeId);
        modelo.addAttribute("titulo", "Control de Asistencias");

        return "asistencias";
    }

    /**
     * 💾 GUARDAR ASISTENCIAS (UNA VEZ POR DÍA)
     */
    @PostMapping("/guardar")
    public String guardarAsistencias(
            @RequestParam Map<String, String> params,
            @RequestParam("fecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha,
            @RequestParam("supervisor") Long supervisorId,
            @RequestParam("sede") Long sedeId,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        // 🛑 SOLO EL DÍA ACTUAL
        Calendar hoy = Calendar.getInstance();
        Calendar f = Calendar.getInstance();
        f.setTime(fecha);

        if (hoy.get(Calendar.YEAR) != f.get(Calendar.YEAR) ||
                hoy.get(Calendar.MONTH) != f.get(Calendar.MONTH) ||
                hoy.get(Calendar.DAY_OF_MONTH) != f.get(Calendar.DAY_OF_MONTH)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Solo se permite registrar asistencias del día actual."
            );
            return "redirect:/asistencias";
        }

        Supervisor registradoPor = supervisorService.findById(supervisorId);

        boolean huboError = false;

        for (String key : params.keySet()) {

            if (key.startsWith("estado_")) {

                Long empleadoId = Long.parseLong(key.replace("estado_", ""));
                String estado = params.get(key);

                Empleado empleado = empleadoService.obtenerPorId(empleadoId);
                if (empleado == null) continue;

                Asistencia asistencia = new Asistencia();
                asistencia.setEmpleado(empleado);
                asistencia.setFecha(fecha);
                asistencia.setEstadoAsistencia(EstadoAsistencia.valueOf(estado));
                asistencia.setRegistradoPor(registradoPor);

                try {
                    // 🔒 REGLA FUERTE: UNA ASISTENCIA POR DÍA
                    asistenciaService.guardarAsistenciaUnica(asistencia);
                } catch (RuntimeException e) {
                    huboError = true;
                }
            }
        }

        if (huboError) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    " ⚠ Algunos empleados ya tenían asistencia registrada hoy. En todo caso comunicate con el área de desarrollo."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "exito",
                    "Asistencias registradas correctamente."
            );
        }

        return "redirect:/asistencias";
    }
}


