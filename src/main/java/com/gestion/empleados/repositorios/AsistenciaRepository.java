package com.gestion.empleados.repositorios;

import com.gestion.empleados.entidades.Asistencia;
import com.gestion.empleados.entidades.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    // Buscar todas las asistencias de un empleado
    List<Asistencia> findByEmpleado(Empleado empleado);

    boolean existsByEmpleadoIdAndFecha(Long empleadoId, Date fecha);

    // Buscar asistencias de un empleado en un rango de fechas
    List<Asistencia> findByEmpleadoAndFechaBetween(Empleado empleado, Date inicio, Date fin);

    // Buscar todas las asistencias de una fecha específica
    List<Asistencia> findByFecha(Date fecha);

    @Query(
            "SELECT a " +
                    "FROM Asistencia a " +
                    "JOIN a.empleado e " +
                    "JOIN e.supervisor s " +
                    "WHERE e.sede.idSede = :sedeId " +
                    "AND a.fecha BETWEEN :inicio AND :fin"
    )
    List<Asistencia> obtenerPorSedeYMes(
            @Param("sedeId") Long sedeId,
            @Param("inicio") Date inicio,
            @Param("fin") Date fin
    );



}
