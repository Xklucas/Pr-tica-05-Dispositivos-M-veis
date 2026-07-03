package com.example.checkinlocais;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class GestaoCheckinActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private LinearLayout layoutConteudo;
    private LinearLayout layoutDeletar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestao_checkin);

        db = new DatabaseHelper(this);
        layoutConteudo = findViewById(R.id.layoutConteudo);
        layoutDeletar = findViewById(R.id.layoutDeletar);

        carregarLista();
    }

    private void carregarLista() {
        layoutConteudo.removeAllViews();
        layoutDeletar.removeAllViews();

        List<Checkin> checkins = db.listarCheckins();

        if (checkins.isEmpty()) {
            TextView vazio = criarTexto("Nenhum check-in cadastrado.", Gravity.START);
            layoutConteudo.addView(vazio);
            return;
        }

        for (Checkin checkin : checkins) {
            TextView txtLocal = criarTexto(checkin.local, Gravity.START);
            layoutConteudo.addView(txtLocal);

            ImageButton btnDeletar = new ImageButton(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(48), dp(48));
            params.gravity = Gravity.CENTER_HORIZONTAL;
            btnDeletar.setLayoutParams(params);
            btnDeletar.setImageResource(android.R.drawable.ic_menu_delete);
            btnDeletar.setBackgroundColor(Color.TRANSPARENT);
            btnDeletar.setContentDescription("Excluir " + checkin.local);
            btnDeletar.setTag(checkin.local);
            btnDeletar.setOnClickListener(this::deletarCheckin);
            layoutDeletar.addView(btnDeletar);
        }
    }

    private TextView criarTexto(String texto, int gravity) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
        );
        textView.setLayoutParams(params);
        textView.setGravity(gravity | Gravity.CENTER_VERTICAL);
        textView.setText(texto);
        textView.setTextSize(16f);
        return textView;
    }

    public void deletarCheckin(View view) {
        Object tag = view.getTag();
        if (!(tag instanceof String)) {
            return;
        }

        String local = (String) tag;
        new AlertDialog.Builder(this)
                .setTitle("Exclusão")
                .setMessage("Tem certeza que deseja excluir " + local + "?")
                .setNegativeButton("NÃO", null)
                .setPositiveButton("SIM", (dialog, which) -> {
                    db.deletarCheckin(local);
                    Toast.makeText(this, "Check-in excluído.", Toast.LENGTH_SHORT).show();
                    reiniciarTela();
                })
                .show();
    }

    private void reiniciarTela() {
        Intent intent = new Intent(this, GestaoCheckinActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
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
