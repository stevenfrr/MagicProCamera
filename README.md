# Magic Pro Camera AI V3
Application Android native avec aperçu CameraX plein écran, capture JPEG, découverte dynamique Camera2 et conseils photo locaux.

## Fonctions réellement incluses
- aperçu caméra arrière et capture JPEG vers `Pictures/MagicProCamera`;
- permission caméra et détection ISO, vitesse, EV, focus manuel, RAW et objectifs disponibles;
- analyse locale et limitée en fréquence : luminosité, nuit/faible lumière, forte lumière et mouvement; aucun pixel n'est envoyé sur Internet;
- recommandations ISO, vitesse, EV et Kelvin bornées par les limites Camera2 détectées;
- IA AUTO, assistant hors ligne (lune/étoiles, action/chien, coucher de soleil) et persistance de son état;
- architecture `camera/`, `ai/scenerecognition/`, `ai/recommendation/`, `ai/enhancement/`, `presets/`, `settings/`.

## Limites transparentes
Aucun modèle de vision n'est embarqué : chien, chat, personne, nourriture et voiture ne sont donc pas reconnus visuellement. La capacité RAW est signalée mais la capture DNG n'est pas encore implémentée. Les recommandations sont affichées et limitées au matériel, mais les commandes manuelles complètes Camera2, l'histogramme, la grille et AE/AF Lock restent à relier aux requêtes de capture; elles ne sont pas annoncées comme appliquées.

## Build
Le workflow GitHub Actions télécharge Gradle 8.10.2 et produit `app/build/outputs/apk/debug/app-debug.apk`.
