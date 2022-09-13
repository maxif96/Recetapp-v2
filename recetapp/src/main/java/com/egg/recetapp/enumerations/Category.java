package com.egg.recetapp.enumerations;

public enum Category {


    CHEF("Chef"),
    AFICIONADO("Aficionado"),
    APRENDIZ("Aprendiz"),
    COCINERO("Cocinero"),
    SUBCHEF("Subchef");

    private String nombre;

    private Category(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return this.nombre;
    }

// CONSULTA: COMO HACEMOS BIEN EL ENUM
//        @Enumerated??

}
