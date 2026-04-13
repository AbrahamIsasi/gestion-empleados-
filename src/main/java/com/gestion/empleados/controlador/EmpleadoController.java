package com.gestion.empleados.controlador;

import java.io.IOException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.gestion.empleados.entidades.Sede;
import com.gestion.empleados.entidades.Supervisor;
import com.gestion.empleados.security.CustomUserDetails;
import com.gestion.empleados.servicio.SedeService;
import com.gestion.empleados.servicio.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestion.empleados.entidades.Empleado;
import com.gestion.empleados.servicio.EmpleadoService;
import com.gestion.empleados.util.paginacion.PageRender;
import com.gestion.empleados.util.reportes.EmpleadoExporterExcel;
import com.gestion.empleados.util.reportes.EmpleadoExporterPDF;
import com.lowagie.text.DocumentException;
import org.springframework.security.core.Authentication;

@Controller
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private SedeService sedeService;

    @Autowired
    private SupervisorService supervisorService;


    private Long obtenerSedeIdPorUsuario() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si entra el usuario programador → siempre sede 1
        if (auth.getName().equals("programador")) {
            return 1L;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) principal;
            return user.getSupervisor().getSede().getIdSede();
        }

        return null; // FALLBACK (no debería suceder)
    }

    // === VER DETALLES ===
    @GetMapping("/ver/{id}")
    public String verDetallesDelEmpleado(@PathVariable Long id,
                                         Map<String, Object> modelo,
                                         RedirectAttributes flash) {

        Empleado empleado = empleadoService.findOne(id);

        if (empleado == null) {
            flash.addFlashAttribute("error", "El empleado no existe en la base de datos");
            return "redirect:/listar";
        }

        modelo.put("empleado", empleado);
        modelo.put("titulo", "Detalles del empleado " + empleado.getNombreCompleto());

        return "ver";
    }

