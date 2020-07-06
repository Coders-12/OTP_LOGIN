package com.example.otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class AddDetails extends AppCompatActivity {
   FirebaseAuth firebaseAuth;
   FirebaseFirestore fstore;


    EditText firstname,lastname,email;
    Button saveBtn;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details2);
        firstname=findViewById(R.id.fname);
        lastname=findViewById(R.id.lname);
        email=findViewById(R.id.email);
        saveBtn=findViewById(R.id.savebtn);

        firebaseAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        userID=firebaseAuth.getCurrentUser().getUid();


        final DocumentReference docref= fstore.collection("users").document(userID);




        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!firstname.getText().toString().isEmpty()&&!lastname.getText().toString().isEmpty()&&!email.getText().toString().isEmpty())
                {
                    String first=firstname.getText().toString();
                    String last=lastname.getText().toString();
                    String userEmail=email.getText().toString();

                    Map<String,Object> user= new HashMap<>();
                    user.put("firstName",first);
                    user.put("lastName",last);
                    user.put("Email",userEmail);

                    docref.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(AddDetails.this,"Insertion Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(AddDetails.this,"All fields are required",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}