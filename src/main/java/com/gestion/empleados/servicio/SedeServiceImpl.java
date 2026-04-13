package com.gestion.empleados.servicio;

import com.gestion.empleados.entidades.Sede;
import com.gestion.empleados.repositorios.SedeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class SedeServiceImpl implements SedeService{

    @Autowired
    private SedeRepository sedeRepository;

    @Override
    public List<Sede> findAll() { return sedeRepository.findAll(); }

    @Override
    public Optional<Sede> findById(Long id) { return sedeRepository.findById(id); }

    @Override
    public Sede save(Sede sede) { return sedeRepository.save(sede); }

    @Override
    public List<Sede> listarTodas() {
        return sedeRepository.findAll();  // <-- Método concreto
    }

    @Override
    public Sede findOne(Long idSede) {
        return sedeRepository.findById(idSede).orElse(null);
    }


}
