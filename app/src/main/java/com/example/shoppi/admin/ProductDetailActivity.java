package com.example.shoppi.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppi.admin.SessionManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shoppi.R;
import com.google.android.material.snackbar.Snackbar;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView textViewProductName, textViewProductPrice, textViewDescripcion;
    private ImageView imageViewProduct, imageViewGif;
    private Button buttonAddToCart, buttonViewCart, btnback2;
    private ShoppingCart shoppingCart;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        textViewProductName = findViewById(R.id.textViewProductName);
        textViewProductPrice = findViewById(R.id.textViewProductPrice);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        imageViewGif = findViewById(R.id.imageViewGif);
        textViewDescripcion = findViewById(R.id.textViewDescripcion);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        shoppingCart = ShoppingCart.getInstance();
        buttonViewCart = findViewById(R.id.carrito);
        btnback2 = findViewById(R.id.btnback2);
        sessionManager = new SessionManager(this);

        Intent intent = getIntent();
        if (intent != null) {
            String productName = intent.getStringExtra("PRODUCT_NAME");
            double productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0);
            String productImageUri = intent.getStringExtra("PRODUCT_IMAGE_URI");
            String productDescripcion = intent.getStringExtra("PRODUCT_DESCRIPCION");
            int productId = intent.getIntExtra("PRODUCT_ID", 0);

            textViewProductName.setText(productName);
            textViewProductPrice.setText(String.format("$%.2f", productPrice));
            textViewDescripcion.setText(productDescripcion);

            if (productImageUri != null && !productImageUri.isEmpty()) {
                Glide.with(this).load(Uri.parse(productImageUri)).into(imageViewProduct);
            }

            buttonViewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sessionManager.isLoggedIn()) {
                        Intent cartIntent = new Intent(ProductDetailActivity.this, CartActivity.class);
                        cartIntent.putExtra("PRODUCT_ID", productId);
                        startActivity(cartIntent);
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "Debes iniciar sesión para ver el carrito", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                }
            });

            btnback2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            Product product = new Product(productId, productName, productPrice, productImageUri);
            buttonAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shoppingCart.addItem(product);

                    Glide.with(ProductDetailActivity.this)
                            .asGif()
                            .load(R.drawable.carrito_de_compras)
                            .into(imageViewGif);

                    Animation fadeIn = AnimationUtils.loadAnimation(ProductDetailActivity.this, R.anim.fade_in);
                    Animation fadeOut = AnimationUtils.loadAnimation(ProductDetailActivity.this, R.anim.fade_out);

                    buttonViewCart.setVisibility(View.GONE);

                    imageViewGif.startAnimation(fadeIn);
                    imageViewGif.setVisibility(View.VISIBLE);

                    imageViewGif.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imageViewGif.startAnimation(fadeOut);
                            imageViewGif.setVisibility(View.GONE);

                            buttonViewCart.setVisibility(View.VISIBLE);
                        }
                    }, 2000);

                    Snackbar.make(v, "Producto añadido al carrito", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }
}
