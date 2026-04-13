package com.gestion.empleados.servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gestion.empleados.entidades.Supervisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.empleados.entidades.Empleado;
import com.gestion.empleados.repositorios.EmpleadoRepository;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

	@Autowired
	private EmpleadoRepository empleadoRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Empleado> findAll() {
		return (List<Empleado>) empleadoRepository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Empleado> findAll(Pageable pageable) {
		return empleadoRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		empleadoRepository.deleteById(id);
	}


	@Override
	@Transactional(readOnly = true)
	public Empleado findOne(Long id) {
		return empleadoRepository.findById(id).orElse(null);
	}

	@Override
	public Empleado obtenerPorId(Long id) {
		return empleadoRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Empleado> findBySedeId(Long sedeId, Pageable pageable) {
		return empleadoRepository.findBySede_IdSede(sedeId, pageable);
	}

	@Override
	public Page<Empleado> findBySede_IdSede(Long idSede, Pageable pageable) {
		return empleadoRepository.findBySede_IdSede(idSede, pageable);
	}

	//codigo añadido recien
	@Override
	public List<Empleado> findBySedeId(Long sedeId) {
		return empleadoRepository.findBySede_IdSede(sedeId);
	}

    @Override
    public List<Empleado> findBySupervisorAndSede(Long supervisorId, Long sedeId) {
        return empleadoRepository.findBySupervisorAndSede(supervisorId, sedeId);
    }

    @Override
    public List<Supervisor> findSupervisoresPorSede(Long sedeId) {
        return empleadoRepository.findSupervisoresPorSede(sedeId);
    }
	@Override
	public List<Empleado> findBySupervisorIdAndSedeId(Long supervisorId, Long sedeId) {
		if (supervisorId == null) return new ArrayList<>();
		if (sedeId != null) {
			return empleadoRepository.findBySupervisor_IdSupervisorAndSede_IdSede(supervisorId, sedeId);
		} else {
			return empleadoRepository.findBySupervisor_IdSupervisor(supervisorId);
		}
	}

    // =====================
// CONTADORES (RESUMEN)
// =====================

    @Override
    @Transactional(readOnly = true)
    public long contarPorSupervisorYSede(Long supervisorId, Long sedeId) {

        if (supervisorId == null || sedeId == null) {
            return 0;
        }

        return empleadoRepository.countBySupervisorAndSede(supervisorId, sedeId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarActivosPorSupervisorYSede(Long supervisorId, Long sedeId) {

        if (supervisorId == null || sedeId == null) {
            return 0;
        }

        return empleadoRepository.countActivosBySupervisorAndSede(supervisorId, sedeId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarBajaPorSupervisorYSede(Long supervisorId, Long sedeId) {

        if (supervisorId == null || sedeId == null) {
            return 0;
        }

        return empleadoRepository.countBajaBySupervisorAndSede(supervisorId, sedeId);
    }
    @Override
    @Transactional
    public void save(Empleado empleado) {

        // 👉 Si se da de baja y no tenía fecha
        if (Boolean.FALSE.equals(empleado.getTipoEstado())
                && empleado.getFechaCese() == null) {

            empleado.setFechaCese(LocalDate.now());
        }

        empleadoRepository.save(empleado);
    }
/*
    @Override
    @Transactional
    public void save(Empleado empleado) {
        empleadoRepository.save(empleado);
    }

*/




}
