package com.egg.recetapp.enumerations;

public enum Type {
    
    VEGETARIANA("Vegetariana"),
    VEGANA("Vegana"),
    SIN_TACC("Sin TACC"),
    DULCES("Dulces"),
    LIGHT("Light"),
    POLLO("Pollo"),
    PESCADO("Pescado"),
    CARNE_ROJA("Carne Roja"),
    ENSALADAS("Ensaladas"),
    PASTAS("Pastas"),
    COMIDAS_RAPIDAS("Comidas Rapidas"),
    CERDO("Cerdo");

    private String nombre;

    private Type(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return this.nombre;
    }
    
    
    // CONSULTA: COMO HACEMOS BIEN EL ENUM

}
