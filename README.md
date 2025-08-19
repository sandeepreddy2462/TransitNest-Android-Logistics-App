## 🚚 Transit Nest

Transit Nest is a peer-to-peer logistics platform that connects senders with travelers who have spare capacity in their trips. The Android client (Java/XML) talks to a lightweight PHP API backed by MySQL to handle authentication, listings, matching, and parcel tracking.

---

## 🔧 Tech Stack

- **Mobile**: Android (Java, XML), Android Studio
- **Backend**: PHP (Apache via XAMPP or any LAMP stack)
- **Database**: MySQL (phpMyAdmin for local dev)
- **API**: REST over HTTPS
- **Build/Tools**: Gradle (Android), XAMPP (Windows local)

---

## 📱 Features

- **User Accounts**: Sign up, sign in, password reset, session management, profile edit, logout
- **Home Tabs**: Profile, Notifications, and navigation to `Home`, `My Trips`, `My Parcels`
- **Sender Flow**: Create parcel with type, item, weight, from/to, receiver details
- **Traveler Flow**: Publish trip with from/to, date/time, transport mode, capacity
- **Discovery**: Search matching senders or travelers; request/accept match
- **Tracking**: Status updates in `My Trips` and `My Parcels`; notifications
- **Security**: Token-based auth over HTTPS (recommend reverse proxy + TLS in production)

---

## 🧭 Architecture Overview

- **Android App**: Screens and forms for senders/travelers; calls PHP endpoints via `Retrofit/HttpUrlConnection` (depending on your implementation).
- **PHP API**: Stateless REST endpoints for auth, parcels, trips, and matching. Simple controllers call data-access layer to MySQL.
- **MySQL**: Normalized schema for users, parcels, trips, matches, and notifications.

---

## 📁 Project Structure (reference)

Android app:
- `app/src/main/java/com/example/transitnest/`
  - `LoginActivity.java`, `RegisterActivity.java`, `HomeActivity.java`
  - `SendParcelActivity.java`, `CarryParcelActivity.java`
  - `DiscoverSenderActivity.java`, `DiscoverBringerActivity.java`
  - `services/ApiClient.java`, `services/AuthService.java`, `models/*`
- `app/src/main/res/layout/`
  - `activity_login.xml`, `activity_register.xml`, `activity_send_parcel.xml`, etc.
- `app/src/main/res/values/`
  - `strings.xml`, `colors.xml`, `styles.xml`

Backend (suggested layout if using XAMPP):
- `htdocs/transitnest/`
  - `public/index.php`
  - `public/.htaccess`
  - `src/config.php`
  - `src/routes/*.php`
  - `src/controllers/*.php`
  - `src/models/*.php`
  - `src/lib/*.php`
  - `storage/logs/`, `storage/uploads/`

---

## ✅ Prerequisites

- Windows 10/11, macOS, or Linux
- Android Studio (latest stable), Android SDK 24+
- JDK 8+ (matching Android Studio’s requirement)
- XAMPP (Apache + PHP 8+ + MySQL 5.7+/8.0+)
- Git

---

## 🚀 Quickstart

### 1) Backend (PHP + MySQL)

1. Install and start XAMPP (Apache and MySQL).
2. Create a MySQL database:
   - Name: `transitnest`
   - Charset: `utf8mb4`, Collation: `utf8mb4_unicode_ci`
3. Create a user with privileges on `transitnest`.
4. Place backend code under `C:\xampp\htdocs\transitnest` (Windows) or `/Applications/XAMPP/htdocs/transitnest` (macOS).
5. Configure environment:

Create `src/config.php` (or `.env` if you use a loader) with your DB credentials:
```php
<?php
return [
	'db_host' => '127.0.0.1',
	'db_port' => 3306,
	'db_name' => 'transitnest',
	'db_user' => 'transit_user',
	'db_pass' => 'change_me',
	'jwt_secret' => 'replace-with-strong-random-secret',
	'app_env' => 'local',
	'app_url' => 'http://localhost/transitnest/public'
];
```

