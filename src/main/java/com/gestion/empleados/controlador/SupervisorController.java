package com.gestion.empleados.controlador;

import com.gestion.empleados.entidades.Supervisor;
import com.gestion.empleados.entidades.Sede;
import com.gestion.empleados.servicio.SupervisorService;
import com.gestion.empleados.servicio.SedeService;
import com.gestion.empleados.servicio.SedeServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/supervisores")
public class SupervisorController {



    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private SedeService sedeService;

    // --- LISTAR SUPERVISORES ---
    @GetMapping
    public String listarSupervisores(Model modelo) {
        List<Supervisor> supervisores = supervisorService.listarTodos();
        modelo.addAttribute("supervisores", supervisores);
        return "supervisores"; // Vista supervisores.html
    }

    // --- FORMULARIO NUEVO SUPERVISOR ---
    @GetMapping("/nuevo")
    public String nuevoSupervisor(Model modelo) {
        Supervisor supervisor = new Supervisor();
        List<Sede> sedes = sedeService.listarTodas();

        modelo.addAttribute("supervisor", supervisor);
        modelo.addAttribute("sedes", sedes);
        return "formSupervisor"; // Vista para crear/editar
    }

    // --- GUARDAR SUPERVISOR ---
    @PostMapping("/guardar")
    public String guardarSupervisor(@ModelAttribute("supervisor") Supervisor supervisor,
                                    RedirectAttributes redirectAttributes) {
        supervisorService.guardar(supervisor);
        redirectAttributes.addFlashAttribute("exito", "Supervisor guardado correctamente.");
        return "redirect:/supervisores";
    }

    // --- EDITAR SUPERVISOR ---
    @GetMapping("/editar/{id}")
    public String editarSupervisor(@PathVariable Long id, Model modelo, RedirectAttributes redirectAttributes) {
        Supervisor supervisor = supervisorService.obtenerPorId(id);
        if (supervisor == null) {
            redirectAttributes.addFlashAttribute("error", "Supervisor no encontrado.");
            return "redirect:/supervisores";
        }

        List<Sede> sedes = sedeService.listarTodas();
        modelo.addAttribute("supervisor", supervisor);
        modelo.addAttribute("sedes", sedes);
        return "formSupervisor";
    }

    // --- ELIMINAR SUPERVISOR ---
    @GetMapping("/eliminar/{id}")
    public String eliminarSupervisor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            supervisorService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Supervisor eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el supervisor.");
        }
        return "redirect:/supervisores";
    }

    // --- OBTENER SUPERVISORES POR SEDE (para combos dinámicos o AJAX) ---
    @GetMapping("/por-sede/{sedeId}")
    @ResponseBody
    public List<Supervisor> obtenerSupervisoresPorSede(@PathVariable Long sedeId) {
        return supervisorService.listarPorSede(sedeId);
    }
}
