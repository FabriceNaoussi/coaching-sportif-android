package com.example.coachingsportif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {

    public static final String BASE_URL = "https://coaching-api-backend-uo3x.onrender.com";

    public interface ApiCallback {
        void onSuccess(String reponseJson, int codeStatut);
        void onError(String message);
    }

    // Requête sans corps (GET, DELETE) ou sans authentification
    public static void appelSimple(String endpoint, String methode, String token, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(methode);
                conn.setRequestProperty("Content-Type", "application/json");
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                int codeStatut = conn.getResponseCode();
                String reponse = lireFlux(codeStatut >= 200 && codeStatut < 300
                        ? conn.getInputStream() : conn.getErrorStream());

                callback.onSuccess(reponse, codeStatut);
                conn.disconnect();
            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    // Requête avec corps JSON (POST, PUT)
    public static void appelAvecCorps(String endpoint, String methode, String token, String corpsJson, ApiCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(methode);
                conn.setRequestProperty("Content-Type", "application/json");
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(corpsJson.getBytes("UTF-8"));
                }

                int codeStatut = conn.getResponseCode();
                String reponse = lireFlux(codeStatut >= 200 && codeStatut < 300
                        ? conn.getInputStream() : conn.getErrorStream());

                callback.onSuccess(reponse, codeStatut);
                conn.disconnect();
            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private static String lireFlux(java.io.InputStream flux) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(flux, "UTF-8"));
        StringBuilder resultat = new StringBuilder();
        String ligne;
        while ((ligne = reader.readLine()) != null) {
            resultat.append(ligne);
        }
        reader.close();
        return resultat.toString();
    }
}