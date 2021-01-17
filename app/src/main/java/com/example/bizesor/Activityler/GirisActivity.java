package com.example.bizesor.Activityler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bizesor.Adapterlar.SinavAdapter;
import com.example.bizesor.Modeller.ModelSinav;
import com.example.bizesor.R;
import com.example.bizesor.SpinnerListeler.SinavListeSpinner;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class GirisActivity extends AppCompatActivity {

    SinavListeSpinner sinavListeSpinner = new SinavListeSpinner();
    SinavAdapter adapter;

    private Button buttonGiris;
    private EditText editGirisMail, editGirisSifre;
    private TextView txtKayitOlLink;
    private ImageView googleGirisButton;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans;

    private String mevcutKullaniciID;
    private AlertDialog alertDialog;

    //Google giriş
    private static final int RC_SIGN_IN=1;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="noldu";

    private String sinavString,kullaniciAdi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris);

        mAuth = FirebaseAuth.getInstance();


        sinavListeSpinner.initList();

        Spinner spinner = findViewById(R.id.spinner_girisSinav);

        adapter = new SinavAdapter(this, sinavListeSpinner.modelSinavArrayList);
        spinner.setAdapter(adapter);

        buttonGiris = findViewById(R.id.btn_girisYap);
        editGirisMail = findViewById(R.id.edit_girisMail);
        editGirisSifre = findViewById(R.id.edit_girisSifre);
        txtKayitOlLink = findViewById(R.id.txt_kayitOlLink);
        googleGirisButton=findViewById(R.id.btn_google_giris);


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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ModelSinav modelSinav = (ModelSinav) parent.getItemAtPosition(position);
                sinavString= modelSinav.getSinavIsmi();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);
        googleGirisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            alertDialog.setTitle("Google ile giriş yapılıyor");
            alertDialog.setMessage("Lütfen bekleyin");
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
                Log.i(TAG,"" + account.getId());
                kullaniciAdi=account.getDisplayName();
            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG,"Buraya giriyo "+e);
                alertDialog.dismiss();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "GİRDİ:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mevcutKullaniciID = mAuth.getCurrentUser().getUid();
                            kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(mevcutKullaniciID);
                            HashMap kullaniciMap = new HashMap();
                            kullaniciMap.put("isim",kullaniciAdi );
                            kullaniciMap.put("sinav", sinavString);
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
                            ProfilDuzenleActivityGit();
                            //KullaniciMainActivityGonder();
                            alertDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            KullaniciGirisYapActivityGonder();
                            Log.i(TAG, "HATA:failure", task.getException());

                        }

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
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.show();


            mAuth.signInWithEmailAndPassword(email, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String mevcutKullaniciID = mAuth.getCurrentUser().getUid();
                        DatabaseReference kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(mevcutKullaniciID);
                        HashMap kullaniciMap = new HashMap();

                        kullaniciMap.put("sinav", sinavString);
                        kullaniciReferans.updateChildren(kullaniciMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Log.i("Sınav", "Sınav değiştirildi");
                                } else {
                                    String mesaj = task.getException().getMessage();
                                    Log.i("Sınav", "Sistem mesajı: " + mesaj);
                                }
                            }
                        });

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

    private void KullaniciGirisYapActivityGonder(){
        Intent girisYapIntent=new Intent(GirisActivity.this,GirisActivity.class);
        girisYapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(girisYapIntent);
        finish();
    }

    private void ProfilDuzenleActivityGit() {
        Intent profilDuzenleActivity = new Intent(GirisActivity.this, ProfilDuzenleActivity.class);
        profilDuzenleActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profilDuzenleActivity);
        finish();
    }
}