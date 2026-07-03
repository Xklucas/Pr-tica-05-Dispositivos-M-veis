package com.example.checkinlocais;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapaCheckinActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private DatabaseHelper db;
    private double latitudeAtual;
    private double longitudeAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_checkin);

        latitudeAtual = getIntent().getDoubleExtra("latitude", Double.NaN);
        longitudeAtual = getIntent().getDoubleExtra("longitude", Double.NaN);

        if (Double.isNaN(latitudeAtual) || Double.isNaN(longitudeAtual)) {
            Toast.makeText(this, "Localização atual não recebida.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        db = new DatabaseHelper(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        habilitarMinhaLocalizacao();

        LatLng posicaoUsuario = new LatLng(latitudeAtual, longitudeAtual);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoUsuario, 16f));

        adicionarMarcadoresCheckin();
    }

    private void habilitarMinhaLocalizacao() {
        if (googleMap == null) {
            return;
        }

        boolean permissaoFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permissaoCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (permissaoFine || permissaoCoarse) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException ignored) {
            }
        }
    }

    private void adicionarMarcadoresCheckin() {
        List<Checkin> checkins = db.listarCheckins();
        for (Checkin checkin : checkins) {
            try {
                double latitude = Double.parseDouble(checkin.latitude);
                double longitude = Double.parseDouble(checkin.longitude);
                LatLng posicao = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(posicao)
                        .title(checkin.local)
                        .snippet("Categoria: " + checkin.categoriaNome + " Visitas: " + checkin.qtdVisitas));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_voltar) {
            finish();
            return true;
        }
        if (id == R.id.action_gestao) {
            startActivity(new Intent(this, GestaoCheckinActivity.class));
            return true;
        }
        if (id == R.id.action_relatorio) {
            startActivity(new Intent(this, RelatorioActivity.class));
            return true;
        }
        if (id == R.id.action_mapa_normal) {
            if (googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
            return true;
        }
        if (id == R.id.action_mapa_hibrido) {
            if (googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
