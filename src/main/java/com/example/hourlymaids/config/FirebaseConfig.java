package com.example.hourlymaids.config;//package com.asiantech.luna.cms.config;
//
//import com.asiantech.luna.config.TokenProvider;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Configuration
//@EnableConfigurationProperties
//public class FirebaseConfig {
//
//    private String credentialFile = "firebase-credential.json";
//
//    @Bean
//    FirebaseMessaging firebaseMessaging() throws IOException {
//        String filePath = TokenProvider.SECURITY_KEY_ROOT_DIR + File.separator + credentialFile;
//        GoogleCredentials googleCredentials = GoogleCredentials
//                .fromStream(new FileInputStream(filePath));
//        FirebaseOptions firebaseOptions = FirebaseOptions
//                .builder()
//                .setCredentials(googleCredentials)
//                .build();
//        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions);
//        return FirebaseMessaging.getInstance(app);
//    }
//}
