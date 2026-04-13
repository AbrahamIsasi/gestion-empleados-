package com.gestion.empleados.repositorios;

import com.gestion.empleados.entidades.Supervisor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gestion.empleados.entidades.Empleado;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmpleadoRepository extends PagingAndSortingRepository<Empleado, Long> {

    // 1) Paginado por sede
    Page<Empleado> findBySede_IdSede(Long idSede, Pageable pageable);

    // 2) Lista completa por sede
    List<Empleado> findBySede_IdSede(Long idSede);

    // 3) Por sede con @Query
    @Query("SELECT e FROM Empleado e WHERE e.sede.idSede = :sedeId")
    List<Empleado> findBySedeId(@Param("sedeId") Long sedeId);

    // 4) Empleados que tienen supervisor asignado
    @Query("SELECT e FROM Empleado e WHERE e.supervisor = true ")
    List<Empleado> findSupervisores();

    // 5) Filtrar por supervisor + sede
    @Query("SELECT e FROM Empleado e WHERE e.supervisor.idSupervisor = :supervisorId AND e.sede.idSede = :sedeId")
    List<Empleado> findBySupervisorAndSede(
            @Param("supervisorId") Long supervisorId,
            @Param("sedeId") Long sedeId
    );

    // 6) Alternativa usando nombres de atributos
    List<Empleado> findBySupervisor_IdSupervisorAndSede_IdSede(Long supervisorId, Long sedeId);

    // 7) Buscar empleados por supervisor (CORRECTO)
    List<Empleado> findBySupervisor_IdSupervisor(Long supervisorId);

    // 8) Supervisores por sede
    @Query("SELECT DISTINCT e.supervisor FROM Empleado e WHERE e.supervisor IS NOT NULL AND e.sede.idSede = :sedeId")
    List<Supervisor> findSupervisoresPorSede(@Param("sedeId") Long sedeId);

    // =====================
// CONTADORES (RESUMEN) AÑADIDO RECIEN 02/01
// =====================

    // Total de agentes por supervisor y sede
    @Query("SELECT COUNT(e) " +
            "FROM Empleado e " +
            "WHERE e.supervisor.idSupervisor = :supervisorId " +
            "AND e.sede.idSede = :sedeId")
    long countBySupervisorAndSede(
            @Param("supervisorId") Long supervisorId,
            @Param("sedeId") Long sedeId
    );

    // Agentes ACTIVOS
    @Query("SELECT COUNT(e) " +
            "FROM Empleado e " +
            "WHERE e.supervisor.idSupervisor = :supervisorId " +
            "AND e.sede.idSede = :sedeId " +
            "AND e.tipoEstado = true")
    long countActivosBySupervisorAndSede(
            @Param("supervisorId") Long supervisorId,
            @Param("sedeId") Long sedeId
    );

    // Agentes EN BAJA
    @Query("SELECT COUNT(e) " +
            "FROM Empleado e " +
            "WHERE e.supervisor.idSupervisor = :supervisorId " +
            "AND e.sede.idSede = :sedeId " +
            "AND e.tipoEstado = false")
    long countBajaBySupervisorAndSede(
            @Param("supervisorId") Long supervisorId,
            @Param("sedeId") Long sedeId
    );

}


