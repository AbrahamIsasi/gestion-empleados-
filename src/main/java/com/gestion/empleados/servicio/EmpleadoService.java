package com.gestion.empleados.servicio;

import java.util.List;

import com.gestion.empleados.entidades.Supervisor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gestion.empleados.entidades.Empleado;

public interface EmpleadoService {

	public List<Empleado> findAll();

	public Page<Empleado> findAll(Pageable pageable);

	public void save(Empleado empleado);

	public Empleado findOne(Long id);

	public void delete(Long id);

	Empleado obtenerPorId(Long empleadoId);

	Page<Empleado> findBySedeId(Long sedeId, Pageable pageable);

	Page<Empleado> findBySede_IdSede(Long idSede, Pageable pageable);

	List<Empleado> findBySedeId(Long sedeId);

    List<Empleado> findBySupervisorAndSede(Long supervisorId, Long sedeId);

    List<Supervisor> findSupervisoresPorSede(Long sedeId);

	List<Empleado> findBySupervisorIdAndSedeId(Long supervisorId, Long sedeId);

    // =====================
// CONTADORES (RESUMEN)
// =====================

    long contarPorSupervisorYSede(Long supervisorId, Long sedeId);

    long contarActivosPorSupervisorYSede(Long supervisorId, Long sedeId);

    long contarBajaPorSupervisorYSede(Long supervisorId, Long sedeId);

}
