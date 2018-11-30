package com.merteroglu.ots;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity{

    private FirebaseFirestore firestore;
    private EditText girisID,girisParola;
    private Button girisButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firestore = FirebaseFirestore.getInstance();

        girisID = findViewById(R.id.girisID);
        girisParola = findViewById(R.id.girisParola);
        girisButton = findViewById(R.id.girisButton);


        girisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userid = girisID.getText().toString();
                final String parola = girisParola.getText().toString();

                if(TextUtils.isEmpty(userid)){
                    Toast.makeText(LoginActivity.this, "Lütfen kullanıcı adınızı giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(parola)){
                    Toast.makeText(LoginActivity.this, "Lütfen parolanızı giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }


                CollectionReference studentRef = firestore.collection("student"); //.whereEqualTo("tc",userid).whereEqualTo("password",parola);
                studentRef.whereEqualTo("tc",userid)
                        .whereEqualTo("password",parola)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() != 0){
                            Toast.makeText(LoginActivity.this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "Giriş Başarısız", Toast.LENGTH_SHORT).show();
                        }

                       for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                           Log.d("Login Activity", "tc : " + documentSnapshot.get("tc"));
                           Log.d("Login Activity", "name : " + documentSnapshot.get("name"));

                       }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Login Activity", "E: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });
    }

}
