package com.gestion.empleados.servicio;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.empleados.entidades.Asistencia;
import com.gestion.empleados.entidades.Empleado;
import com.gestion.empleados.repositorios.AsistenciaRepository;

@Service
public class AsistenciaServiceImpl implements AsistenciaService{

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Asistencia> findAll() {
        return asistenciaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Asistencia findOne(Long id) {
        return asistenciaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(Asistencia asistencia) {
        asistenciaRepository.save(asistencia);
    }

    @Transactional
    @Override
    public void guardarAsistenciaUnica(Asistencia asistencia) {

        boolean existe = asistenciaRepository
                .existsByEmpleadoIdAndFecha(
                        asistencia.getEmpleado().getId(),
                        asistencia.getFecha()
                );

        if (existe) {
            throw new RuntimeException(
                    "El empleado " +
                            asistencia.getEmpleado().getNombreCompleto() +
                            " ya tiene asistencia registrada hoy"
            );
        }

        asistenciaRepository.save(asistencia);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        asistenciaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asistencia> findByEmpleado(Empleado empleado) {
        return asistenciaRepository.findByEmpleado(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asistencia> findByEmpleadoAndFechaBetween(Empleado empleado, Date inicio, Date fin) {
        return asistenciaRepository.findByEmpleadoAndFechaBetween(empleado, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asistencia> findByFecha(Date fecha) {
        return asistenciaRepository.findByFecha(fecha);
    }

    @Override
    public List<Asistencia> obtenerPorSedeYMes(Long sedeId, Date inicio, Date fin) {
        return asistenciaRepository.obtenerPorSedeYMes(sedeId, inicio, fin);
    }
}
