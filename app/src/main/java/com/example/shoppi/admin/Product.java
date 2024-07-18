package com.example.shoppi.admin;

public class Product {
    private int id;
    private String name;
    private double price;
    private String img_seccion;

    public Product(int id, String name, double price, String img_seccion) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.img_seccion = img_seccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return img_seccion;
    }

    public void setImageUrl(String img_seccion) {
        this.img_seccion = img_seccion;
    }
}
