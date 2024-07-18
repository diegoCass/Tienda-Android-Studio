package com.example.shoppi.admin;

public class Producto {
    private int id;
    private String nombre;
    private double precio;
    private String img_seccion;
    private String qr;
    private String descripcion;

    public Producto(int id, String nombre, double precio, String img_seccion, String qr, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.img_seccion = img_seccion;
        this.qr = qr;
        this.descripcion = descripcion;
    }

    // Getters y setters para todos los campos, incluyendo el nuevo campo 'descripcion'
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {this.precio = precio;
    }

    public String getImagenUri() {
        return img_seccion;
    }

    public void img_seccion(String img_seccion) {
        this.img_seccion = img_seccion;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
