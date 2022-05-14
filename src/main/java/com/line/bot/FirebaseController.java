package com.line.bot;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FirebaseController {

    private static final String DATABASE_URL = "https://line-bot-money-tracker-default-rtdb.firebaseio.com/";
    private static DatabaseReference database;

    public static void main(String[] args) {

        try {
            File file = new File(
                    FirebaseController.class.getClassLoader().getResource("serviceAccountKey.json").getFile()
            );
            FileInputStream serviceAccount = new FileInputStream(file);
//            FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            System.out.println("ERROR: invalid service account credentials.");
            System.out.println(e.getMessage());

            System.exit(1);
        }
        database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference userRef = database.child("group/netflix/member/member1");

        database.child("group").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError);

            }
        });

    }
}
