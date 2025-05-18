# 🚚 Transit Nest

**Transit Nest** is an Android-based logistics and delivery platform that connects **senders** and **travelers** for peer-to-peer parcel transportation. Built using Android Studio (Java/XML) with a PHP backend and MySQL database, it enables seamless interaction between users to send or carry parcels based on live routes and travel plans.

---

## 🔧 Tech Stack

- **Frontend**: Android Studio (Java, XML)
- **Backend**: PHP (hosted on XAMPP or web server)
- **Database**: MySQL (phpMyAdmin)
- **Connection**: HTTPS API (via PHP endpoints)

---

## 📱 Features

### 1. **User Authentication**
- Sign Up: Name, Email, Phone, Password
- Sign In: Email or Phone + Password
- Password Reset
- Session-based login tracking
- Profile update & logout

### 2. **Home Screen Tabs**
- Profile (Left) | Notifications (Right)
- Navigation Tabs: `Home`, `My Trips`, `My Parcels`
- Action Buttons: `Send a Parcel`, `Carry a Parcel`, `Discover Sender`, `Discover Bringer`

### 3. **Send a Parcel**
- Parcel type, item, weight, from-to locations
- Receiver details form
- Submits data to backend

### 4. **Carry a Parcel (For Travelers)**
- Travel form with from-to, date, time
- Transport mode input
- Publishes travel availability

### 5. **Discover Sender / Discover Bringer**
- Enter source and destination
- Show available senders or travelers
- Accept button for matching
- Payment, parcel status tracking
- Both users track delivery via `My Trips` and `My Parcels`

---

## 📁 Project Structure

The Android application code is organized as follows:

- **Java Files**: Located in `app/src/main/java/com/example/transitnest/`
  - Contains all activity classes, helpers, and business logic
  - Example: `LoginActivity.java`, `SendParcelActivity.java`, etc.

- **XML Layouts**: Located in `app/src/main/res/layout/`
  - Contains UI layout files for each screen
  - Example: `activity_login.xml`, `activity_register.xml`, etc.

- **Resources**:
  - `drawable/`: for images, shapes, icons
  - `values/`: for app-wide styles, strings, and color definitions
  - `mipmap/`: for app launcher icons

Each Java activity corresponds to a screen and is linked to its respective layout XML file for UI rendering.

