package com.gestion.empleados.servicio;

import com.gestion.empleados.entidades.Sede;
import com.gestion.empleados.entidades.Supervisor;

import java.util.List;

public interface SupervisorService {

    List<Supervisor> listarTodos();

    List<Supervisor> listarPorSede(Long sedeId);

    Supervisor obtenerPorId(Long id);

    Supervisor guardar(Supervisor supervisor);
    void eliminar(Long id);

    public interface SedeService {
        List<Sede> listarTodas();
        // otros métodos que tengas...
    }

    Supervisor findById(Long id);



}