6. Import minimal schema (you can extend later):
```sql
CREATE TABLE IF NOT EXISTS users (
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	email VARCHAR(150) UNIQUE,
	phone VARCHAR(30) UNIQUE,
	password_hash VARCHAR(255) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS trips (
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	from_city VARCHAR(120) NOT NULL,
	to_city VARCHAR(120) NOT NULL,
	departure_at DATETIME NOT NULL,
	transport_mode VARCHAR(60) NOT NULL,
	capacity_kg DECIMAL(6,2) DEFAULT 0,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS parcels (
	id INT AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	item VARCHAR(120) NOT NULL,
	weight_kg DECIMAL(6,2) NOT NULL,
	from_city VARCHAR(120) NOT NULL,
	to_city VARCHAR(120) NOT NULL,
	receiver_name VARCHAR(120),
	receiver_phone VARCHAR(40),
	status VARCHAR(40) DEFAULT 'created',
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS matches (
	id INT AUTO_INCREMENT PRIMARY KEY,
	parcel_id INT NOT NULL,
	rip_id INT NOT NULL,
	status VARCHAR(40) DEFAULT 'pending',
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (parcel_id) REFERENCES parcels(id),
	FOREIGN KEY (trip_id) REFERENCES trips(id),
	UNIQUE KEY uniq_parcel_trip (parcel_id, trip_id)
);
```

7. Verify PHP is serving:
- Visit `http://localhost/transitnest/public/health` (create a simple health route that returns `{"ok":true}`).

### 2) Android App

1. Open the Android project in Android Studio.
2. Set the API base URL (example in `services/ApiClient.java`):
```java
public final class ApiClient {
	private static final String BASE_URL = "http://10.0.2.2/transitnest/public/api/"; // Android emulator to host
	// ... build Retrofit/Http client here
}
```
- Use `10.0.2.2` for Android emulator, or your machine IP for a physical device on the same network.
3. Build and run on an emulator or device.

---

## 🌐 API Endpoints (example)

Base URL: `http://localhost/transitnest/public/api/`

- Auth
  - `POST /auth/register` — name, email/phone, password
  - `POST /auth/login` — email/phone, password
  - `POST /auth/logout` — invalidate token
- Profile
  - `GET /me` — current user
  - `PUT /me` — update profile fields
- Parcels
  - `POST /parcels` — create parcel
  - `GET /parcels` — list my parcels
  - `GET /parcels/search?from=&to=` — discovery for travelers
- Trips
  - `POST /trips` — publish trip
  - `GET /trips` — list my trips
  - `GET /trips/search?from=&to=` — discovery for senders
- Matches
  - `POST /matches` — request match (`parcel_id`, `trip_id`)
  - `PUT /matches/{id}` — accept/reject
- Notifications
  - `GET /notifications` — list notifications

Authentication: Bearer token in `Authorization` header for protected routes.

---

## 🔐 Security & Deployment Notes

- Always deploy behind HTTPS (use a reverse proxy like Nginx with TLS).
- Store hashed passwords (`password_hash()` in PHP), never plaintext.
- Keep `jwt_secret` out of source control; use env vars in production.
- Harden Apache/PHP: disable directory listing, set proper `public` webroot.

---

## 🧪 Testing

- Use Postman/Insomnia collection to test endpoints locally.
- Add instrumented/UI tests in Android for critical flows (login, create parcel, publish trip, match accept).

---

## 🛠 Troubleshooting

- Android cannot reach `localhost`: use `10.0.2.2` (emulator) or your LAN IP (device).
- CORS/API 404: ensure your routes are under `public/index.php` and `.htaccess` rewrites are enabled.
- DB connection failures: recheck `src/config.php` host/user/pass and XAMPP MySQL status.

---

## 🤝 Contributing

- Fork, create a feature branch, commit with clear messages, open a PR.
- Keep code readable, handle errors, and include basic tests where applicable.

---

## 🗺 Roadmap

- In-app payments and escrow
- In-app chat between sender and traveler
- Rich parcel status timeline with proofs of handoff
- Ratings/reviews and dispute resolution
- Push notifications (FCM)

---

## 📄 License

MIT — see `LICENSE` for details.
