package com.gestion.empleados.entidades;

import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String nombreCompleto;

    @NotEmpty
    private String nombreCorto;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @NotNull
    private String cargo;

    @NotNull
    private int telefono;

    @NotNull
    private String sexo;

    @NotEmpty
    private String dni;

    @NotNull
    private String condicion;

    @Column(name = "tipo_estado", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean tipoEstado = true;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @Column(name = "fecha_cese")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaCese;

    // 🔹 Relación con la tabla SEDE
    @ManyToOne
    @JoinColumn(name = "sede_id", referencedColumnName = "id_sede")
    private Sede sede;

    // 🔹 Relación con SUPERVISOR (nuevo campo)
    @ManyToOne
    @JoinColumn(name = "supervisor_id", referencedColumnName = "id_supervisor")
    private Supervisor supervisor;

    // 🔹 Relación con ASISTENCIAS
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Asistencia> asistencias;

    // 🔹 Constructores
    public Empleado() {}

    public Empleado(Long empleadoId) {
        this.id = empleadoId;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCompleto() {
        return nombreCompleto;
    }
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getTelefono() { return telefono; }
    public void setTelefono(int telefono) { this.telefono = telefono; }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public boolean getTipoEstado() { return tipoEstado; }
    public void setTipoEstado(boolean tipoEstado) { this.tipoEstado = tipoEstado; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDate getFechaCese() {
        return fechaCese;
    }

    public void setFechaCese(LocalDate fechaCese) {
        this.fechaCese = fechaCese;
    }

    public Sede getSede() { return sede; }
    public void setSede(Sede sede) { this.sede = sede; }

    public Supervisor getSupervisor() { return supervisor; }
    public void setSupervisor(Supervisor supervisor) { this.supervisor = supervisor; }

    public List<Asistencia> getAsistencias() { return asistencias; }
    public void setAsistencias(List<Asistencia> asistencias) { this.asistencias = asistencias; }

    // --- Métodos auxiliares ---
    public void addAsistencia(Asistencia asistencia) {
        asistencias.add(asistencia);
        asistencia.setEmpleado(this);
    }

    public void removeAsistencia(Asistencia asistencia) {
        asistencias.remove(asistencia);
        asistencia.setEmpleado(null);
    }



}

