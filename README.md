<p align="center">
  <img src="fastlane/metadata/android/en-US/images/icon.png" width="120" alt="Icône de Rushio">
</p>

<h1 align="center">Rushio</h1>

<p align="center">
  Paroles synchronisées pour Android, en Kotlin et Jetpack Compose (Material 3 / Material You).<br>
  Version personnelle et améliorée de <a href="https://github.com/shub39/Rush">Rush</a>.
</p>

## Fonctionnalités

- Recherche, sauvegarde et partage de paroles (cartes de partage façon Spotify).
- Paroles synchronisées à la ligne ou à la syllabe, avec suivi automatique de la chanson en cours
  (mode Rushio, nécessite l'accès aux notifications).
- Arrière-plans animés, personnalisation du texte, romanisation (japonais, coréen, chinois…).
- Toutes les fonctionnalités sont gratuites : pas d'achats intégrés, pas de statistiques d'utilisation.
- Interface en français et en anglais.

## Crédits

Rushio est un fork de [Rush](https://github.com/shub39/Rush), créé par
[Shubham Gorai (shub39)](https://github.com/shub39) et ses contributeurs, distribué sous licence
GPL-3.0. Un grand merci à eux ! Si Rushio vous plaît, pensez à soutenir le projet d'origine.

Bibliothèques et projets utilisés ou ayant inspiré Rush / Rushio :

- [Fastlyrics](https://github.com/TecCheck/FastLyrics), [SongSync](https://github.com/Lambada10/SongSync),
  [LrcLib](https://lrclib.net/), [LyricsPlus](https://github.com/ibratabian17/lyricsplus)
- Kotlin, Jetpack Compose et Compose Multiplatform
- [MaterialKolor](https://github.com/jordond/MaterialKolor), [Kmpalette](https://github.com/jordond/kmpalette),
  [ColorPicker Compose](https://github.com/skydoves/colorpicker-compose)
- [FileKit](https://github.com/vinceglb/filekit), [KSoup](https://github.com/fleeksoft/ksoup),
  [Landscapist](https://github.com/skydoves/landscapist), [Zoomable](https://github.com/usuiat/Zoomable)

## Licence

Rushio est un logiciel libre distribué sous licence
[GNU GPL v3.0 ou ultérieure](LICENSE), comme Rush.

## Compiler

Prérequis : JDK 25 (le démon Gradle y est épinglé) et le SDK Android (plateforme 37).

```bash
./gradlew :androidApp:assembleDebug     # APK de debug (com.karelisio.rushio.debug)
./gradlew :androidApp:assembleRelease   # APK release (signé si les variables ci-dessous sont définies)
```

Le module `desktopApp` (et `iosApp`) hérité de Rush n'est pas maintenu dans Rushio.

## APK signé automatiquement (GitHub Actions)

Le workflow [`build-release.yml`](.github/workflows/build-release.yml) compile un APK release signé à
chaque push sur `main` (ou manuellement via « Run workflow ») et le joint en artefact au run.

Secrets à créer dans *Settings → Secrets and variables → Actions* :

| Secret              | Contenu                                    |
|---------------------|--------------------------------------------|
| `KEYSTORE_BASE64`   | le keystore `.jks` encodé en base64        |
| `KEYSTORE_PASSWORD` | le mot de passe du keystore                |
| `KEY_ALIAS`         | l'alias de la clé                          |
| `KEY_PASSWORD`      | le mot de passe de la clé                  |

Créer un keystore et obtenir sa version base64 :

```bash
keytool -genkeypair -v -keystore rushio.jks -alias rushio -keyalg RSA -keysize 4096 -validity 10000
base64 -w 0 rushio.jks   # à coller dans le secret KEYSTORE_BASE64
```

Pour signer en local, exporter `KEYSTORE_FILE` (chemin du `.jks`), `KEYSTORE_PASSWORD`, `KEY_ALIAS`
et `KEY_PASSWORD` avant de lancer `assembleRelease`.
