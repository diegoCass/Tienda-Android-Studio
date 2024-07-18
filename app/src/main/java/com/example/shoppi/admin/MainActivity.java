package com.example.shoppi.admin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;

    private DatabaseHelper databaseHelper;
    private EditText editTextSectionName;
    private Button buttonAddSection;
    private Button buttonSelectImage;
    private ImageView imageViewSelected;
    private RecyclerView recyclerViewSections;
    private SectionAdapter sectionAdapter;
    private List<Seccion> sectionList;
    private String selectedImagePath = null;
    private SessionManager sessionManager; // Variable para manejar la sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this); // Inicializar SessionManager

        checkStoragePermission();

        editTextSectionName = findViewById(R.id.editTextSectionName);
        buttonAddSection = findViewById(R.id.buttonAddSection);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        imageViewSelected = findViewById(R.id.imageViewSelected);
        recyclerViewSections = findViewById(R.id.recyclerViewSections);

        recyclerViewSections.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        sectionList = new ArrayList<>();
        sectionAdapter = new SectionAdapter(sectionList, new SectionAdapter.OnSectionClickListener() {
            @Override
            public void onSectionClick(int position) {
                Seccion seccion = sectionList.get(position);
                Intent intent = new Intent(MainActivity.this, SectionDetailActivity.class);
                intent.putExtra("SECCION_ID", seccion.getId());
                startActivity(intent);
            }
        });

        recyclerViewSections.setAdapter(sectionAdapter);

        buttonAddSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sectionName = editTextSectionName.getText().toString().trim();
                if (!sectionName.isEmpty()) {
                    createSection(sectionName);
                    loadSections();
                }
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        TextView textViewCerrar = findViewById(R.id.textViewCerrar);
        textViewCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion(v);
            }
        });

        loadSections();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes acceder al almacenamiento
            } else {
                Toast.makeText(this, "Permiso de acceso al almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createSection(String name) {
        try {
            String imgPath = selectedImagePath != null ? selectedImagePath : "@drawable/estre.png"; // Reemplaza con una imagen por defecto si no se selecciona ninguna
            databaseHelper.insertSeccion(name, imgPath); // Incluye ambos argumentos
            selectedImagePath = null; // Reiniciar la ruta de la imagen seleccionada después de crear la sección
            imageViewSelected.setImageResource(0); // Limpia la vista de la imagen
        } catch (Exception e) {
            Log.e("MainActivity", "Error creating section", e);
        }
    }

    private void loadSections() {
        sectionList.clear();
        sectionList.addAll(databaseHelper.getAllSecciones());
        sectionAdapter.notifyDataSetChanged();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                selectedImagePath = saveImageToInternalStorage(imageUri);
                imageViewSelected.setImageURI(imageUri);
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            if (inputStream == null) return null;

            File file = new File(getFilesDir(), getFileName(uri));
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("MainActivity", "Error saving image to internal storage", e);
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public void cerrarSesion(View view) {
        // Lógica para cerrar sesión
        sessionManager.setLogin(false); // Marcar como no logueado
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finalizar la actividad actual
    }
}
