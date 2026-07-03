package com.example.checkinlocais;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 100;

    private AutoCompleteTextView autoLocal;
    private Spinner spinnerCategoria;
    private TextView txtLatitude;
    private TextView txtLongitude;
    private Button btnCheckin;

    private DatabaseHelper db;
    private List<Categoria> categorias;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Double latitudeAtual;
    private Double longitudeAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        autoLocal = findViewById(R.id.autoLocal);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        btnCheckin = findViewById(R.id.btnCheckin);

        configurarAutoComplete();
        configurarSpinnerCategorias();
        configurarBotaoCheckin();
        configurarCallbackLocalizacao();

        if (temPermissaoLocalizacao()) {
            iniciarAtualizacaoLocalizacao();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }
    }

    private void configurarAutoComplete() {
        List<String> locais = db.listarNomesLocais();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locais);
        autoLocal.setAdapter(adapter);
    }

    private void configurarSpinnerCategorias() {
        categorias = db.listarCategorias();
        List<String> nomes = new ArrayList<>();
        for (Categoria categoria : categorias) {
            nomes.add(categoria.nome);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    private void configurarBotaoCheckin() {
        btnCheckin.setOnClickListener(v -> realizarCheckin());
    }

    private void configurarCallbackLocalizacao() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    atualizarLocalizacaoNaTela(location);
                }
            }
        };
    }

    private boolean temPermissaoLocalizacao() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void iniciarAtualizacaoLocalizacao() {
        if (!temPermissaoLocalizacao()) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(1500)
                .build();

        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    atualizarLocalizacaoNaTela(location);
                }
            });
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Toast.makeText(this, "Permissão de localização não concedida.", Toast.LENGTH_LONG).show();
        }
    }

    private void atualizarLocalizacaoNaTela(Location location) {
        latitudeAtual = location.getLatitude();
        longitudeAtual = location.getLongitude();
        txtLatitude.setText(formatarCoordenada(latitudeAtual));
        txtLongitude.setText(formatarCoordenada(longitudeAtual));
    }

    private String formatarCoordenada(double valor) {
        return String.format(Locale.US, "%.8f", valor);
    }

    private void realizarCheckin() {
        String local = autoLocal.getText().toString().trim();

        if (local.isEmpty()) {
            Toast.makeText(this, "Informe o nome do local.", Toast.LENGTH_LONG).show();
            return;
        }

        if (categorias == null || categorias.isEmpty() || spinnerCategoria.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Escolha uma categoria.", Toast.LENGTH_LONG).show();
            return;
        }

        if (latitudeAtual == null || longitudeAtual == null) {
            Toast.makeText(this, "Aguarde a localização atual ser obtida.", Toast.LENGTH_LONG).show();
            return;
        }

        if (db.existeCheckin(local)) {
            db.incrementarVisitas(local);
            Toast.makeText(this, "Check-in atualizado.", Toast.LENGTH_SHORT).show();
        } else {
            int posicao = spinnerCategoria.getSelectedItemPosition();
            int categoriaId = categorias.get(posicao).id;
            db.inserirCheckin(local, categoriaId, formatarCoordenada(latitudeAtual), formatarCoordenada(longitudeAtual));
            Toast.makeText(this, "Novo check-in cadastrado.", Toast.LENGTH_SHORT).show();
        }

        reiniciarTela();
    }

    private void reiniciarTela() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_mapa) {
            abrirMapa();
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
        return super.onOptionsItemSelected(item);
    }

    private void abrirMapa() {
        if (latitudeAtual == null || longitudeAtual == null) {
            Toast.makeText(this, "Aguarde a localização atual para abrir o mapa.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, MapaCheckinActivity.class);
        intent.putExtra("latitude", latitudeAtual);
        intent.putExtra("longitude", longitudeAtual);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && temPermissaoLocalizacao()) {
                iniciarAtualizacaoLocalizacao();
            } else {
                txtLatitude.setText("Sem permissão");
                txtLongitude.setText("Sem permissão");
                Toast.makeText(this, "A localização é necessária para fazer check-in.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

}
