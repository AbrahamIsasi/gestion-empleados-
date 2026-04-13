package com.gestion.empleados.security;

import com.gestion.empleados.entidades.Supervisor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetails implements UserDetails {

    private final Supervisor supervisor;

    public CustomUserDetails(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    // 🔥 ESTE MÉTODO ES VITAL PARA OBTENER LA SEDE DEL USUARIO
    public Long getSedeId() {
        if (supervisor.getSede() == null) {
            return null;
        }
        return supervisor.getSede().getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return java.util.Collections.singletonList((GrantedAuthority) () -> "ROLE_ADMIN");
    }

    @Override
    public String getPassword() {
        return supervisor.getPassword();
    }

    @Override
    public String getUsername() {
        return supervisor.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return supervisor.getActivo(); }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return supervisor.getActivo(); }
}

