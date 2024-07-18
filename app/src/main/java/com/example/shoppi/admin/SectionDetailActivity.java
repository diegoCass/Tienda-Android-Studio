package com.example.shoppi.admin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppi.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SectionDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2;

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Producto> productList;
    private int seccionId;
    private Uri selectedImageUri;
    private ImageView imageViewProductDialog;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_detail);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

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

        Button buttonShowDialog = findViewById(R.id.buttonShowDialog);
        buttonShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();
            }
        });

        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Producto selectedProduct = productList.get(position);

                Intent intent = new Intent(SectionDetailActivity.this, ProductDetailActivity.class);

                intent.putExtra("PRODUCT_ID", selectedProduct.getId());
                intent.putExtra("PRODUCT_NAME", selectedProduct.getNombre());
                intent.putExtra("PRODUCT_PRICE", selectedProduct.getPrecio());
                intent.putExtra("PRODUCT_IMAGE_URI", selectedProduct.getImagenUri());
                intent.putExtra("PRODUCT_QR", selectedProduct.getQr());
                intent.putExtra("PRODUCT_DESCRIPCION",selectedProduct.getDescripcion());

                startActivity(intent);
            }
        });
    }

    private void showAddProductDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        EditText editTextProductName = dialogView.findViewById(R.id.editTextProductName);
        EditText editTextProductPrice = dialogView.findViewById(R.id.editTextProductPrice);
        EditText editTextQR = dialogView.findViewById(R.id.editTextQR);
        EditText editTextDescription = dialogView.findViewById(R.id.Textdescripcion);
        Button buttonSelectImage = dialogView.findViewById(R.id.buttonSelectImage);
        imageViewProductDialog = dialogView.findViewById(R.id.imageViewProduct);
        Button buttonAddProduct = dialogView.findViewById(R.id.buttonAddProduct);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = editTextProductName.getText().toString().trim();
                String productPriceStr = editTextProductPrice.getText().toString().trim();
                String qr = editTextQR.getText().toString().trim();
                String descripcion = editTextDescription.getText().toString().trim(); // Obtener la descripción
                if (!productName.isEmpty() && !productPriceStr.isEmpty() && selectedImageUri != null && !qr.isEmpty() && !descripcion.isEmpty()) {
                    double productPrice = Double.parseDouble(productPriceStr);
                    addProduct(productName, productPrice, selectedImageUri.toString(), qr, descripcion); // Pasar la descripción
                } else {
                    Toast.makeText(SectionDetailActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void openImageSelector() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageSelector();
            } else {
                Toast.makeText(this, "Permiso de acceso al almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Uri newUri = saveImageToInternalStorage(selectedImageUri);
            if (newUri != null) {
                selectedImageUri = newUri;
                if (imageViewProductDialog != null) {
                    imageViewProductDialog.setImageURI(selectedImageUri);
                    imageViewProductDialog.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(this, "Error al copiar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri saveImageToInternalStorage(Uri imageUri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            File directory = new File(getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            return Uri.fromFile(file);
        } catch (Exception e) {
            Log.e("SectionDetailActivity", "Error saving image", e);
            return null;
        }
    }

    private void addProduct(String name, double price, String imageUri, String qr, String descripcion) {
        try {
            databaseHelper.insertProducto(name, price, imageUri, seccionId, qr, descripcion);
            Toast.makeText(SectionDetailActivity.this, "Producto agregado", Toast.LENGTH_SHORT).show();
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            loadProducts();
        } catch (Exception e) {
            Log.e("SectionDetailActivity", "Error adding product", e);
        }
    }

    private void loadProducts() {
        productList.clear();
        productList.addAll(databaseHelper.getProductosBySeccion(seccionId));
        Log.d("SectionDetailActivity", "Loaded products: " + productList.size());
        productAdapter.notifyDataSetChanged();
    }
}
