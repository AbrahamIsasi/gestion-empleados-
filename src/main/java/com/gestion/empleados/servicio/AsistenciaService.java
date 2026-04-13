package com.gestion.empleados.servicio;

import com.gestion.empleados.entidades.Asistencia;
import com.gestion.empleados.entidades.Empleado;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface AsistenciaService {
    List<Asistencia> findAll();

    Asistencia findOne(Long id);

    void save(Asistencia asistencia);

    void guardarAsistenciaUnica(Asistencia asistencia);

    void delete(Long id);

    // Buscar todas las asistencias de un empleado
    List<Asistencia> findByEmpleado(Empleado empleado);

    // Buscar asistencias de un empleado en un rango de fechas
    List<Asistencia> findByEmpleadoAndFechaBetween(Empleado empleado, Date inicio, Date fin);

    // Buscar todas las asistencias de una fecha específica
    List<Asistencia> findByFecha(Date fecha);


    List<Asistencia> obtenerPorSedeYMes(Long sedeId, Date inicio, Date fin);


}
