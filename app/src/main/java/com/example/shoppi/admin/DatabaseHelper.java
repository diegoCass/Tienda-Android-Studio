package com.example.shoppi.admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Shoppi.db";
    private static final int DATABASE_VERSION = 1;

    // Nombres de las tablas.
    private static final String TABLE_SECCIONES = "secciones";
    private static final String TABLE_PRODUCTOS = "productos";
    private static final String TABLE_USUARIOS = "usuarios";

    // Columnas de la tabla 'secciones'.
    private static final String COLUMN_SECCION_ID = "id";
    private static final String COLUMN_SECCION_NOMBRE = "nombre";
    private static final String COLUMN_SECCION_IMG_SECCION = "img_seccion";

    // Columnas de la tabla 'productos'.
    private static final String COLUMN_PRODUCTO_ID = "id";
    private static final String COLUMN_PRODUCTO_NOMBRE = "nombre";
    private static final String COLUMN_PRODUCTO_PRECIO = "precio";
    private static final String COLUMN_PRODUCTO_SECCION_ID = "id_seccion";
    private static final String COLUMN_PRODUCTO_IMAGEN_URI = "imagen_uri";
    private static final String COLUMN_PRODUCTO_QR = "qr";
    private static final String COLUMN_PRODUCTO_DESCRIPCION = "descripcion";

    // Columnas de la tabla 'usuarios'.
    private static final String COLUMN_USUARIO_ID = "id";
    private static final String COLUMN_USUARIO_USERNAME = "username";
    private static final String COLUMN_USUARIO_PASSWORD = "password";

    // Sentencia SQL para crear la tabla 'secciones'.
    private static final String CREATE_TABLE_SECCIONES = "CREATE TABLE " + TABLE_SECCIONES + "("
            + COLUMN_SECCION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SECCION_IMG_SECCION + " TEXT,"
            + COLUMN_SECCION_NOMBRE + " TEXT NOT NULL" + ")";

    // Sentencia SQL para crear la tabla 'productos'.
    private static final String CREATE_TABLE_PRODUCTOS = "CREATE TABLE " + TABLE_PRODUCTOS + "("
            + COLUMN_PRODUCTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PRODUCTO_NOMBRE + " TEXT NOT NULL,"
            + COLUMN_PRODUCTO_PRECIO + " REAL NOT NULL,"
            + COLUMN_PRODUCTO_SECCION_ID + " INTEGER,"
            + COLUMN_PRODUCTO_IMAGEN_URI + " TEXT,"
            + COLUMN_PRODUCTO_DESCRIPCION + " TEXT,"
            + COLUMN_PRODUCTO_QR + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_PRODUCTO_SECCION_ID + ") REFERENCES "
            + TABLE_SECCIONES + "(" + COLUMN_SECCION_ID + "))";

    // Sentencia SQL para crear la tabla 'usuarios'.
    private static final String CREATE_TABLE_USUARIOS = "CREATE TABLE " + TABLE_USUARIOS + "("
            + COLUMN_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USUARIO_USERNAME + " TEXT UNIQUE NOT NULL,"
            + COLUMN_USUARIO_PASSWORD + " TEXT NOT NULL" + ")";

    // Constructor de la clase DatabaseHelper.
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Método llamado cuando se crea la base de datos por primera vez.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear las tablas 'secciones', 'productos' y 'usuarios'.
        db.execSQL(CREATE_TABLE_SECCIONES);
        db.execSQL(CREATE_TABLE_PRODUCTOS);
        db.execSQL(CREATE_TABLE_USUARIOS);
    }

    // Método llamado cuando la base de datos necesita ser actualizada.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar las tablas existentes si existen y crear nuevas.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECCIONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // Método para insertar una nueva sección en la tabla 'secciones'.
    public long insertSeccion(String nombre, String img_seccion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SECCION_NOMBRE, nombre);
        values.put(COLUMN_SECCION_IMG_SECCION, img_seccion);
        return db.insert(TABLE_SECCIONES, null, values);
    }

    // Método para insertar un nuevo producto en la tabla 'productos'.
    public long insertProducto(String nombre, double precio, String imagenUri, int idSeccion, String qr, String descripcion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTO_NOMBRE, nombre);
        values.put(COLUMN_PRODUCTO_PRECIO, precio);
        values.put(COLUMN_PRODUCTO_IMAGEN_URI, imagenUri);
        values.put(COLUMN_PRODUCTO_SECCION_ID, idSeccion);
        values.put(COLUMN_PRODUCTO_QR, qr);
        values.put(COLUMN_PRODUCTO_DESCRIPCION, descripcion);
        return db.insert(TABLE_PRODUCTOS, null, values);
    }

    // Método para insertar un nuevo usuario en la tabla 'usuarios'.
    public long insertUsuario(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO_USERNAME, username);
        values.put(COLUMN_USUARIO_PASSWORD, password);
        return db.insert(TABLE_USUARIOS, null, values);
    }

    // Método para verificar las credenciales de inicio de sesión.
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COLUMN_USUARIO_ID };
        String selection = COLUMN_USUARIO_USERNAME + "=? AND " + COLUMN_USUARIO_PASSWORD + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_USUARIOS, columns, selection, selectionArgs,
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Método para obtener el ID del usuario por nombre de usuario.
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COLUMN_USUARIO_ID };
        String selection = COLUMN_USUARIO_USERNAME + "=?";
        String[] selectionArgs = { username };
        Cursor cursor = db.query(TABLE_USUARIOS, columns, selection, selectionArgs,
                null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_ID));
        }
        cursor.close();
        return userId;
    }

    // Método para obtener todas las secciones de la tabla 'secciones'.
    public List<Seccion> getAllSecciones() {
        List<Seccion> secciones = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SECCIONES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Seccion seccion = new Seccion(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SECCION_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECCION_IMG_SECCION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECCION_NOMBRE))
                );
                secciones.add(seccion);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return secciones;
    }

    // Método para obtener todos los productos de una sección específica de la tabla 'productos'.
    public List<Producto> getProductosBySeccion(int idSeccion) {
        List<Producto> productos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTOS + " WHERE " + COLUMN_PRODUCTO_SECCION_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(idSeccion)});
        if (cursor.moveToFirst()) {
            do {
                Producto producto = new Producto(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_NOMBRE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_PRECIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_IMAGEN_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_QR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_DESCRIPCION))
                );
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    // Método para obtener productos aleatorios de la tabla 'productos'.
    public List<Producto> getProductosAleatorios() {
        List<Producto> productos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTOS + " ORDER BY RANDOM() LIMIT 250";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Producto producto = new Producto(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_NOMBRE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_PRECIO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_IMAGEN_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_QR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCTO_DESCRIPCION))
                );
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }
}
