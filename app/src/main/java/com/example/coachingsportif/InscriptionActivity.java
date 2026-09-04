package com.example.coachingsportif;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class InscriptionActivity extends AppCompatActivity {

    private EditText editNom, editEmail, editMotDePasse, editObjectif;
    private TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        editNom = findViewById(R.id.editNom);
        editEmail = findViewById(R.id.editEmailInscription);
        editMotDePasse = findViewById(R.id.editMotDePasseInscription);
        editObjectif = findViewById(R.id.editObjectif);
        txtMessage = findViewById(R.id.txtMessageInscription);
        Button btnInscription = findViewById(R.id.btnInscription);

        btnInscription.setOnClickListener(v -> tenterInscription());
    }

    private void tenterInscription() {
        String nom = editNom.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String motDePasse = editMotDePasse.getText().toString().trim();
        String objectif = editObjectif.getText().toString().trim();

        if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            txtMessage.setText("Nom, email et mot de passe sont obligatoires.");
            return;
        }

        try {
            JSONObject corps = new JSONObject();
            corps.put("nom", nom);
            corps.put("email", email);
            corps.put("mot_de_passe", motDePasse);
            corps.put("objectif", objectif);
            corps.put("plan_client", "basique");

            ApiHelper.appelAvecCorps("/api/register", "POST", null, corps.toString(), new ApiHelper.ApiCallback() {
                @Override
                public void onSuccess(String reponseJson, int codeStatut) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject reponse = new JSONObject(reponseJson);
                            if (codeStatut == 201) {
                                txtMessage.setTextColor(0xFF2E7D32); // vert
                                txtMessage.setText("Compte créé ! Retour à la connexion...");
                                // Retour automatique à l'écran de connexion après un court délai
                                txtMessage.postDelayed(() -> {
                                    startActivity(new Intent(InscriptionActivity.this, MainActivity.class));
                                    finish();
                                }, 1500);
                            } else {
                                txtMessage.setText(reponse.optString("message", "Erreur lors de l'inscription"));
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