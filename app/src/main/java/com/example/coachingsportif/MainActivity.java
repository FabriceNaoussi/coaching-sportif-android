package com.example.coachingsportif;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText editEmail, editMotDePasse;
    private TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail = findViewById(R.id.editEmail);
        editMotDePasse = findViewById(R.id.editMotDePasse);
        txtMessage = findViewById(R.id.txtMessage);
        Button btnConnexion = findViewById(R.id.btnConnexion);
        TextView txtCreerCompte = findViewById(R.id.txtCreerCompte);

        btnConnexion.setOnClickListener(v -> tenterConnexion());

        txtCreerCompte.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InscriptionActivity.class);
            startActivity(intent);
        });
    }

    private void tenterConnexion() {
        String email = editEmail.getText().toString().trim();
        String motDePasse = editMotDePasse.getText().toString().trim();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            txtMessage.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            JSONObject corps = new JSONObject();
            corps.put("email", email);
            corps.put("mot_de_passe", motDePasse);

            ApiHelper.appelAvecCorps("/api/login", "POST", null, corps.toString(), new ApiHelper.ApiCallback() {
                @Override
                public void onSuccess(String reponseJson, int codeStatut) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject reponse = new JSONObject(reponseJson);
                            if (codeStatut == 200) {
                                String token = reponse.getString("token");
                                // Sauvegarder le token pour les prochains écrans
                                SharedPreferences prefs = getSharedPreferences("CoachingPrefs", MODE_PRIVATE);
                                prefs.edit().putString("token", token).apply();

                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                txtMessage.setText(reponse.optString("message", "Erreur de connexion"));
                            }
                        } catch (JSONException e) {
                            txtMessage.setText("Réponse invalide du serveur.");
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> txtMessage.setText("Erreur réseau : " + message));
                }
            });
        } catch (JSONException e) {
            txtMessage.setText("Erreur de préparation de la requête.");
        }
    }
}