package com.example.coachingsportif;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AjouterSeanceActivity extends AppCompatActivity {

    private EditText editTypeExercice, editDate, editDuree;
    private TextView txtMessage;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_seance);

        SharedPreferences prefs = getSharedPreferences("CoachingPrefs", MODE_PRIVATE);
        token = prefs.getString("token", null);

        editTypeExercice = findViewById(R.id.editTypeExercice);
        editDate = findViewById(R.id.editDate);
        editDuree = findViewById(R.id.editDuree);
        txtMessage = findViewById(R.id.txtMessageAjout);
        Button btnEnregistrer = findViewById(R.id.btnEnregistrerSeance);

        btnEnregistrer.setOnClickListener(v -> enregistrerSeance());
    }

    private void enregistrerSeance() {
        String type = editTypeExercice.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        String dureeTxt = editDuree.getText().toString().trim();

        if (type.isEmpty() || date.isEmpty() || dureeTxt.isEmpty()) {
            txtMessage.setText("Tous les champs sont obligatoires.");
            return;
        }

        try {
            JSONObject corps = new JSONObject();
            corps.put("type_exercice", type);
            corps.put("date", date);
            corps.put("duree_minutes", Integer.parseInt(dureeTxt));

            ApiHelper.appelAvecCorps("/api/seances", "POST", token, corps.toString(), new ApiHelper.ApiCallback() {
                @Override
                public void onSuccess(String reponseJson, int codeStatut) {
                    runOnUiThread(() -> {
                        if (codeStatut == 201) {
                            finish(); // Retour au tableau de bord, qui se rafraîchira via onResume()
                        } else {
                            txtMessage.setText("Erreur lors de la création (vérifie le format de la date).");
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> txtMessage.setText("Erreur réseau : " + message));
                }
            });
        } catch (NumberFormatException e) {
            txtMessage.setText("La durée doit être un nombre.");
        } catch (JSONException e) {
            txtMessage.setText("Erreur de préparation de la requête.");
        }
    }
}