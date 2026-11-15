

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

### 4. Menjalankan Test Class Tertentu
```bash
.\gradlew.bat test "-PsuiteFile=src/test/resources/testng-api.xml" "-Denv=staging"
```


### 5. Melihat Laporan Hasil Uji (HTML Report)
Setelah pengujian selesai, laporan lengkap dapat dibuka di browser:
`build/reports/tests/test/index.html`

