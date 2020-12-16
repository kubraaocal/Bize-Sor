package com.example.bizesor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class KayitOlActivity extends AppCompatActivity {

    SinavAdapter adapter;
    ListeSpinner listeSpinner = new ListeSpinner();

    private Spinner spinner;
    private EditText editKullaniciIsim, editKullaniciSoyisim, editKullaniciMail, editKullaniciSifre;
    private Button btnKaydet;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans;

    String mevcutKullaniciID;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        mAuth = FirebaseAuth.getInstance();

        listeSpinner.initList();

        spinner = findViewById(R.id.activity_kayit_ol_spinner_sinav);
        editKullaniciIsim = findViewById(R.id.activity_kayit_ol_edit_isim);
        editKullaniciSoyisim = findViewById(R.id.activity_kayit_ol_edit_soyisim);
        editKullaniciMail = findViewById(R.id.activity_kayit_ol_edit_mail);
        editKullaniciSifre = findViewById(R.id.activity_kayit_ol_edit_sifre);
        btnKaydet = findViewById(R.id.activity_kayit_ol_btn_kaydet);

        alertDialog = new AlertDialog.Builder(this).create();


        adapter = new SinavAdapter(this, listeSpinner.sinavItemArrayList);
        spinner.setAdapter(adapter);

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OlusturYeniHesap();
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
        final String soyisim = editKullaniciSoyisim.getText().toString();
        final String sinav = spinner.getSelectedItem().toString();
        String email = editKullaniciMail.getText().toString();
        String sifre = editKullaniciSifre.getText().toString();
        if (TextUtils.isEmpty(isim) || TextUtils.isEmpty(soyisim) || TextUtils.isEmpty(email)
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
                        KullaniciBilgileriAl(isim, soyisim, sinav);
                        MainActivityGit();

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

    private void KullaniciBilgileriAl(String isim, String soyisim, String sinav) {

        mevcutKullaniciID = mAuth.getCurrentUser().getUid();
        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(mevcutKullaniciID);

        HashMap kullaniciMap = new HashMap();
        kullaniciMap.put("isim", isim);
        kullaniciMap.put("soyisim", soyisim);
        kullaniciMap.put("sinav", sinav);
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


        /*kullaniciReferans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.hasChild(mevcutKullaniciID)) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    private void MainActivityGit() {
        Intent mainActivity = new Intent(KayitOlActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }
}