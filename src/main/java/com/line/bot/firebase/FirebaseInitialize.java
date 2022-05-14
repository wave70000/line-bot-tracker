package com.line.bot.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.line.bot.FirebaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitialize {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseInitialize.class);

    private static final String DATABASE_URL = "https://line-bot-money-tracker-default-rtdb.firebaseio.com/";
    private static DatabaseReference database;

    public static DatabaseReference initialize() {
        try {
            File file = new File(
                    FirebaseController.class.getClassLoader().getResource("serviceAccountKey.json").getFile()
            );
            FileInputStream serviceAccount = new FileInputStream(file);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);
            logger.info("Firebase initialized");
        } catch (IOException e) {
            System.out.println("ERROR: invalid service account credentials.");
            System.out.println(e.getMessage());

            System.exit(1);
        }

        database = FirebaseDatabase.getInstance().getReference();

        //TODO delete pls
        FirebaseCredential firebaseCredential = new FirebaseCredential();
        System.out.println(firebaseCredential.getType());
        System.out.println(firebaseCredential.getProject_id());

        return database;
    }

    public void test() {

        FirebaseCredential firebaseCredential = new FirebaseCredential();
        System.out.println(firebaseCredential.getType());

    }
}
