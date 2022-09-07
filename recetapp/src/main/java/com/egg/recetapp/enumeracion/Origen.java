package com.egg.recetapp.enumeracion;

public enum Origen {

    ARMENIO("Armenio"),
    CHINA("China"),
    INTERNACIONAL("Internacional"),
    JAPONESA("Japonesa"),
    MEXICANA("Mexicana"),
    PERUANA("Peruana"),
    CRIOLLA("Criolla"),
    ITALIANA("Italiana"),
    FRANCESA("Francesa"),
    COLOMBIANA("Colombiana"),
    VENEZOLANA("Venezolana"),
    BRASILERA("Brasilera"),
    ARABE("Arabe"),
    INDU("Indu"),
    RUSA("Rusa"),
    CATALANA("Catalana");
    

    private String nombre;

    private Origen(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return this.nombre;
    }
    
    // CONSULTA: COMO HACEMOS BIEN EL ENUM

}
