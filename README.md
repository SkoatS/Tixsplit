# TixSplit Automation Testing Framework - The Final Project

Framework automation testing berbasis **Selenium WebDriver**, **TestNG**, **RestAssured**, dan **Gradle (Kotlin DSL)** untuk platform **[TixSplit](https://tixsplit.dibimbing.id/)** berdasarkan test specification pada `./spec/TestCase.csv`.

---

## 🏗️ Arsitektur & Pola Desain

1. **Page Object Model (POM)**:
   - Pemisahan elemen UI dan aksi pengguna ke dalam class halaman tersendiri (`src/main/java/pages/`).
   - [`BasePage`](file:///D:/QA/The%20Final%20Project/src/main/java/commons/BasePage.java) menangani dynamic element handling via `clickWithRetry(By locator)`, explicit wait (`WebDriverWait`), dan scroll handling untuk mencegah `StaleElementReferenceException` maupun `ElementClickInterceptedException`.

2. **Parallel Execution & Thread Isolation**:
   - [`DriverManager`](file:///D:/QA/The%20Final%20Project/src/test/java/core/DriverManager.java) menggunakan `ThreadLocal<WebDriver>` untuk memastikan setiap thread uji memiliki instance browser yang sepenuhnya independen tanpa konflik state.
   - Konfigurasi [`testng.xml`](file:///D:/QA/The%20Final%20Project/src/test/resources/testng.xml) diset `parallel="methods"` dengan `thread-count="3"`.

3. **Hybrid UI & API Testing**:
   - Mendukung end-to-end testing melalui antarmuka web (Chrome/Firefox) serta validasi API simultan (RestAssured) untuk skenario beban konkurensi (anti-oversell parallel checkout) dan verifikasi formula matematis fee/kode unik.

4. **Retry Mechanism**:
   - Menggunakan [`RetryAnalyzer`](file:///D:/QA/The%20Final%20Project/src/test/java/core/RetryAnalyzer.java) untuk menangani kemungkinan test flaky pada jaringan publik.

---

## 📋 Pemetaan Cakupan Test Case (`./spec/TestCase.csv`)

| Test Case ID | Nama Test Case | Tipe Uji | Class Pengujian | Status |
|---|---|---|---|---|
| **TC-AUTH-002** | Registrasi dengan email duplikat | Negative (Critical) | [`AuthTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/AuthTests.java) | ✅ PASSED |
| **TC-AUTH-003** | Login email + password valid | Positive (Critical) | [`AuthTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/AuthTests.java) | ✅ PASSED |
| **TC-AUTH-004** | Login kredensial salah | Negative (Critical) | [`AuthTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/AuthTests.java) | ✅ PASSED |
| **TC-COM-001** | Membuat komunitas baru | Positive (High) | [`CommunityTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/CommunityTests.java) | ✅ PASSED |
| **TC-EVT-001** | Membuat event berbayar (FIXED) PUBLIC | Positive (Critical) | [`EventTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/EventTests.java) | ✅ PASSED |
| **TC-EVT-002** | Gagal membuat event jika field wajib tanggal kosong | Negative (High) | [`EventTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/EventTests.java) | ✅ PASSED |
| **TC-PAY-001** | RSVP Event Gratis berhasil | Positive (Critical) | [`PaymentTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/PaymentTests.java) | ✅ PASSED |
| **TC-PAY-002** | RSVP Event Gratis saat kuota penuh | Negative (Critical) | [`PaymentTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/PaymentTests.java) | ✅ PASSED |
| **TC-PAY-003** | Join kembali event yang sudah dibayar dan di-cancel | Positive (Critical) | [`PaymentTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/PaymentTests.java) | ✅ PASSED |
| **TC-PAY-004** | Anti-Oversell Checkout Serentak (5 users parallel) | Edge Case (Critical) | [`ParallelCheckoutTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/ParallelCheckoutTests.java) | ✅ PASSED |
| **TC-PAY-005** | Kalkulasi Admin Fee (5%) & 3-Digit Kode Unik | Edge Case (Critical) | [`PaymentTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/PaymentTests.java) | ✅ PASSED |
| **TC-PAY-006** | Klik "Saya sudah membayar" lewat batas waktu (410) | Negative (Critical) | [`PaymentTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/PaymentTests.java) | ✅ PASSED |
| **TC-PAY-008** | Host confirm pembayaran | Edge Case (Critical) | [`PaymentTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/PaymentTests.java) | ✅ PASSED |
| **TC-SB-003** | Pembagian Split Bill sisa desimal ganjil (+sisa 1) | Edge Case (Critical) | [`SplitBillTests`](file:///D:/QA/The%20Final%20Project/src/test/java/tests/SplitBillTests.java) | ✅ PASSED |

---

## 📁 Struktur Direktori

```
D:\QA\The Final Project/
├── build.gradle.kts                      # Dependensi Gradle & TestNG task
├── settings.gradle                       # Nama project root
├── gradlew & gradlew.bat                 # Gradle wrapper executable
├── spec/
│   └── TestCase.csv                      # Spesifikasi Test Case awal
└── src/
    ├── main/java/
    │   ├── commons/
    │   │   └── BasePage.java             # Base Page Object & dynamic retry logic
    │   └── pages/
    │       ├── SignInPage.java           # Page Object untuk /sign-in
    │       ├── CreateAccountPage.java    # Page Object untuk /create-account
    │       ├── CommunityPage.java        # Page Object untuk /communities/new & details
    │       ├── CreateEventPage.java      # Page Object untuk /communities/{slug}/events/new
    │       ├── EventDetailsPage.java     # Page Object untuk /events/{id} (RSVP & status)
    │       ├── PaymentCheckoutPage.java  # Page Object untuk /events/{id}/pay/{paymentId}
    │       └── SplitBillPage.java        # Page Object untuk /tools/split-bill
    └── test/
        ├── java/
        │   ├── core/
        │   │   ├── BaseTests.java        # Lifecycle (@BeforeSuite, @BeforeMethod, @AfterMethod)
        │   │   ├── ConfigManager.java    # Property loader staging.properties
        │   │   ├── DriverManager.java    # ThreadLocal WebDriver manager
        │   │   └── RetryAnalyzer.java    # Auto-retry handler
        │   └── tests/
        │       ├── AuthTests.java
        │       ├── CommunityTests.java
        │       ├── EventTests.java
        │       ├── PaymentTests.java
        │       ├── ParallelCheckoutTests.java
        │       └── SplitBillTests.java
        └── resources/
            ├── config/
            │   └── staging.properties    # BaseUrl & credentials seeder
            ├── testng.xml                # Parallel execution suite config
            └── log4j2.xml                # Logging configuration
```

---

## 🚀 Cara Menjalankan Pengujian

### 1. Menjalankan Seluruh Test Suite (Headless & Parallel)
```bash
gradle test -Dheadless=true --no-daemon
```

### 2. Menjalankan Menggunakan GUI Browser (Interaktif)
```bash
gradle test --no-daemon
```

### 3. Menjalankan Test Class Tertentu
```bash
# Menjalankan Auth Tests
gradle test --tests "tests.AuthTests" -Dheadless=true --no-daemon

# Menjalankan Community Tests
gradle test --tests "tests.CommunityTests" -Dheadless=true --no-daemon

# Menjalankan Event Tests
gradle test --tests "tests.EventTests" -Dheadless=true --no-daemon

# Menjalankan Payment Tests
gradle test --tests "tests.PaymentTests" -Dheadless=true --no-daemon

# Menjalankan Parallel Concurrency Checkout
gradle test --tests "tests.ParallelCheckoutTests" -Dheadless=true --no-daemon

# Menjalankan Split Bill Calculation
gradle test --tests "tests.SplitBillTests" -Dheadless=true --no-daemon
```

### 4. Melihat Laporan Hasil Uji (HTML Report)
Setelah pengujian selesai, laporan lengkap dapat dibuka di browser:
`build/reports/tests/test/index.html`

