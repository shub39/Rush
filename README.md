
[<img src="fastlane/metadata/android/en-US/images/icon200x200.png" alt="Rush" width="200"/>]()

# Rush
### Search, save and share lyrics like Spotify! 

> [<img src="https://ziadoua.github.io/m3-Markdown-Badges/badges/Android/android2.svg">]()
> [<img src="https://ziadoua.github.io/m3-Markdown-Badges/badges/AndroidStudio/androidstudio3.svg">]()
> [<img src="https://ziadoua.github.io/m3-Markdown-Badges/badges/Kotlin/kotlin1.svg">]()

> ### Stats and Socials
> [<img src="https://m3-markdown-badges.vercel.app/stars/1/3/shub39/Rush">]()
> [<img src="https://m3-markdown-badges.vercel.app/issues/1/2/shub39/Rush">]()
> [<img src="https://ziadoua.github.io/m3-Markdown-Badges/badges/Discord/discord2.svg">](https://discord.gg/https://discord.gg/nxA2hgtEKf)

> ### Get From
> [<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/com.shub39.rush/)
> <a href="https://apt.izzysoft.de/packages/com.shub39.rush/latest"><img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" height="80"></a>
> [<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=com.shub39.rush.play)
> ### Or Get Latest [Release](https://github.com/shub39/Rush/releases) from Github

## Screenshots 📱

| ![1](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png) | ![2](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png) |
|:-------------------------------------------------------------------:|:-------------------------------------------------------------------:|
| ![3](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png) | ![4](fastlane/metadata/android/en-US/images/phoneScreenshots/4.png) |
| ![5](fastlane/metadata/android/en-US/images/phoneScreenshots/5.png) | ![6](fastlane/metadata/android/en-US/images/phoneScreenshots/6.png) | 

## Features ✨
>- [x] Search Lyrics
>- [x] Download Lyrics
>- [x] Share Lyrics
>- [x] Customisations
>- [x] Auto-fill current playing song in search 
>- [x] Synced Lyrics
>- [x] Batch download lyrics
>- [x] Import and Export saved lyrics

## Why ❔
Spotify removed its feature to see and share lyrics from its free tier just to bring it back again. 
So, I made this app to get and store lyrics for my favorite songs from Genius and share them like Spotify,
all in Material 3 look. As an audiophile, This has now become my way to listen to complete albums with lyrics without 
dealing with genius's "UI".

## Translations 🔠

|            LANGUAGE            | UPDATED BY                                               |
|:------------------------------:|:---------------------------------------------------------|
|            English             | Myself                                                   |
|            Japanese            | [kuragehimekuara1](https://github.com/kuragehimekurara1) |
|            Italian             | [colozzacristian](https://github.com/colozzacristian)    |
|            Turkish             | [mikropsoft](https://github.com/mikropsoft)              |
|             French             | [Valyyme](https://github.com/Valyyme)                    |
|      Brazilian Portuguese      | [luxdev01](https://github.com/luxdev01)                  | 
| Simplified/Traditional Chinese | [AC19970](https://github.com/AC19970)                    |
|             German             | [Irgendeinwer](https://github.com/Irgendeinwer)          |
|            Persian             | [alr86](https://github.com/alr86)                        |
|            Spanish             | [WaifuPX-DG](https://github.com/WaifuPX-DG)              |


## References and Inspiration 💡

>- [Fastlyrics](https://github.com/TecCheck/FastLyrics)
>- [SongSync](https://github.com/Lambada10/SongSync)
>- [LrcLib](https://lrclib.net/) 
>- Spotify Lyrics UI

## Tech Stack ⚒️

>- [Ktor](https://github.com/ktorio/ktor) for networking
>- [Ksoup](https://github.com/fleeksoft/ksoup) for parsing html
>- Database using [ROOM](https://developer.android.com/jetpack/androidx/releases/room)
>- MVI architecture
>- Dependency injection using [Koin](https://insert-koin.io/docs/reference/koin-compose/compose/)
>- [Landscapist](https://github.com/skydoves/landscapist) for Images
>- [Colorpicker-compose](https://github.com/skydoves/colorpicker-compose) for the color picker
>- [MaterialKolor](https://github.com/jordond/MaterialKolor) for generating color palettes
>- [Hypnotic Canvas](https://mikepenz.github.io/HypnoticCanvas/) for the mesh gradient background

## How it works 🤔

Rush is not just a lyrics app. Users can share lyrics as cards too, for that it needs to depend on Genius API Album art
and other metadata. While genius provides accurate lyrics, It doesn't provide timed lyrics so it fetches that from LRCLIB
using the title and artist info provided by genius. Users can correct lyrics from LRCLIB with the correct lyrics feature.

Below are some Flowcharts I made so almost everyone can understand how Rush works

### Searching 🔍️
```mermaid
flowchart
    n1@{ shape: "rounded", label: "App" }
n1 ---|"Search query"| n2@{ shape: "circle", label: "Genius API" }
n2 ---|"Entities containing IDs"| n1
style n2 color:#000000,fill:#FFDE59
```

### Fetching 🎣
```mermaid
flowchart
	subgraph s1["Background"]
		n1@{ shape: "diam", label: "Song exists in database?" }
        n5["Song title, Artist, Genius lyrics, Synced lyrics (if available) "]
	end
	subgraph s2["UI"]
		n6["ID and Genius URL of selected song "]
		n2["Song lyrics displayed"]
	end
	n3@{ shape: "circle", label: "LRCLIB API" }
	n4@{ shape: "circle", label: "Scraping" }
    n6 --- n1
    n1 ---|Yes| n2
    n1 ---|No| n4
    n5 ---|After saving to database| n2 
    n4 ---|Genius Lyrics| n5
    n5 ---|Title, Artist| n3
    n3 ---|Lyrics, Synced Lyrics| n5
	style n3 color:#FFFFFF,fill:#5E17EB
	style n4 color:#FFFFFF,fill:#FF3131
```

## Support 💕
> ### If you love this app, consider supporting. I'm a undergrad trying to get some pocket money 🥹
> [<img alt="bmc-button" src="https://ziadoua.github.io/m3-Markdown-Badges/badges/BuyMeACoffee/buymeacoffee3.svg">](https://www.buymeacoffee.com/shub39)
