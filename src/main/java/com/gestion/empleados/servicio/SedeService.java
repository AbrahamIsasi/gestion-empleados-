package com.gestion.empleados.servicio;

import com.gestion.empleados.entidades.Sede;

import java.util.List;
import java.util.Optional;

public interface SedeService {
    List<Sede> findAll();
    Optional<Sede> findById(Long idSede);
    Sede save(Sede sede);
    List<Sede> listarTodas();  // <-- Agrega esto
    Sede findOne(Long idSede);
    }






