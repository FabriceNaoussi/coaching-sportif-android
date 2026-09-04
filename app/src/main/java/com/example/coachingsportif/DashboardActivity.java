package com.example.coachingsportif;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ListView listSeances;
    private TextView txtMessage;
    private String token;
    private List<Integer> idsSeances = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences prefs = getSharedPreferences("CoachingPrefs", MODE_PRIVATE);
        token = prefs.getString("token", null);

        if (token == null) {
            // Pas de session active, retour à l'écran de connexion
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        listSeances = findViewById(R.id.listSeances);
        txtMessage = findViewById(R.id.txtMessageDashboard);
        Button btnAjouterSeance = findViewById(R.id.btnAjouterSeance);
        Button btnVoirMesures = findViewById(R.id.btnVoirMesures);

        btnAjouterSeance.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, AjouterSeanceActivity.class)));

        btnVoirMesures.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, MesuresActivity.class)));

        listSeances.setOnItemClickListener((parent, view, position, id) ->
                afficherOptionsSeance(idsSeances.get(position)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerSeances(); // Recharge la liste à chaque retour sur cet écran
    }

    private void chargerSeances() {
        ApiHelper.appelSimple("/api/seances", "GET", token, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String reponseJson, int codeStatut) {
                runOnUiThread(() -> {
                    try {
                        JSONObject reponse = new JSONObject(reponseJson);
                        JSONArray seances = reponse.getJSONArray("seances");

                        List<String> affichage = new ArrayList<>();
                        idsSeances.clear();

                        for (int i = 0; i < seances.length(); i++) {
                            JSONObject s = seances.getJSONObject(i);
                            idsSeances.add(s.getInt("id"));
                            String ligne = s.getString("type_exercice") + " — "
                                    + s.getString("date").replace("T", " ") + "\n"
                                    + s.getInt("duree_minutes") + " min — " + s.getString("statut");
                            affichage.add(ligne);
                        }

                        if (affichage.isEmpty()) {
                            txtMessage.setText("Aucune séance pour l'instant. Ajoutes-en une !");
                        } else {
                            txtMessage.setText("");
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                DashboardActivity.this, android.R.layout.simple_list_item_1, affichage);
                        listSeances.setAdapter(adapter);

                    } catch (JSONException e) {
                        txtMessage.setText("Erreur d'affichage des séances.");
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> txtMessage.setText("Erreur réseau : " + message));
            }
        });
    }

    private void afficherOptionsSeance(int seanceId) {
        new AlertDialog.Builder(this)
                .setTitle("Cette séance")
                .setItems(new CharSequence[]{"Annuler la séance", "Fermer"}, (dialog, which) -> {
                    if (which == 0) {
                        annulerSeance(seanceId);
                    }
                })
                .show();
    }

    private void annulerSeance(int seanceId) {
        ApiHelper.appelSimple("/api/seances/" + seanceId + "/annuler", "PUT", token, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String reponseJson, int codeStatut) {
                runOnUiThread(() -> {
                    Toast.makeText(DashboardActivity.this, "Séance annulée", Toast.LENGTH_SHORT).show();
                    chargerSeances(); // Rafraîchir la liste
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Erreur : " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}