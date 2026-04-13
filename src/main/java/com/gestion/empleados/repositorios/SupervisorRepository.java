package com.gestion.empleados.repositorios;

import com.gestion.empleados.entidades.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {


    // Supervisores activos (sin filtrar)
    List<Supervisor> findByActivoTrue();

    // Supervisores por sede (todos)
    List<Supervisor> findBySede_IdSede(Long idSede);

    // Supervisores activos por sede
    List<Supervisor> findBySede_IdSedeAndActivoTrue(Long idSede);

    Supervisor findByEmail(String email);



}
