package com.gestion.empleados.entidades;

public enum EstadoAsistencia {

        ASISTIO("Asistió"),
        CESADO("Cesado"),
        COMISIONISTA("Comisionista"),
        DESCANSO_MEDICO("Descanso Médico"),
        FALTA_1_DIA_DESCUENTO("Falta (1 Día de Descuento)"),
        FALTA_2_DIAS_DESCUENTO("Falta (2 Días de Descuento)"),
        LICENCIA_SIN_GOCE_DE_HABER("Licencia sin Goce de Haber"),
        PAGO_1_DIA("Pago 1 Día"),
        PAGO_DOBLE("Pago Doble"),
        FALLECIMIENTO_FAMILIAR_DIRECTO("Fallecimiento Familiar Directo"),
        PAGO_POR_HORAS("Pago por Horas"),
        VACACIONES("Vacaciones");

        private final String descripcion;

        EstadoAsistencia(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
