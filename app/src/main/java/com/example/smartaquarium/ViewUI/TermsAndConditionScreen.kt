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
                title = { Text("Terms and Conditions") },
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
                text = "Terms & Conditions of Use",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = navyblue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                    1. Introduction
                    Welcome to SmartAquarium! By using this application, you agree to all the terms and conditions outlined below. If you do not agree with any part of these terms, please refrain from using this application.
                    
                    2. Service Description
                    SmartAquarium is an application designed to help users manage and monitor aquarium conditions digitally. It offers features such as water quality monitoring, data recording, and routine maintenance notifications.
                    
                    3. User Rights and Responsibilities
                    - Users are responsible for the accuracy of the information they input into the app.
                    - Users must not use the app for any unlawful purposes.
                    - Users are not permitted to distribute, hack, or modify the app without official permission from the developer.
                    - Users must keep their account secure and not share their login credentials with others.
                    
                    4. Developer Rights and Responsibilities
                    - The developer reserves the right to modify or update the app without prior notice.
                    - The developer is not responsible for any errors or losses resulting from the use of the app.
                    - The developer will strive to ensure the app’s security and stability, but does not guarantee uninterrupted or error-free service.
                    
                    5. Privacy and Security
                    - User data will be kept confidential and will not be sold or shared with third parties without user consent.
                    - The app may collect usage history to improve services.
                    - Users are responsible for maintaining the security of their accounts, including passwords and login credentials.
                    
                    6. Limitation of Liability
                    - SmartAquarium is not liable for any damage or loss caused by user misuse of the app.
                    - The app is a supportive tool and does not replace manual aquarium care.
                    - Users are fully responsible for decisions made based on the data presented in the app.
                    
                    7. Changes to Terms and Conditions
                    These terms and conditions may be updated from time to time. If significant changes are made, we will notify users via the app or registered email.
                    
                    8. Contact for Support
                    If you have any questions or need assistance, please contact us via email: alfred.gerald@student.pradita.ac.id
                    
                    By using this app, you are deemed to have read, understood, and agreed to all applicable terms.
                """.trimIndent(),
                fontSize = 14.sp
            )
        }
    }
}
