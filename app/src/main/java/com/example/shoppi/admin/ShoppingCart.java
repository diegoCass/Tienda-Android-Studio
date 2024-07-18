package com.example.shoppi.admin;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private static ShoppingCart instance;
    private List<CartItem> cartItems;

    private ShoppingCart() {
        cartItems = new ArrayList<>();
    }

    public static synchronized ShoppingCart getInstance() {
        if (instance == null) {
            instance = new ShoppingCart();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void addItem(Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(product, 1));
    }

    public void removeItem(Product product) {
        CartItem toRemove = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                toRemove = item;
                break;
            }
        }
        if (toRemove != null) {
            cartItems.remove(toRemove);
        }
    }

    public void updateQuantity(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }
}
