package com.example.shoppi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppi.admin.CartActivity;
import com.example.shoppi.admin.DatabaseHelper;
import com.example.shoppi.admin.LoginActivity;
import com.example.shoppi.admin.ProductAdapter;
import com.example.shoppi.admin.ProductDetailActivity;
import com.example.shoppi.admin.Producto;
import com.example.shoppi.admin.Seccion;
import com.example.shoppi.admin.SectionAdapter;
import com.example.shoppi.admin.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class inicio extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewSections;
    private RecyclerView recyclerViewProducts;
    private SectionAdapter sectionAdapter;
    private List<Seccion> sectionList = new ArrayList<>();
    private ProgressBar progressBar;
    private ProgressBar progressBar3;
    private ProductAdapter productAdapter;
    private Button buttonViewCart;
    private List<Producto> productList = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        progressBar = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        buttonViewCart = findViewById(R.id.carrito);
        sessionManager = new SessionManager(this);

        if (progressBar == null) {
            throw new RuntimeException("progressBar2 no se encontró en el layout");
        }
        if (progressBar3 == null) {
            throw new RuntimeException("progressBar3 no se encontró en el layout");
        }

        databaseHelper = new DatabaseHelper(this);
        checkStoragePermission();

        recyclerViewSections = findViewById(R.id.recyclerViewSections);
        recyclerViewSections.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        productAdapter = new ProductAdapter(productList);
        recyclerViewProducts.setAdapter(productAdapter);

        sectionAdapter = new SectionAdapter(sectionList, new SectionAdapter.OnSectionClickListener() {
            @Override
            public void onSectionClick(int position) {
                Seccion seccion = sectionList.get(position);
                Intent intent = new Intent(inicio.this, section_detail.class);
                intent.putExtra("SECCION_ID", seccion.getId());
                startActivity(intent);
            }
        });
        recyclerViewSections.setAdapter(sectionAdapter);

        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Producto selectedProduct = productList.get(position);

                Intent intent = new Intent(inicio.this, ProductDetailActivity.class);

                intent.putExtra("PRODUCT_ID", selectedProduct.getId());
                intent.putExtra("PRODUCT_NAME", selectedProduct.getNombre());
                intent.putExtra("PRODUCT_PRICE", selectedProduct.getPrecio());
                intent.putExtra("PRODUCT_IMAGE_URI", selectedProduct.getImagenUri());
                intent.putExtra("PRODUCT_QR", selectedProduct.getQr());
                intent.putExtra("PRODUCT_DESCRIPCION", selectedProduct.getDescripcion());

                startActivity(intent);
            }
        });

        buttonViewCart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    Intent cartIntent = new Intent(inicio.this, CartActivity.class);
                    startActivity(cartIntent);
                } else {
                    Toast.makeText(inicio.this, "Debes iniciar sesión para ver el carrito", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(inicio.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

        Button cerrarButton = findViewById(R.id.cerrar);
        cerrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            loadSections();
            loadRandomProducts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSections();
                loadRandomProducts();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadSections() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sectionList.clear();
                        sectionList.addAll(databaseHelper.getAllSecciones());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sectionAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();
            }
        }, 1000);
    }

    private void loadRandomProducts() {
        progressBar3.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        productList.clear();
                        productList.addAll(databaseHelper.getProductosAleatorios());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                productAdapter.notifyDataSetChanged();
                                progressBar3.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();
            }
        }, 1300);
    }

    private void cerrarSesion() {
        // Cerrar sesión y redirigir al LoginActivity
        sessionManager.setLogin(false); // Marcar como no logueado
        Intent intent = new Intent(inicio.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finalizar la actividad actual
    }
}
