package com.example.otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    EditText phonenumber,codeEnter;
    Button nextBtn;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker codePicker;
    String verificationID;
    PhoneAuthProvider.ForceResendingToken token;
    Boolean verificationInProgress =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        phonenumber=findViewById(R.id.phone);
        codeEnter=findViewById(R.id.codeEnter);
        progressBar=findViewById(R.id.progressBar);
        nextBtn=findViewById(R.id.nextBtn);
        state=findViewById(R.id.state);
        codePicker=findViewById(R.id.ccp);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verificationInProgress)
                {
                    if(!phonenumber.getText().toString().isEmpty() && phonenumber.getText().toString().length() == 10)
                    {
                        String phoneNum="+"+codePicker.getSelectedCountryCode()+phonenumber.getText().toString();
                        Log.d(TAG,"OnClick: Phone NO ->"+phoneNum);
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP..");
                        state.setVisibility(View.VISIBLE);
                        requestOTP(phoneNum);


                    }
                    else
                    {
                        phonenumber.setError("Phone number is not valid");
                    }
                }
                else{
                    String userOTP=codeEnter.getText().toString();
                    if(!userOTP.isEmpty()){

                        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,userOTP);
                        verifyAuth(credential);

                    }
                    else{
                        codeEnter.setError("Valid OTP require");
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(fAuth.getCurrentUser()!=null){
            progressBar.setVisibility(View.VISIBLE);
            state.setText("Checking");
            state.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }



    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkUserProfile();
                }
                else{
                    Toast.makeText(Register.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserProfile() {
        DocumentReference docref=fstore.collection("users").document(fAuth.getCurrentUser().getUid());
        docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else{
                    startActivity(new Intent(getApplicationContext(),AddDetails.class));
                    finish();

                }
            }
        });
    }
    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                codeEnter.setVisibility(View.VISIBLE);
                verificationID= s;
                token=forceResendingToken;
                nextBtn.setText("Verify");
                verificationInProgress=true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Register.this,"Cannot create account"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}