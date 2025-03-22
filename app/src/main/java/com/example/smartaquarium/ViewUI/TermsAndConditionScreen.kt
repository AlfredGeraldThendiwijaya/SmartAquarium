package com.example.smartaquarium.ViewUI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartaquarium.ui.theme.navyblue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Syarat dan Ketentuan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Syarat & Ketentuan Penggunaan",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = navyblue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                    1. Pengenalan
                    Selamat datang di SmartAquarium! Dengan menggunakan aplikasi ini, Anda menyetujui semua syarat dan ketentuan yang tercantum di bawah ini. Jika Anda tidak setuju dengan salah satu bagian dari syarat ini, harap tidak menggunakan aplikasi ini.
                    
                    2. Deskripsi Layanan
                    SmartAquarium adalah aplikasi yang membantu pengguna dalam mengelola dan memantau kondisi akuarium secara digital. Aplikasi ini menyediakan fitur pemantauan kualitas air, pencatatan data, serta notifikasi untuk pemeliharaan rutin.
                    
                    3. Hak dan Kewajiban Pengguna
                    - Pengguna bertanggung jawab atas keakuratan informasi yang mereka masukkan dalam aplikasi.
                    - Pengguna tidak diperbolehkan menggunakan aplikasi untuk tujuan yang melanggar hukum.
                    - Pengguna tidak diperkenankan menyebarkan, meretas, atau memodifikasi aplikasi tanpa izin resmi dari pengembang.
                    - Pengguna wajib menjaga keamanan akun mereka dan tidak membagikan kredensial login kepada pihak lain.
                    
                    4. Hak dan Kewajiban Pengembang
                    - Pengembang berhak melakukan perubahan atau pembaruan aplikasi tanpa pemberitahuan sebelumnya.
                    - Pengembang tidak bertanggung jawab atas kesalahan atau kerugian akibat penggunaan aplikasi.
                    - Pengembang akan berusaha memastikan keamanan dan stabilitas aplikasi, tetapi tidak menjamin layanan bebas dari gangguan atau kesalahan.
                    
                    5. Privasi dan Keamanan
                    - Data pengguna akan dijaga kerahasiaannya dan tidak akan dijual atau dibagikan kepada pihak ketiga tanpa izin pengguna.
                    - Aplikasi dapat mengumpulkan data seperti riwayat penggunaan untuk meningkatkan layanan.
                    - Pengguna bertanggung jawab menjaga keamanan akun mereka, termasuk kata sandi dan kredensial login.
                    
                    6. Batasan Tanggung Jawab
                    - SmartAquarium tidak bertanggung jawab atas kerusakan atau kehilangan yang disebabkan oleh kesalahan pengguna dalam penggunaan aplikasi.
                    - Aplikasi ini bersifat sebagai alat bantu dan tidak menggantikan perawatan akuarium secara manual.
                    - Pengguna bertanggung jawab penuh atas keputusan yang mereka buat berdasarkan data yang disajikan dalam aplikasi.
                    
                    7. Perubahan Syarat dan Ketentuan
                    Kami dapat memperbarui syarat dan ketentuan ini dari waktu ke waktu. Jika ada perubahan signifikan, kami akan memberi tahu pengguna melalui aplikasi atau email yang terdaftar.
                    
                    8. Kontak untuk Bantuan
                    Jika Anda memiliki pertanyaan atau memerlukan bantuan, silakan hubungi kami melalui email: alfred.gerald@student.pradita.ac.id
                    
                    Dengan menggunakan aplikasi ini, Anda dianggap telah membaca, memahami, dan menyetujui semua ketentuan yang berlaku.
                """.trimIndent(),
                fontSize = 14.sp
            )
        }
    }
}
