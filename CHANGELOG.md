# Changelog

## 1.0.0
- Rushio : nouveau nom, nouvelle icône et nouvel identifiant (com.karelisio.rushio)
- Toutes les fonctionnalités sont gratuites (RevenueCat et écrans Pro supprimés)
- Aucune statistique d'utilisation collectée
- Traduction française complétée
- APK signé généré automatiquement par GitHub Actions
- Nouvelles sources de paroles : NetEase, QQ Music et Musixmatch (non officiel), en plus de LRCLIB
- Recherche en cascade : syllabe > ligne > texte brut, ordre des sources configurable
- Réglages « Sources de paroles » : activer, désactiver et réordonner chaque source
- Bouton « Changer de source » sur l'écran des paroles, avec comparaison des sources
- Correction manuelle des paroles dans toutes les sources
- Compensation de la latence Bluetooth : détection de la sortie audio active (appareil et type)
- Décalage de synchronisation par appareil précis ou par type d'appareil (autoradio, casque, enceinte…)
- Boutons −50 / +50 ms sur l'écran des paroles, enregistrables pour l'appareil, le type ou le titre
- Réglages « Appareils audio et latence » : appareils connus et décalages modifiables
- Paroles flottantes : fenêtre par-dessus les autres applications (ligne en cours et suivante), déplaçable, redimensionnable, opacité réglable, synchronisée avec le décalage de l'appareil
- Service de premier plan qui suit la chanson en cours même quand Rushio est fermé
- Paroles en notification (au choix avec la fenêtre flottante) : ligne en cours et suivante, mise à jour à chaque ligne sans son ni vibration, visible sur l'écran de verrouillage
- Écran « Fonctionnement en arrière-plan » : désactiver l'optimisation de la batterie, ouvrir le lancement d'applications (Honor/MagicOS, Huawei, Xiaomi…), conseils pour que Rushio reste actif

## Rush 7.1.1
- Removed share card limit 
- Miscellaneous Updates and Fixes

## Rush 7.1.0
- Removed unnecessary calls to Genius API
- Removed dead APIs
- Miscellaneous Updates and Fixes
- Updated Translations

## Rush 7.0.7
- Fixed crashes in lower android versions
- Removed Onboarding and Changelog Sheets
- Miscellaneous Updates and Fixes

## Rush 7.0.5
- Updated Translations
- Miscellaneous Updates and Fixes

## Rush 7.0.3
- Fixed Audio dependent Lyrics Backgrounds not applying

## Rush 7.0.2
- Revamped Navigation, Animations Several UI components
- Added better support for expanded screens
- Fixed inconsistent share cards based on device
- Updated Lyrics customization defaults
- Updated Share card customization defaults

## Rush 6.7.1
- Revamped save page toolbar

## Rush 6.6.1
- Updated Translations

## Rush 6.6.0
- Updated Translations
- Improved song detection and fetching
- Miscellaneous UI improvements

## Rush 6.5.3
- Updated Translations
- Fixed lyrics not loading #296
- Fixed crashes on Lyrics correct dialog
- Fixed duplicate results in Lyrics correct dialog

## Rush 6.5.2
- Updated Translations
- Improved Lyrics correct dialog.

## Rush 6.5.1
- Fixed lyrics search issues
- Miscellaneous UI fixes

## Rush 6.5.0
- Updated Translations
- Added new Card Style - Brat
- Improved existing card styles
- Removed underused customization options
- Miscellaneous UI fixes

## Rush 6.4.2
- Updated Translations
- Performance improvements
- Fixed UI hiding issues in Lyrics Page

## Rush 6.4.1
- Updated Translations
- Fixed slow lyrics search faster

## Rush 6.4.0
- Added option to hide controls in Lyrics page after 5 seconds
- Enable it from Lyrics Customizations
- Miscellaneous UI Fixes

## Rush 6.3.3
- Improved usability in Landscape and Desktop screen sizes
- Miscellaneous UI Fixes

## Rush 6.3.2
- Updated Translations
- Fixed crashes while remaking Messy Card

## Rush 6.3.1
- Fixed crashes in Synced Lyrics page

## Rush 6.3.0
- Updated Translations

