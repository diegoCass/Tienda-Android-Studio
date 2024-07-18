package com.example.shoppi.admin;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppi.R;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private ImageView imageViewProduct;
    private CartAdapter cartAdapter;
    private ShoppingCart shoppingCart;
    private TextView textViewTotalPrice;
    private Button buttonCheckout, btnback;
    private EditText editTextSearch;
    private int returnProductId;

    private Handler handler = new Handler();
    private Runnable updateRunnable;

    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 101;

    // Variable de instancia para almacenar el texto del recibo
    private String receiptText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        btnback = findViewById(R.id.btnback);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        buttonCheckout = findViewById(R.id.buttonCheckout);
        editTextSearch = findViewById(R.id.editTextSearch);

        shoppingCart = ShoppingCart.getInstance();

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(shoppingCart.getCartItems(), new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onQuantityChanged(Product product, int quantity) {
                shoppingCart.updateQuantity(product, quantity);
                updateTotalPrice();
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemRemoved(Product product) {
                shoppingCart.removeItem(product);
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }
        });
        recyclerViewCart.setAdapter(cartAdapter);

        updateTotalPrice();

        Intent intent = getIntent();
        if (intent != null) {
            returnProductId = intent.getIntExtra("PRODUCT_ID", -1);
        }

        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckoutConfirmation();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("PRODUCT_ID", returnProductId); // Devolver el ID
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    editTextSearch.setTextColor(ContextCompat.getColor(CartActivity.this, android.R.color.transparent));
                } else {
                    editTextSearch.setTextColor(ContextCompat.getColor(CartActivity.this, android.R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Inicializar el Runnable para actualizar el RecyclerView
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
                handler.postDelayed(this, 2000); // Ejecutar cada 2 segundos
            }
        };

        // Iniciar el Runnable
        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }

    private void updateTotalPrice() {
        textViewTotalPrice.setText(String.format(Locale.getDefault(), "Total: $%.2f", shoppingCart.getTotalPrice()));
    }

    private void showCheckoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Compra")
                .setMessage("¿Estás seguro de que quieres proceder al pago?")
                .setPositiveButton("SI", (dialog, which) -> showReceipt())
                .setNegativeButton("NO", null)
                .show();
    }

    private void showReceipt() {
        Dialog receiptDialog = new Dialog(this);
        receiptDialog.setContentView(R.layout.dialog_receipt);
        TextView textViewReceipt = receiptDialog.findViewById(R.id.textViewReceipt);

        StringBuilder receiptTextBuilder = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        receiptTextBuilder.append("Shoppi\n");
        receiptTextBuilder.append("Fecha: ").append(currentDate).append("\n\n");

        for (CartItem cartItem : shoppingCart.getCartItems()) {
            Product product = cartItem.getProduct();
            receiptTextBuilder.append(product.getName()).append(" - Cantidad: ")
                    .append(cartItem.getQuantity()).append("\n");
        }

        receiptTextBuilder.append("\nTotal: $").append(String.format(Locale.getDefault(), "%.2f", shoppingCart.getTotalPrice()));

        receiptText = receiptTextBuilder.toString();
        textViewReceipt.setText(receiptText);

        receiptDialog.findViewById(R.id.buttonCloseReceipt).setOnClickListener(v -> {
            receiptDialog.dismiss();
            shoppingCart.clearCart();
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
        });

        // Botón para generar PDF
        Button btnPrint = receiptDialog.findViewById(R.id.buttonPrint);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdf(receiptText);
            }
        });

        receiptDialog.show();
    }

    private void generatePdf(String receiptText) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso si no está concedido
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permiso ya concedido
            new GeneratePdfTask().execute(receiptText);
        }
    }

    private class GeneratePdfTask extends AsyncTask<String, Void, Boolean> {
        private File pdfPath;

        @Override
        protected Boolean doInBackground(String... strings) {
            String receiptText = strings[0];
            try {
                File appDir = new File(getFilesDir(), "pdfs");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
                String timestamp = sdf.format(new Date());
                String baseFileName = "recibo_" + timestamp + ".pdf";
                pdfPath = new File(appDir, baseFileName);

                // Verificar si el archivo existe y cambiar el nombre si es necesario
                int fileIndex = 1;
                while (pdfPath.exists()) {
                    String newFileName = "recibo_" + timestamp + "_" + fileIndex + ".pdf";
                    pdfPath = new File(appDir, newFileName);
                    fileIndex++;
                }

                PdfWriter writer = new PdfWriter(pdfPath);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);

                document.add(new Paragraph(receiptText));

                document.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CartActivity.this, "PDF generado correctamente en " + pdfPath.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CartActivity.this, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new GeneratePdfTask().execute(receiptText);
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
