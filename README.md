<img width="1920" height="1080" alt="Untitled design" src="https://github.com/user-attachments/assets/e6853316-174d-470f-a246-fe8f1716741e" />

# 🐟 Discus Care – Smart Aquarium Monitoring System

Discus Care adalah proyek sistem monitoring akuarium cerdas berbasis IoT dan Machine Learning. Sistem ini mampu memantau kualitas air dan mendeteksi kesehatan ikan discus secara otomatis melalui aplikasi Android.

## 🎯 Tujuan Proyek

Menciptakan sistem pemantauan akuarium yang terintegrasi antara perangkat keras (sensor, kamera) dan perangkat lunak (mobile app, backend, dan model ML), untuk membantu pemilik ikan discus dalam menjaga kualitas lingkungan air dan kesehatan ikan secara real-time.

## 🚀 Fitur Utama

- 🔍 Prediksi kualitas air (pH, suhu, TDS) hingga 7 hari ke depan menggunakan XGBoost
- 🧠 Deteksi ikan sehat/sakit dari gambar menggunakan CNN (ResNet50V2)
- 🌡️ Pembacaan sensor real-time: pH, suhu (DS18B20), dan TDS
- 📸 Pengambilan gambar otomatis via ESP32-CAM
- 📱 Aplikasi Android berbasis Jetpack Compose dengan grafik interaktif (MPAndroidChart)
- 🔧 Jadwal pakan otomatis menggunakan servo
- ☁️ Backend API menggunakan Firebase Functions dan Cloud Run

## 🧪 Teknologi yang Digunakan

| Komponen       | Teknologi                                      |
|----------------|------------------------------------------------|
| IoT Device     | ESP32, ESP32-CAM, Mikrokontroler ESP8266 (NodeMCU)|
| Sensor         | DFRobot pH, DS18B20 (Suhu), Sensor TDS         |
| ML Model       | XGBoost (forecast), ResNet50V2 (klasifikasi)   |
| Backend API    | Firebase Functions (Node.js + Express)         |
| Frontend App   | Android (Jetpack Compose, Kotlin)              |
| Penyimpanan    | Firebase Storage, Realtime Database            |

## 🗂️ Peran Saya

-Mengintegrasikan API prediksi kualitas air (XGBoost) ke aplikasi Android

-Menyusun dan melatih model prediksi suhu, pH, dan TDS menggunakan XGBoost

-Menyusun pipeline preprocessing data sensor untuk keperluan prediksi

-Membangun tampilan visualisasi grafik prediksi (MPAndroidChart) di aplikasi Android

-Menguji dan mengintegrasikan komunikasi IoT (sensor & kamera ESP32-CAM) melalui API

-Berperan dalam debugging dan optimasi data flow antara backend dan frontend

## 🖼️ Tampilan Aplikasi

<div style="display: flex; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/aa465c9f-8a8d-41ee-8522-9ed156718cb7" width="200"/>
  <img src="https://github.com/user-attachments/assets/f364410c-664c-4ebe-b072-498f9e07011c" width="200"/>
  <img src="https://github.com/user-attachments/assets/7b32a0df-91ec-4f77-92c2-80db01f8bd3a" width="200"/>
  <img src="https://github.com/user-attachments/assets/6b22fc52-8e67-48a8-8dc8-11ddfcd7c8d5" width="200"/>
</div>

## 📄 Catatan Tambahan

Proyek ini dikembangkan sebagai tugas akhir sarjana, dan dirancang untuk digunakan oleh pemilik ikan discus sebagai alat bantu pemantauan otomatis.