## Rush 6.2.10
- Improved Romanization accuracy. Thanks to @rbelem
- Updated Translations
- Fixed Search Sheet UI overlapping
- Added angles and more variations to messy card. 
- Added Refresh button to regenerate Messy Card
- Miscellaneous UI fixes

## Rush 6.2.01
- Fixed UI bug in share cards

## Rush 6.2.0
- Fixed Expressive Syllables causing crashes
- Fixed UI inconsistencies

## Rush 6.1.15
- (Android 12+) Fixed Material Theme not respecting System Colors
- Updated Translations
- Fixed Hebrew Translations not applying
- Miscellaneous UI improvements

## Rush 6.1.14
- Updated Translations
- (Android 13+) Added Language Picker in Settings

## Rush 6.1.13
- Fixed local search results not loading Lyrics

## Rush 6.1.11
- Added Fullscreen mode for share cards. Thanks to @BobbyEsp (#242)
- Added ability to scroll while viewing Synced Lyrics. Auto-Scroll will resume in 3 seconds
- Optimised App size
- Fixed miscellaneous UI issues
- Updated Translations

## Rush 6.0.0
- Added lyrics romanizations. Thanks to @rbelem.
- Supported languages: Japanese, Korean, Chinese, Hindi, Punjabi and other Cyrillic Languages
- Enable Romanization from Lyrics Customizations
- Updated Translations

## Rush 5.9.1
- Improved Expressive Syllables, Enable it from Lyrics Customizations 
- Translation Updates
- Redesigned Messy Share Card with Flex Fonts
- Added Animated Previews for Synced Lyrics
- Fixed SearchSheet UX issues with keyboard

## Rush 5.9.0
- [Experimental] Added Expressive Syllable highlighting, Enable it from Lyrics Customizations 
- Translation Updates
- Misc UI Improvements in bottom sheets

## Rush 5.8.2
- Fixed misleading Syllable Synced Lyrics for some tracks
- Removed word by word highlight from line synced lyrics
- Updated Translations

## Rush 5.8.1
- Miscellaneous updates and fixes

## Rush 5.8.0
- Added Expressive motion scheme to animations
- Updated Translations

## Rush 5.7.3
- Improved App Navigation Experience

## Rush 5.7.2
- Fixed Material Theme not Working #230

## Rush 5.7.1
- Fixed auto lyrics fetching issues
- Fixed miscellaneous song sync bugs
- Updated Lyrics Customisation UI
- Updated Share Cards UI

## Rush 5.7.0
- Added support for TTML (Syllable Synced) Lyrics
- Improved Synced Lyrics Animation
- Added in app Changelogs

## Rush 5.6.0
- Fixed LRCLIB api not responding sometimes (hopefully)
- Misc updates and fixes
- Added flex fonts for headlines, displays and sharecards

## Rush 5.5.2
- Fixed synced lyrics not loading
- Moved to jdk 21 (fdroid builds)

## Rush 5.5.1
- Fixed text wrap on larger DPI (#220)
- Added default font option (#212)

## Rush 5.5.0
- Translation updates
- Redid all icons. [reason](https://x.com/riggaroo/status/1971485475866341530?s=20)
- Fixed Synced lyrics anchoring to the bottom (#199)
- Added Scale to the current synced lyric
- Added google sans font (#206)
- Added Controls for media (#204)
- Added Action bar in landscape mode (#210)
- All screens are now usable in landscape mode and larger devices.

## Rush 5.4.2
- Trimmed unneeded network calls

## Rush 5.4.0
- Fixed local search results not appearing #201
- Prefill title and artist in correct dialog #198
- Prettified settings and saved page ui 
- fixed misc bugs
- removed about libraries

## Rush 5.3.0
- **Improved autosearch**, a button will be displayed when exact match is not found 
- Added Rush Logo to cards and a toggle for that
- misc bug fixes, updated deps, metadata and fixed deprecations

## Rush 5.2.0
- Optimised share cards
- Optimised Backup ui and fix crashes
- made padding between lines scale with lineheight
- polished onboarding
- Added new Lyrics background - curves
- Made errors copyable

## Rush 5.1.1
- Fixed saved page lagging for huge lists
- Fixed inconsistent ui in onboarding

## Rush 5.1.0
- Added new Audio sensitive lyrics backgrounds Gradient and Wave (#182)
- Added pulsating buttons on audio sensitive backgrounds
- Misc Background Improvements