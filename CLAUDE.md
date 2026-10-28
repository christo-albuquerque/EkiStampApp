# App Specification: Eki Stamp & Station Explorer (Android / Kotlin)

## 1. Project Goal & Overview
Build a read-only Jetpack Compose Android app that displays Japanese train stations, rail lines, and their official Eki Stamps (駅スタンプ).

The web source (`japan-ekistamp.com`, catalog at `/en/list.html` or `/list.html`; alternative: `stamp.funakiya.com`) provides station names, line names, locations, and stamp images (mostly in Japanese). The app MUST intercept this raw Japanese data in the **Repository Layer**, translate text fields into English via Google ML Kit on-device translation, and persist the records into a local SQLite (Room) database.

The end-user interface MUST present station names, rail line names, prefectures, and stamp descriptions **100% in English** without exposing translation loading states or raw Japanese strings.

## 2. Tech Stack & Dependencies
* **Language:** Kotlin
* **UI:** Jetpack Compose (Material 3), MVVM with unidirectional data flow
* **Async:** Kotlin Coroutines + `StateFlow` / `SharedFlow`
* **Local DB:** Room (SQLite)
* **Networking & Scraping:** Retrofit2 / OkHttp3 / Jsoup (HTML table parsing)
* **Image Loading:** Coil 3 (disk caching enabled for stamp thumbnails & high-res images)
* **Translation:** Google ML Kit Translate (`com.google.mlkit:translate:17.0.2`)
* **Architecture:** Clean Architecture (UI -> ViewModel -> Repository -> Local/Remote DataSources)

## 3. Data Flow & Translation Interceptor

```
[ Web Data Source / Jsoup scraper ]
        │ emits Japanese DTOs
        ▼
[ EkiStampRepository ]
        │ 1. Check Room for cached record
        │ 2. If missing: pass text to TranslationManager
        │ 3. Store English + Japanese values in Room
        ▼
[ Room SQLite ]
        │ emits Flow<List<...>>
        ▼
[ Compose UI ] (renders strictly English)
```

### Translation Business Rules
1. **Station Names:** Romanize using Hepburn (e.g. 谷保 -> Yaho, 新宿 -> Shinjuku). Fall back to ML Kit if unmapped.
2. **Line Names & Prefectures:** Translate directly (南武線 -> Nambu Line, 東京都 -> Tokyo).
3. **Stamp Descriptions / Landmarks:** ML Kit on-device Japanese-to-English translation.

## 4. Database Schema (Room)

```kotlin
@Entity(tableName = "eki_stamps")
data class EkiStampEntity(
    @PrimaryKey val id: String,              // e.g. "JR_NAMBU_YAHO"
    val stationNameJa: String,
    val stationNameEn: String,
    val lineNameJa: String,
    val lineNameEn: String,
    val prefectureEn: String,
    val imageUrl: String,
    val stampDescriptionEn: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isCollected: Boolean = false,
    val isTranslated: Boolean = true
)
```

## 5. UI Layout Requirements (Jetpack Compose)

### Screen 1: `CatalogScreen`
* Search bar: filter by English station name, rail line, or prefecture.
* Category chips: filter by railway company (JR East, Tokyo Metro, Odakyu, ...) or region.
* Grid/list cards: Coil stamp thumbnail, English station name ("Yaho Station"), rail line tag ("JR Nambu Line").

### Screen 2: `DetailScreen`
* Zoomable full-resolution stamp image.
* English station name, Japanese kanji subtitle, rail line, prefecture.
* Stamp story / landmark info (translated description).
* Static map placeholder / coordinates card.

## 6. Execution Plan
1. **Gradle setup:** Compose, Material 3, Room, Retrofit/Jsoup, Coil, ML Kit Translate.
2. **Data & translation layer:** DTOs + Jsoup scraper; `TranslationManager.kt` (ML Kit, models download asynchronously in background); Room DB, DAO, `EkiStampEntity`.
3. **Repository:** `EkiStampRepositoryImpl` parses Japanese data, translates, caches in Room, emits clean `Flow` models.
4. **UI:** `CatalogScreen` and `DetailScreen`.
