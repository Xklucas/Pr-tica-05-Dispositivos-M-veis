package com.example.checkinlocais;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "checkin_locais.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Categoria (" +
                "idCategoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Checkin (" +
                "Local TEXT PRIMARY KEY, " +
                "qtdVisitas INTEGER NOT NULL, " +
                "cat INTEGER NOT NULL, " +
                "latitude TEXT NOT NULL, " +
                "longitude TEXT NOT NULL, " +
                "CONSTRAINT fkey0 FOREIGN KEY (cat) REFERENCES Categoria (idCategoria))");

        inserirCategoriaInicial(db, "Restaurante");
        inserirCategoriaInicial(db, "Bar");
        inserirCategoriaInicial(db, "Cinema");
        inserirCategoriaInicial(db, "Universidade");
        inserirCategoriaInicial(db, "Estádio");
        inserirCategoriaInicial(db, "Parque");
        inserirCategoriaInicial(db, "Outros");
    }

    private void inserirCategoriaInicial(SQLiteDatabase db, String nome) {
        ContentValues values = new ContentValues();
        values.put("nome", nome);
        db.insert("Categoria", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Checkin");
        db.execSQL("DROP TABLE IF EXISTS Categoria");
        onCreate(db);
    }

    public List<Categoria> listarCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT idCategoria, nome FROM Categoria ORDER BY idCategoria ASC", null);
        try {
            while (cursor.moveToNext()) {
                categorias.add(new Categoria(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idCategoria")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nome"))
                ));
            }
        } finally {
            cursor.close();
        }
        return categorias;
    }

    public List<String> listarNomesLocais() {
        List<String> locais = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Local FROM Checkin ORDER BY Local COLLATE NOCASE ASC", null);
        try {
            while (cursor.moveToNext()) {
                locais.add(cursor.getString(cursor.getColumnIndexOrThrow("Local")));
            }
        } finally {
            cursor.close();
        }
        return locais;
    }

    public boolean existeCheckin(String local) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM Checkin WHERE Local = ? LIMIT 1", new String[]{local});
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    public long inserirCheckin(String local, int categoriaId, String latitude, String longitude) {
        ContentValues values = new ContentValues();
        values.put("Local", local);
        values.put("qtdVisitas", 1);
        values.put("cat", categoriaId);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        return getWritableDatabase().insert("Checkin", null, values);
    }

    public int incrementarVisitas(String local) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE Checkin SET qtdVisitas = qtdVisitas + 1 WHERE Local = ?", new Object[]{local});
        return 1;
    }

    public int deletarCheckin(String local) {
        return getWritableDatabase().delete("Checkin", "Local = ?", new String[]{local});
    }

    public List<Checkin> listarCheckins() {
        return listarCheckinsComConsulta("SELECT c.Local, c.qtdVisitas, c.cat, c.latitude, c.longitude, cat.nome AS categoriaNome " +
                "FROM Checkin c INNER JOIN Categoria cat ON c.cat = cat.idCategoria " +
                "ORDER BY c.Local COLLATE NOCASE ASC");
    }

    public List<Checkin> listarRelatorioMaisVisitados() {
        return listarCheckinsComConsulta("SELECT c.Local, c.qtdVisitas, c.cat, c.latitude, c.longitude, cat.nome AS categoriaNome " +
                "FROM Checkin c INNER JOIN Categoria cat ON c.cat = cat.idCategoria " +
                "ORDER BY c.qtdVisitas DESC, c.Local COLLATE NOCASE ASC");
    }

    private List<Checkin> listarCheckinsComConsulta(String sql) {
        List<Checkin> checkins = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                checkins.add(new Checkin(
                        cursor.getString(cursor.getColumnIndexOrThrow("Local")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("qtdVisitas")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("cat")),
                        cursor.getString(cursor.getColumnIndexOrThrow("categoriaNome")),
                        cursor.getString(cursor.getColumnIndexOrThrow("latitude")),
                        cursor.getString(cursor.getColumnIndexOrThrow("longitude"))
                ));
            }
        } finally {
            cursor.close();
        }
        return checkins;
    }
}