@GetMapping({"/", "/listar", ""})
public String listarEmpleados(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(required = false) Long sede,
                              @RequestParam(name = "supervisor", required = false) Long supervisorId,
                              Model modelo,
                              Principal principal) {

    if (principal == null) {
        return "redirect:/login";
    }

    String username = principal.getName();
    Pageable pageable = PageRequest.of(page, 50);
    Page<Empleado> empleados;

    // =====================
    // VARIABLES DE RESUMEN
    // =====================
    long totalAgentes = 0;
    long agentesActivos = 0;
    long agentesBaja = 0;

    // PROGRAMADOR → puede elegir sede
    if (username.equals("programador")) {

        Long sedeId = (sede != null) ? sede : 1L;

        empleados = empleadoService.findBySedeId(sedeId, pageable);

        // 👉 RESUMEN SOLO SI ELIGE SUPERVISOR
        if (supervisorId != null && supervisorId > 0) {
            totalAgentes = empleadoService.contarPorSupervisorYSede(supervisorId, sedeId);
            agentesActivos = empleadoService.contarActivosPorSupervisorYSede(supervisorId, sedeId);
            agentesBaja = empleadoService.contarBajaPorSupervisorYSede(supervisorId, sedeId);
        }

        modelo.addAttribute("sedes", sedeService.findAll());
        modelo.addAttribute("sedeSeleccionada", sedeId);
        modelo.addAttribute("supervisorSeleccionado", supervisorId);
        modelo.addAttribute("titulo", "Listado general de empleados");

    } else {

        // SI SEDE VIENE VACÍO → TOMAR SEDE DEL USUARIO
        Long sedeId = (sede != null) ? sede : obtenerSedeIdPorUsuario();

        // Filtrar por supervisor
        if (supervisorId != null && supervisorId > 0) {
            List<Empleado> lista = empleadoService.findBySupervisorAndSede(supervisorId, sedeId);
            empleados = new PageImpl<>(lista, pageable, lista.size());

            // 👉 RESUMEN POR SUPERVISOR
            totalAgentes = empleadoService.contarPorSupervisorYSede(supervisorId, sedeId);
            agentesActivos = empleadoService.contarActivosPorSupervisorYSede(supervisorId, sedeId);
            agentesBaja = empleadoService.contarBajaPorSupervisorYSede(supervisorId, sedeId);

        } else {
            empleados = empleadoService.findBySedeId(sedeId, pageable);
        }

        modelo.addAttribute("supervisores", supervisorService.listarPorSede(sedeId));
        modelo.addAttribute("sedeSeleccionada", sedeId);
        modelo.addAttribute("supervisorSeleccionado", supervisorId);
        modelo.addAttribute("titulo", "Listado de empleados");
    }

    // =====================
    // ENVIAR A LA VISTA
    // =====================
    modelo.addAttribute("empleados", empleados);
    modelo.addAttribute("page", new PageRender<>("/listar", empleados));

    modelo.addAttribute("totalAgentes", totalAgentes);
    modelo.addAttribute("agentesActivos", agentesActivos);
    modelo.addAttribute("agentesBaja", agentesBaja);

    return "listar";
}


    // === FORMULARIO ===
    @GetMapping("/form")
    public String mostrarFormulario(@RequestParam(required = false) Long supervisor,
                                    @RequestParam(required = false) Long sede,
                                    Model modelo,
                                    Principal principal) {

        String username = principal.getName();

        Long sedeId = (sede != null) ? sede : obtenerSedeIdPorUsuario();

        Empleado empleado = new Empleado();
        List<Supervisor> supervisores = supervisorService.listarPorSede(sedeId);

        // Preseleccionar supervisor si viene por URL
        if (supervisor != null) {
            supervisores.stream()
                    .filter(s -> s.getIdSupervisor().equals(supervisor))
                    .findFirst()
                    .ifPresent(empleado::setSupervisor);
        }

        modelo.addAttribute("empleado", empleado);
        modelo.addAttribute("supervisores", supervisores);
        modelo.addAttribute("sedeSeleccionada", sedeId);
        modelo.addAttribute("titulo", "Registro de empleados");

        if (username.equals("programador")) {
            modelo.addAttribute("sedes", sedeService.findAll());
        }

        return "form";
    }


    // === GUARDAR ===
    @PostMapping("/form")
    public String guardarEmpleado(@Valid @ModelAttribute("empleado") Empleado empleado,
                                  BindingResult result,
                                  @RequestParam("sede") Long sedeId,
                                  Model modelo,
                                  RedirectAttributes flash,
                                  SessionStatus status) {

        if (result.hasErrors()) {
            modelo.addAttribute("supervisores", supervisorService.listarPorSede(sedeId));
            modelo.addAttribute("titulo", "Registro de empleados");
            return "form";
        }

        // Supervisor correcto
        Supervisor sup = supervisorService.obtenerPorId(empleado.getSupervisor().getIdSupervisor());
        empleado.setSupervisor(sup);

        // Sede correcta
        Sede sede = sedeService.findById(sedeId)
                .orElseThrow(() -> new RuntimeException("No se encontró la sede"));
        empleado.setSede(sede);

        empleadoService.save(empleado);
        status.setComplete();

        flash.addFlashAttribute("success", "Empleado guardado correctamente");

        return "redirect:/listar?sede=" + sedeId +
                "&supervisor=" + empleado.getSupervisor().getIdSupervisor();
    }


    // === EDITAR ===
    @GetMapping("/form/{id}")
    public String editarEmpleado(@PathVariable Long id,
                                 Model modelo,
                                 RedirectAttributes flash,
                                 Principal principal) {

        Empleado empleado = empleadoService.findOne(id);

        if (empleado == null) {
            flash.addFlashAttribute("error", "El empleado no existe");
            return "redirect:/listar";
        }

        Long sedeId = empleado.getSede().getIdSede(); // IMPORTANTÍSIMO

        modelo.addAttribute("empleado", empleado);
        modelo.addAttribute("titulo", "Edición de empleado");
        modelo.addAttribute("supervisores", supervisorService.listarPorSede(sedeId));
        modelo.addAttribute("sedeSeleccionada", sedeId); // NECESARIO PARA EL HIDDEN

        // Programador puede ver sedes
        if (principal.getName().equals("programador")) {
            modelo.addAttribute("sedes", sedeService.findAll());
        }

        return "form";
    }


@GetMapping("/eliminar/{id}")
public String eliminarCliente(@PathVariable Long id,
                              @RequestParam(name = "sedeId", required = false) Long sedeId,
                              RedirectAttributes flash) {

    if (id > 0) {
        empleadoService.delete(id);
        flash.addFlashAttribute("success", "Empleado eliminado con éxito");
    }

    // Si no vino sedeId, no romper
    if (sedeId == null) {
        return "redirect:/listar";
    }

    return "redirect:/listar?sede=" + sedeId;
}


    @GetMapping("/exportarPDF")
	public void exportarListadoDeEmpleadosEnPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());

		String cabecera = "Content-Disposition";
		String valor = "attachment; filename=Empleados_" + fechaActual + ".pdf";

		response.setHeader(cabecera, valor);

		List<Empleado> empleados = empleadoService.findAll();

		EmpleadoExporterPDF exporter = new EmpleadoExporterPDF(empleados);
		exporter.exportar(response);
	}

	@GetMapping("/exportarExcel")
	public void exportarListadoDeEmpleadosEnExcel(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/octet-stream");

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());

		String cabecera = "Content-Disposition";
		String valor = "attachment; filename=Empleados_" + fechaActual + ".xlsx";

		response.setHeader(cabecera, valor);

		List<Empleado> empleados = empleadoService.findAll();

		EmpleadoExporterExcel exporter = new EmpleadoExporterExcel(empleados);
		exporter.exportar(response);
	}
}
