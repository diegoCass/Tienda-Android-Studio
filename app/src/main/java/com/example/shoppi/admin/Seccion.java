package com.example.shoppi.admin;

public class Seccion {
    private int id;
    private String nombre;
    private String img_seccion;

    public Seccion(int id, String img_seccion, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.img_seccion = img_seccion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSeccionImg() {
        return img_seccion;
    }
}
