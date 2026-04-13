package com.gestion.empleados.entidades;

import javax.persistence.*;

@Entity
@Table(name = "sede")
    public class Sede {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_sede")
        private Long idSede;

        private String nombre;

        // constructores, getters y setters
        public Sede() {}
        public Sede(Long id) { this.idSede = id; }
        public Long getId() { return idSede; }
        public void setId(Long id) { this.idSede = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public Long getIdSede() {
        return idSede;
    }

        public void setIdSede(Long idSede) {
        this.idSede = idSede;
    }

}

