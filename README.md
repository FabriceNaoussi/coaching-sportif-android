# 📱 Coaching Sportif — Application Android

Application Android (Java) permettant à un client de gérer son suivi avec
son coach sportif : calendrier de séances, progression (mesures), le tout
via une API REST sécurisée.

> Projet de session — Cours 420-A15-BB (Programmation de services) et
> A17 (Programmation d'applications mobiles Android).

## 🔗 Projet complet — Backend associé

Cette application consomme l'API REST développée séparément :
- **Code source du backend :** https://github.com/FabriceNaoussi/coaching-api-backend
- **API en ligne :** https://coaching-api-backend-uo3x.onrender.com
- **Publiée sur RapidAPI :** https://rapidapi.com/FabriceNaoussi/api/coaching-sportif

## 🛠️ Technologies

- **Java** (Android natif, sans framework tiers)
- **HttpURLConnection** + **Thread** — communication réseau bas niveau
- **org.json** (JSONObject/JSONArray) — parsing des réponses de l'API
- **ListView** + **ArrayAdapter** — affichage des listes
- **Intents** — navigation entre écrans
- **SharedPreferences** — persistance locale du token de session

## 📁 Structure

app/src/main/java/com/example/coachingsportif/
├── ApiHelper.java # Couche réseau réutilisable (GET/POST/PUT/DELETE)
├── MainActivity.java # Écran de connexion
├── InscriptionActivity.java # Écran de création de compte
├── DashboardActivity.java # Tableau de bord — calendrier de séances
├── AjouterSeanceActivity.java # Formulaire d'ajout de séance
└── MesuresActivity.java # Suivi de progression (poids, masse grasse)


## ✨ Fonctionnalités

- Inscription et connexion sécurisées (JWT)
- Consultation du calendrier de séances
- Ajout et annulation de séances
- Suivi de progression (poids, masse grasse) avec historique
- Session persistante (token conservé entre les lancements de l'app)

## 🧠 Concepts démontrés

- Activities, layouts et navigation par Intents
- Connectivité réseau via `HttpURLConnection` sur un `Thread` dédié
- Parsing JSON natif Android
- Permission Internet (`AndroidManifest.xml`)
- Persistance légère (`SharedPreferences`)

## ⚙️ Installation

```bash
git clone https://github.com/FabriceNaoussi/coaching-sportif-android.git
```
Ouvrir dans Android Studio, synchroniser Gradle, lancer sur un émulateur
ou un appareil physique (API 24+). L'application se connecte directement
à l'API déployée en ligne — aucune configuration locale nécessaire.

---