package com.merteroglu.ots;

import android.content.Intent;
import android.graphics.Color;
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
import com.merteroglu.ots.Model.Driver;
import com.merteroglu.ots.Model.Student;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity{

    private FirebaseFirestore firestore;
    private EditText girisID,girisParola;
    private Button girisButton;
    private int flag = 0;
    private SweetAlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firestore = FirebaseFirestore.getInstance();

        girisID = findViewById(R.id.girisID);
        girisParola = findViewById(R.id.girisParola);
        girisButton = findViewById(R.id.girisButton);
        girisID.setText("512256");
        girisParola.setText("12345");


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

                mDialog = new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.PROGRESS_TYPE);
                mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                mDialog.setTitleText("Giriş Yapılıyor");
                mDialog.setCancelable(false);
                mDialog.show();


                CollectionReference studentRef = firestore.collection("student");
                studentRef.whereEqualTo("tc",userid)
                        .whereEqualTo("password",parola)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size() != 0){
                            Student student = queryDocumentSnapshots.getDocuments().get(0).toObject(Student.class);
                            student.setId(queryDocumentSnapshots.getDocuments().get(0).getId());
                            Intent intent = new Intent(getApplicationContext(),StudentActivity.class);
                            intent.putExtra("StudentID",student.getId());
                            mDialog.dismissWithAnimation();
                            startActivity(intent);
                        }else{
                            Log.d("Login", "Student Failed");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Login Activity", "E: " + e.getMessage());
                        e.printStackTrace();
                        flag++;
                        openFailedLoginBox();
                    }
                });

                CollectionReference driverRef = firestore.collection("driver");
                driverRef.whereEqualTo("tc",userid)
                        .whereEqualTo("password",parola)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(queryDocumentSnapshots.size() != 0){
                                    Driver driver = queryDocumentSnapshots.getDocuments().get(0).toObject(Driver.class);
                                    driver.setId(queryDocumentSnapshots.getDocuments().get(0).getId());
                                    Intent intent = new Intent(getApplicationContext(),DriverActivity.class);
                                    intent.putExtra("DriverID",driver.getId());
                                    mDialog.dismissWithAnimation();
                                    startActivity(intent);
                                }else{
                                    Log.d("Login", "Driver Failed");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Login Activity", "E: " + e.getMessage());
                                e.printStackTrace();
                                flag++;
                                openFailedLoginBox();
                            }
                        });




            }

        });
    }

    private void openFailedLoginBox() {
        if (flag == 2) {
            mDialog.setTitleText("Başarısız");
            mDialog.setContentText("Giriş yapılamadı. id ve şifrenizi kontrol edin");
            mDialog.setConfirmText("Tamam");
            mDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            mDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    mDialog.dismissWithAnimation();
                }
            });
        }
    }
}
