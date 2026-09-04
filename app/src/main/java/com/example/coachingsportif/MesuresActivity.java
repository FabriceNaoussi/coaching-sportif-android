package com.example.coachingsportif;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MesuresActivity extends AppCompatActivity {

    private EditText editPoids, editMasseGrasse;
    private TextView txtMessage;
    private ListView listMesures;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesures);

        SharedPreferences prefs = getSharedPreferences("CoachingPrefs", MODE_PRIVATE);
        token = prefs.getString("token", null);

        editPoids = findViewById(R.id.editPoids);
        editMasseGrasse = findViewById(R.id.editMasseGrasse);
        txtMessage = findViewById(R.id.txtMessageMesures);
        listMesures = findViewById(R.id.listMesures);
        Button btnAjouter = findViewById(R.id.btnAjouterMesure);

        btnAjouter.setOnClickListener(v -> ajouterMesure());
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerMesures();
    }

    private void chargerMesures() {
        ApiHelper.appelSimple("/api/mesures", "GET", token, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String reponseJson, int codeStatut) {
                runOnUiThread(() -> {
                    try {
                        JSONObject reponse = new JSONObject(reponseJson);
                        JSONArray mesures = reponse.getJSONArray("mesures");

                        List<String> affichage = new ArrayList<>();
                        for (int i = 0; i < mesures.length(); i++) {
                            JSONObject m = mesures.getJSONObject(i);
                            String ligne = m.getDouble("poids") + " kg";
                            if (!m.isNull("masse_grasse")) {
                                ligne += " — " + m.getDouble("masse_grasse") + "% masse grasse";
                            }
                            ligne += "\n" + m.getString("date").replace("T", " ");
                            affichage.add(ligne);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                MesuresActivity.this, android.R.layout.simple_list_item_1, affichage);
                        listMesures.setAdapter(adapter);

                    } catch (JSONException e) {
                        txtMessage.setText("Erreur d'affichage des mesures.");
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> txtMessage.setText("Erreur réseau : " + message));
            }
        });
    }

    private void ajouterMesure() {
        String poidsTxt = editPoids.getText().toString().trim();
        String masseGrasseTxt = editMasseGrasse.getText().toString().trim();

        if (poidsTxt.isEmpty()) {
            txtMessage.setText("Le poids est obligatoire.");
            return;
        }

        try {
            JSONObject corps = new JSONObject();
            corps.put("poids", Double.parseDouble(poidsTxt));
            if (!masseGrasseTxt.isEmpty()) {
                corps.put("masse_grasse", Double.parseDouble(masseGrasseTxt));
            }

            ApiHelper.appelAvecCorps("/api/mesures", "POST", token, corps.toString(), new ApiHelper.ApiCallback() {
                @Override
                public void onSuccess(String reponseJson, int codeStatut) {
                    runOnUiThread(() -> {
                        if (codeStatut == 201) {
                            editPoids.setText("");
                            editMasseGrasse.setText("");
                            txtMessage.setText("");
                            chargerMesures(); // Rafraîchir la liste
                        } else {
                            txtMessage.setText("Erreur lors de l'ajout.");
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> txtMessage.setText("Erreur réseau : " + message));
                }
            });
        } catch (NumberFormatException e) {
            txtMessage.setText("Le poids et la masse grasse doivent être des nombres.");
        } catch (JSONException e) {
            txtMessage.setText("Erreur de préparation de la requête.");
        }
    }
}