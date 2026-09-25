# MovieWatchlist

MovieWatchlist is a native Android app for keeping track of movies you want to watch and movies you have seen. You can organize movies into custom watchlists, record ratings and reviews, and explore your collection through local and Firestore queries. The app looks up movie details and posters through the [TMDB API](https://developer.themoviedb.org/docs/getting-started); movies can also be saved manually when a match is unavailable.

## Features

- Add, edit, and delete movies with a title, genre, release year, runtime, streaming platform, watched status, and personal rating.
- Search TMDB by title and select the correct result to fill in movie metadata and a poster. You can keep your own values or save a movie manually.
- Create watchlists and add movies to them.
- Browse watched movies, filter by genre, and view the movies in a selected watchlist.
- Create and browse reviews stored in Cloud Firestore; query reviews by movie title, rating, or watched date.
- Receive a daily reminder about movies you have not watched yet. Android 13 and newer ask for notification permission. A button on the main screen can queue a reminder immediately for testing.

## Tech stack

| Area | Technology |
| --- | --- |
| App | Kotlin, Android SDK, Android Views and View Binding |
| Architecture | ViewModel, LiveData, repositories, Kotlin coroutines |
| Local storage | Room database |
| Movie data and posters | TMDB API, Coil |
| Cloud reviews | Firebase Cloud Firestore |
| Reminders | WorkManager and Android notifications |

## Getting started

### Requirements

- Android Studio with an Android SDK that supports `compileSdk 36`
- JDK 17
- An Android device or emulator running Android 7.0 (API 24) or newer
- A [TMDB API key](https://developer.themoviedb.org/docs/getting-started) for movie lookup
- A Firebase project with Cloud Firestore for the reviews feature

### Set up the project

1. Clone the repository and open it in Android Studio:

   ```bash
   git clone https://github.com/TheodenKink/MovieWatchlistApp.git
   ```

2. In the [Firebase console](https://console.firebase.google.com/), register an Android app with package name `com.aris.moviewatchlist`, download its `google-services.json`, and place the file at `app/google-services.json`. Enable Cloud Firestore and configure its security rules for your intended use. The Google Services Gradle plugin expects this file even though it is intentionally excluded from Git.

3. Add your TMDB v3 API key to the root `local.properties` file:

   ```properties
   tmdbApiKey=YOUR_TMDB_API_KEY
   ```

   Alternatively, set the `TMDB_API_KEY` environment variable before building. The build reads `local.properties` first, then the environment variable. Android Studio may already have a `local.properties` file for your SDK path; add the line to that file rather than replacing it.

4. Sync Gradle, select the `app` configuration, and run it on a device or emulator. Internet access is needed for TMDB and Firestore; locally saved movie data is stored in Room.

> `local.properties` and `app/google-services.json` are ignored by Git. Keep personal credentials out of commits. The TMDB key is copied into the Android app at build time through `BuildConfig`, so excluding it from Git does **not** make it secret in a distributed APK. For a published app, assess key exposure and usage limits; a server-side proxy is needed if the credential must remain private.

## How it works

1. Tap the add button to enter a movie title. On save, the app searches TMDB and lets you choose between matching movies when there is more than one result.
2. Edit the movie to set its watched status, personal rating, platform, or other details. Open a saved movie to add it to an existing watchlist.
3. Use **Watchlists** to group movies and **Queries** to explore watched movies, genres, and watchlist contents.
4. Use **Reviews** for Firestore-backed reviews. The Firestore query screen can show highly rated reviews, reviews for one title, or reviews ordered by watched date.

Movie records and watchlists live in the on-device Room database. Reviews use Cloud Firestore and therefore require a configured Firebase project and network access. This repository does not include a preconfigured Firebase project or Firestore rules.

## TMDB attribution

This product uses the TMDB API but is not endorsed or certified by TMDB.

Movie metadata and poster images are obtained from [The Movie Database (TMDB)](https://www.themoviedb.org). TMDB's [attribution requirements](https://developer.themoviedb.org/docs/faq) also require the approved TMDB logo and the notice above in an **About** or **Credits** section inside an app that uses its API. This README provides repository attribution; an in-app attribution screen is not currently present in the source tree and should be added before distribution. Commercial use requires a separate arrangement with TMDB.

## Project status

This is a portfolio/learning project. No public `google-services.json`, TMDB credential, release APK, or software license is included in this repository.
