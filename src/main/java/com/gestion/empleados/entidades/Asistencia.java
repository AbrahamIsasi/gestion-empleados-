package com.gestion.empleados.entidades;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "asistencia")
public class Asistencia {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Relación con EMPLEADO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    // 🔹 (Opcional) Relación con SUPERVISOR -> quién registró la asistencia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id", referencedColumnName = "id_supervisor")
    private Supervisor registradoPor;


    // 📅 Fecha del registro (solo día)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date fecha;

    // 📊 Estado (ASISTIO, FALTO, LSG, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_asistencia", length = 20, nullable = false)
    private EstadoAsistencia estadoAsistencia;

    // 📆 Auditoría
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt = new Date();

    // ✅ Constructores
    public Asistencia() {}

    public Asistencia(Empleado empleado, Date fecha, EstadoAsistencia estadoAsistencia) {
        this.empleado = empleado;
        this.fecha = fecha;
        this.estadoAsistencia = estadoAsistencia;
    }

    // ✅ Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Supervisor getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(Supervisor registradoPor) { this.registradoPor = registradoPor; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public EstadoAsistencia getEstadoAsistencia() { return estadoAsistencia; }
    public void setEstadoAsistencia(EstadoAsistencia estadoAsistencia) { this.estadoAsistencia = estadoAsistencia; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }
}
