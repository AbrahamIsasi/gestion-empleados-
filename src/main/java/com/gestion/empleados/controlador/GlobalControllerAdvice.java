package com.gestion.empleados.controlador;

import com.gestion.empleados.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("email")
    public String agregarEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated())
            return "";

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) principal;
            return user.getSupervisor().getEmail();
        }

        return auth.getName();
    }

    @ModelAttribute("sedeNombre")
    public String agregarSede() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated())
            return "";

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails user = (CustomUserDetails) principal;
            return user.getSupervisor().getSede().getNombre();
        }

        // PROGRAMADOR → siempre sede 1
        if (auth.getName().equals("programador")) {
            return "PROGRAMADOR";
        }

        return "";
    }
}