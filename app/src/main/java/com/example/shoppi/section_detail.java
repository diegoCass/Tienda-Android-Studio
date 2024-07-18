package com.example.shoppi;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppi.admin.CartActivity;
import com.example.shoppi.admin.DatabaseHelper;
import com.example.shoppi.admin.LoginActivity;
import com.example.shoppi.admin.ProductAdapter;
import com.example.shoppi.admin.ProductDetailActivity;
import com.example.shoppi.admin.Producto;
import com.example.shoppi.admin.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class section_detail extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private Button buttonViewCart, buttonback;
    private List<Producto> productList;
    private int seccionId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_detail);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        buttonViewCart = findViewById(R.id.carrito);
        buttonback = findViewById(R.id.btnback);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerViewProducts.setAdapter(productAdapter);

        seccionId = getIntent().getIntExtra("SECCION_ID", -1);
        Log.d("SectionDetailActivity", "Received section ID: " + seccionId);

        if (seccionId != -1) {
            loadProducts();
        } else {
            Toast.makeText(this, "ID de sección no válido", Toast.LENGTH_SHORT).show();
        }

        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Producto selectedProduct = productList.get(position);

                Intent intent = new Intent(section_detail.this, ProductDetailActivity.class);

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
                    Intent cartIntent = new Intent(section_detail.this, CartActivity.class);
                    startActivity(cartIntent);
                } else {
                    Toast.makeText(section_detail.this, "Debes iniciar sesión para ver el carrito", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(section_detail.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

        buttonback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent backIntent = new Intent(section_detail.this, inicio.class);
                startActivity(backIntent);
            }
        });
    }

    private void openImageSelector() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    private void loadProducts() {
        productList.clear();
        productList.addAll(databaseHelper.getProductosBySeccion(seccionId));
        Log.d("SectionDetailActivity", "Loaded products: " + productList.size());
        productAdapter.notifyDataSetChanged();
    }
}
