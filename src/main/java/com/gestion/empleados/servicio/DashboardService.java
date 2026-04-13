package com.gestion.empleados.servicio;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.OutputStream;

public interface DashboardService {


    void exportarAsistenciaExcel(
            Long sedeId,
            int mes,
            int anio,
            OutputStream outputStream
    );
}
