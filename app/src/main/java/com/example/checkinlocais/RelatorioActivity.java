package com.example.checkinlocais;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RelatorioActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private LinearLayout layoutLocais;
    private LinearLayout layoutVisitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        db = new DatabaseHelper(this);
        layoutLocais = findViewById(R.id.layoutLocais);
        layoutVisitas = findViewById(R.id.layoutVisitas);

        carregarRelatorio();
    }

    private void carregarRelatorio() {
        layoutLocais.removeAllViews();
        layoutVisitas.removeAllViews();

        List<Checkin> checkins = db.listarRelatorioMaisVisitados();

        if (checkins.isEmpty()) {
            layoutLocais.addView(criarTexto("Nenhum check-in cadastrado.", Gravity.START));
            return;
        }

        for (Checkin checkin : checkins) {
            layoutLocais.addView(criarTexto(checkin.local, Gravity.START));
            layoutVisitas.addView(criarTexto(String.valueOf(checkin.qtdVisitas), Gravity.END));
        }
    }

    private TextView criarTexto(String texto, int gravity) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(40)
        );
        textView.setLayoutParams(params);
        textView.setGravity(gravity | Gravity.CENTER_VERTICAL);
        textView.setText(texto);
        textView.setTextSize(16f);
        return textView;
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_voltar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_voltar) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
