package com.egg.recetapp.enumeracion;

public enum Categoria {


    CHEF("Chef"),
    AFICIONADO("Aficionado"),
    APRENDIZ("Aprendiz"),
    COCINERO("Cocinero"),
    SUBCHEF("Subchef");

    private String nombre;

    private Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return this.nombre;
    }

// CONSULTA: COMO HACEMOS BIEN EL ENUM
//        @Enumerated??

}
