package com.example.checkinlocais;

public class Checkin {
    public final String local;
    public final int qtdVisitas;
    public final int categoriaId;
    public final String categoriaNome;
    public final String latitude;
    public final String longitude;

    public Checkin(String local, int qtdVisitas, int categoriaId, String categoriaNome, String latitude, String longitude) {
        this.local = local;
        this.qtdVisitas = qtdVisitas;
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
