package com.example.bizesor.Activityler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bizesor.Adapterlar.SinavAdapter;
import com.example.bizesor.Modeller.ModelSinav;
import com.example.bizesor.R;
import com.example.bizesor.SpinnerListeler.SinavListeSpinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class KayitOlActivity extends AppCompatActivity {

    private SinavAdapter adapter;
    private SinavListeSpinner sinavListeSpinner = new SinavListeSpinner();

    private Spinner spinner;
    private EditText editKullaniciIsim, editKullaniciMail, editKullaniciSifre;
    private Button btnKaydet;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans;

    private String mevcutKullaniciID;
    private String sinavString;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        mAuth = FirebaseAuth.getInstance();

        sinavListeSpinner.initList();

        spinner = findViewById(R.id.activity_kayit_ol_spinner_sinav);
        editKullaniciIsim = findViewById(R.id.activity_kayit_ol_edit_isim);
        editKullaniciMail = findViewById(R.id.activity_kayit_ol_edit_mail);
        editKullaniciSifre = findViewById(R.id.activity_kayit_ol_edit_sifre);
        btnKaydet = findViewById(R.id.activity_kayit_ol_btn_kaydet);

        alertDialog = new AlertDialog.Builder(this).create();

        adapter = new SinavAdapter(this, sinavListeSpinner.modelSinavArrayList);
        spinner.setAdapter(adapter);

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OlusturYeniHesap();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ModelSinav modelSinav = (ModelSinav) parent.getItemAtPosition(position);
                sinavString = modelSinav.getSinavIsmi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser baglantiKullanici = mAuth.getCurrentUser();

        if (baglantiKullanici != null) {
            MainActivityGit();
        }
    }

    private void OlusturYeniHesap() {//Bu metotda isim,soyisim vs tutmuyor onları nasıl kayıt edeceğimi öğren
        final String isim = editKullaniciIsim.getText().toString();
        final String sinav = sinavString.toString();
        Log.i("aaaaaa", sinav);
        String email = editKullaniciMail.getText().toString();
        String sifre = editKullaniciSifre.getText().toString();
        if (TextUtils.isEmpty(isim) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(sifre) || TextUtils.isEmpty(sinav)) {
            Toast.makeText(this, "Lütfen boş alan bırakmayınız..", Toast.LENGTH_LONG).show();
        } else {
            alertDialog.setTitle("Hesap Oluşturuluyor");
            alertDialog.setMessage("Lütfen bir süre bekleyin..");
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Bunu if içine al ve user model ile model oluşturarak yap
                        KullaniciBilgileriAl(isim, sinav);
                        ProfilDuzenleActivityGit();

                        Toast.makeText(KayitOlActivity.this, "Kayıt oluşturuldu..", Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    } else {
                        String mesaj = task.getException().getMessage();
                        Toast.makeText(KayitOlActivity.this, "Hata: " + mesaj, Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    }
                }
            });
        }
    }

    private void KullaniciBilgileriAl(String isim, String sinav) {

        mevcutKullaniciID = mAuth.getCurrentUser().getUid();
        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(mevcutKullaniciID);

        HashMap kullaniciMap = new HashMap();
        kullaniciMap.put("isim", isim);
        kullaniciMap.put("sinav", sinav);
        kullaniciMap.put("resim", "null");
        kullaniciMap.put("pPuan", "0");
        kullaniciMap.put("pSoruSayisi", "0");
        kullaniciMap.put("hakkimda", "Merhaba, ben bizesor kullanıyorum");
        kullaniciReferans.updateChildren(kullaniciMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.i("Bilgiler Kayıt", "Bilgiler başarıyla kayıt edildi");
                } else {
                    String mesaj = task.getException().getMessage();
                    Log.i("Bilgiler Kayıt", "Sistem mesajı: " + mesaj);
                }
            }
        });

    }

    private void ProfilDuzenleActivityGit() {
        Intent profilDuzenleActivity = new Intent(KayitOlActivity.this, ProfilDuzenleActivity.class);
        profilDuzenleActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profilDuzenleActivity);
        finish();
    }
    private void MainActivityGit() {
        Intent mainIntent = new Intent(KayitOlActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}