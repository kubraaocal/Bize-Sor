package com.example.bizesor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GirisActivity extends AppCompatActivity {

    ListeSpinner listeSpinner = new ListeSpinner();
    SinavAdapter adapter;

    private Button buttonGiris;
    private EditText editGirisMail, editGirisSifre;
    private TextView txtKayitOlLink;
    private Spinner spnSinav;

    private FirebaseAuth mAuth;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        mAuth = FirebaseAuth.getInstance();


        listeSpinner.initList();


        Spinner spinner = findViewById(R.id.spinner_girisSinav);

        adapter = new SinavAdapter(this, listeSpinner.sinavItemArrayList);
        spinner.setAdapter(adapter);

        buttonGiris = findViewById(R.id.btn_girisYap);
        editGirisMail = findViewById(R.id.edit_girisMail);
        editGirisSifre = findViewById(R.id.edit_girisSifre);
        txtKayitOlLink = findViewById(R.id.txt_kayitOlLink);
        spnSinav = findViewById(R.id.spinner_girisSinav);


        alertDialog = new AlertDialog.Builder(this).create();

        txtKayitOlLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KullaniciKayitOlActivityGonder();
            }
        });

        buttonGiris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KullaniciGirisKontrol();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser baglantiKullanici = mAuth.getCurrentUser();

        if (baglantiKullanici != null) {
            KullaniciMainActivityGonder();
        }
    }

    private void KullaniciGirisKontrol() {
        String email = editGirisMail.getText().toString();
        String sifre = editGirisSifre.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Lütfen email giriniz..", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(sifre)) {
            Toast.makeText(this, "Lütfen şifre giriniz..", Toast.LENGTH_SHORT).show();
        } else {
            alertDialog.setTitle("Giriş yapılıyor");
            alertDialog.setMessage("Lütfen bekleyin");
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        KullaniciMainActivityGonder();

                        Toast.makeText(GirisActivity.this, "Giriş yapıldı..", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(GirisActivity.this, "Giriş yapılamadı..", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                }
            });
        }

    }

    private void KullaniciMainActivityGonder() {
        Intent mainIntent = new Intent(GirisActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void KullaniciKayitOlActivityGonder() {
        Intent kayitOlIntent = new Intent(GirisActivity.this, KayitOlActivity.class);
        startActivity(kayitOlIntent);
    }
}