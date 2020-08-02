package com.codeplay.geoplay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codeplay.geoplay.R;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText otpDigit1;
    private EditText otpDigit2;
    private EditText otpDigit3;
    private EditText otpDigit4;
    private EditText otpDigit5;
    private EditText otpDigit6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        StringBuilder otp = new StringBuilder();
        progressBar = findViewById(R.id.progress_circular_2);

        otpDigit1 = findViewById(R.id.et_1);
        otpDigit2 = findViewById(R.id.et_2);
        otpDigit3 = findViewById(R.id.et_3);
        otpDigit4 = findViewById(R.id.et_4);
        otpDigit5 = findViewById(R.id.et_5);
        otpDigit6 = findViewById(R.id.et_6);

        EditText[] edit = {otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6};

        otpDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    otpDigit2.requestFocus();
                }
            }
        });
        otpDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    otpDigit3.requestFocus();
                }
            }
        });
        otpDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    otpDigit4.requestFocus();
                }
            }
        });
        otpDigit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    otpDigit5.requestFocus();
                }
            }
        });
        otpDigit5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    otpDigit6.requestFocus();
                }
            }
        });

        TextView checkVerificationCode = findViewById(R.id.check_verification_note);

        mAuth = FirebaseAuth.getInstance();

        String phonenumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);

        checkVerificationCode.setText("Please type the verification code sent\nto " + phonenumber);


        findViewById(R.id.verify).setOnClickListener(v -> {
            otp.append(otpDigit1.getText().toString().trim());
            otp.append(otpDigit2.getText().toString().trim());
            otp.append(otpDigit3.getText().toString().trim());
            otp.append(otpDigit4.getText().toString().trim());
            otp.append(otpDigit5.getText().toString().trim());
            otp.append(otpDigit6.getText().toString().trim());

            System.out.println(otp.toString());

            if (otp.length() == 0 || otp.length() < 6) {
                otpDigit1.setError("Enter OTP!");
                otpDigit1.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(otp.toString());
        });
    }


    private void verifyCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(OtpVerificationActivity.this, "Verified Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(OtpVerificationActivity.this,
                        task.getException().getMessage(),
                        Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(
                        @NonNull String s,
                        @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationId = s;
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(OtpVerificationActivity.this, "OTP has been sent", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        otpDigit1.setText(code.charAt(0));
                        otpDigit2.setText(code.charAt(1));
                        otpDigit3.setText(code.charAt(2));
                        otpDigit4.setText(code.charAt(3));
                        otpDigit5.setText(code.charAt(4));
                        otpDigit6.setText(code.charAt(5));
                        verifyCode(code);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(OtpVerificationActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            };
}