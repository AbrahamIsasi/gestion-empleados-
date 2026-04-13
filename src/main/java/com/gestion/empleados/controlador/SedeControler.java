package com.gestion.empleados.controlador;

import com.gestion.empleados.entidades.Sede;
import com.gestion.empleados.servicio.SedeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/sede")
    public class SedeControler {

        @Autowired
        private SedeService sedeService;

        @GetMapping("/listar")
        public String listar(Model model) {
            model.addAttribute("sedes", sedeService.findAll());
            return "sede/listar";
        }

        @GetMapping("/form")
        public String form(Model model) {
            model.addAttribute("sede", new Sede());
            return "sede/form";
        }

        @PostMapping("/form")
        public String guardar(Sede sede, RedirectAttributes flash) {
            sedeService.save(sede);
            flash.addFlashAttribute("success", "Sede guardada");
            return "redirect:/sede/listar";
        }
    }


