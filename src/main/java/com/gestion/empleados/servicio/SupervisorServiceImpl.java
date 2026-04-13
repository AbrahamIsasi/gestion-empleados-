package com.gestion.empleados.servicio;

import org.springframework.stereotype.Service;

import com.gestion.empleados.entidades.Supervisor;
import com.gestion.empleados.repositorios.SupervisorRepository;
import com.gestion.empleados.servicio.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class SupervisorServiceImpl implements SupervisorService {

    @Autowired
    private SupervisorRepository supervisorRepository;

    @Override
    public List<Supervisor> listarTodos() {
        return supervisorRepository.findByActivoTrue();
    }

    @Override
    public List<Supervisor> listarPorSede(Long sedeId) {
        // Antes: return supervisorRepository.findBySedeIdAndActivoTrue(sedeId);
        // Ahora:
        return supervisorRepository.findBySede_IdSedeAndActivoTrue(sedeId);
    }


    @Override
    public Supervisor obtenerPorId(Long id) {
        return supervisorRepository.findById(id).orElse(null);
    }


    @Override
    public void eliminar(Long id) {
        supervisorRepository.deleteById(id);
    }

    @Override
    public Supervisor guardar(Supervisor supervisor) {
        return supervisorRepository.save(supervisor);
    }
    @Override
    public Supervisor findById(Long id) {
        return supervisorRepository.findById(id).orElse(null);
    }

}
