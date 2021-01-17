package com.example.bizesor.Activityler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.bizesor.Adapterlar.SinavAdapter;
import com.example.bizesor.Modeller.ModelPaylasim;
import com.example.bizesor.Modeller.ModelSinav;
import com.example.bizesor.R;
import com.example.bizesor.SpinnerListeler.DersListeSpinner;
import com.example.bizesor.SpinnerListeler.KonuListeSpinner;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class PaylasimActivity extends AppCompatActivity {

    SinavAdapter adapter;
    DersListeSpinner dersListeSpinner;
    KonuListeSpinner konuListeSpinner;

    private AlertDialog alertDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReference, paylasimReference;
    private String kullaniciID;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    private Uri resimUri;
    //private String uri = "";
    private String dersString;
    private String konuString;
    private String puan;
    private String tarih, saat, paylasimAdi, dowloadUrl;

    private Toolbar toolbar;
    private TextView textFotografSec;
    private ImageView secilenResim;
    private Spinner spinnerDers, spinnerKonu;
    private EditText editSoruGir;
    private ElegantNumberButton puanButtonu;
    private Button paylasimKaydet;


    ModelPaylasim modelPaylasim=new ModelPaylasim();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylasim);

        mAuth = FirebaseAuth.getInstance();
        kullaniciID = mAuth.getCurrentUser().getUid();
        kullaniciReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        //.child(kullaniciID) sildim
        paylasimReference = FirebaseDatabase.getInstance().getReference().child("Paylasimlar");
        storageReference = FirebaseStorage.getInstance().getReference();

        alertDialog = new AlertDialog.Builder(this).create();

        toolbar = findViewById(R.id.toolbar_soru_gonder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Soru Gönder");

        textFotografSec = findViewById(R.id.post_activity_text_fotograf_sec);
        secilenResim = findViewById(R.id.post_activity_image);
        spinnerDers = findViewById(R.id.post_activity_spinner_ders);
        spinnerKonu = findViewById(R.id.post_activity_spinner_konu);
        editSoruGir = findViewById(R.id.post_activity_edit_soru);
        puanButtonu = findViewById(R.id.post_activity_puan);
        paylasimKaydet = findViewById(R.id.post_activity_btn_gonder);

        textFotografSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(2, 1).start(PaylasimActivity.this);
            }
        });

        SpinnerListeleme();
        paylasimKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                puan = puanButtonu.getNumber();
                ResmiVeritabaninaKaydet();

            }
        });
    }

    private void ResmiVeritabaninaKaydet() {
        if (editSoruGir.getText().toString().isEmpty() && resimUri == null) {
            Toast.makeText(this, "Lütfen soru girin ya da resim ekleyin..", Toast.LENGTH_LONG).show();
        } else {
            alertDialog.setTitle("Paylaşılıyor");
            alertDialog.setMessage("Lütfen biraz bekleyin");
            alertDialog.show();
            Calendar calendarTarih = Calendar.getInstance();
            SimpleDateFormat tarihFormat = new SimpleDateFormat("dd-MM-YYYY");
            tarih = tarihFormat.format(calendarTarih.getTime());

            Calendar calendarSaat = Calendar.getInstance();
            SimpleDateFormat saatFormat = new SimpleDateFormat("HH:mm");
            saat = saatFormat.format(calendarSaat.getTime());

            paylasimAdi = tarih + saat;
            try {
                final StorageReference filePath = storageReference.child("paylasim").child(resimUri.getLastPathSegment() + paylasimAdi + ".jpg");

                uploadTask = filePath.putFile(resimUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri dowloadUri = task.getResult();
                            dowloadUrl = dowloadUri.toString();

                            PostVeritabaninaKaydet();
                        } else {
                            Log.i("Hata", "Else düştü,resim yüklenemedi");
                        }
                    }
                });
            } catch (Exception e) {
                Log.i("Hata", e.getMessage());
                try {
                    PostVeritabaninaKaydet();
                } catch (Exception ex) {
                    Log.i("Hata", ex.getMessage());
                }
            }
        }
    }



    private void PostVeritabaninaKaydet() {
        kullaniciReference.child(kullaniciID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kullaniciReference.child(kullaniciID).removeEventListener(this);
                if (snapshot.exists()) {
                    String kullaniciAdi = snapshot.child("isim").getValue().toString();
                    String kullaniciProfilResmi = snapshot.child("resim").getValue().toString();
                    String soru = editSoruGir.getText().toString();

                    HashMap paylasimMap = new HashMap();
                    paylasimMap.put("kullaniciId", kullaniciID);
                    paylasimMap.put("paylasimId",kullaniciID+paylasimAdi);
                    paylasimMap.put("pLikes","0");
                    paylasimMap.put("kullaniciAdSoyad", kullaniciAdi);
                    paylasimMap.put("tarihSaat", tarih + " " + saat);
                    paylasimMap.put("pYorum","0");
                    if (!soru.isEmpty()) {
                        paylasimMap.put("paylasimYazi", soru);
                    }
                    if (dowloadUrl != null) {
                        paylasimMap.put("paylasimResim", dowloadUrl);
                    }
                    paylasimMap.put("resim", kullaniciProfilResmi);
                    paylasimMap.put("ders", dersString + " " + konuString);
                    paylasimMap.put("puan", puan);//listeyi hangi ekrande cekıyorsun
                    paylasimReference.child(kullaniciID + paylasimAdi).updateChildren(paylasimMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                MainActivityGonder();
                                Toast.makeText(PaylasimActivity.this, "Paylaşım kayıt edildi", Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                                kullaniciSoruSayisi();
                            } else {
                                Toast.makeText(PaylasimActivity.this, "Paylaşım kayıt edilemedi", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void kullaniciSoruSayisi() {
        kullaniciReference.child(kullaniciID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String soruSayisi = "" + snapshot.child("pSoruSayisi").getValue();
                int yeniSoruSayisi = Integer.parseInt(soruSayisi) + 1;
                kullaniciReference.child(kullaniciID).child("pSoruSayisi").setValue("" + yeniSoruSayisi);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SpinnerListeleme() {
        //Burada sinav kısmındaki spinnera göre ders spinnerını oluşturuyoruz
        kullaniciReference.child(kullaniciID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("sinav")) {
                    String sinav = snapshot.child("sinav").getValue().toString();
                    dersListeSpinner = new DersListeSpinner();
                    dersListeSpinner.initlist(sinav);
                    adapter = new SinavAdapter(PaylasimActivity.this, dersListeSpinner.dersItemArrayList);
                    spinnerDers.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Burada ders kısmındaki spinnera göre konu spinnerını dolduruyoruz
        spinnerDers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ModelSinav dersItem = (ModelSinav) parent.getItemAtPosition(position);
                dersString = dersItem.getSinavIsmi();
                konuListeSpinner = new KonuListeSpinner();
                try {
                    konuListeSpinner.initlist(dersString);
                    adapter = new SinavAdapter(PaylasimActivity.this, konuListeSpinner.konuItemArrayList);
                    spinnerKonu.setAdapter(adapter);
                } catch (Exception e) {
                    Log.i("Hata", e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Burada konu spinner stringini alıyoruz
        spinnerKonu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ModelSinav konuItem = (ModelSinav) parent.getItemAtPosition(position);
                konuString = konuItem.getSinavIsmi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            File file = new File(result.getUri().getPath());

            File compressedImageFile = Compressor.getDefault(this).compressToFile(file);

            resimUri=Uri.fromFile(compressedImageFile);
           // resimUri = result.getUri();
            secilenResim.setImageURI(resimUri);
        } else {
            Toast.makeText(this, "Hata:Tekrar deneyin", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i("log", "Anasayfaya gitmek için kullanılıyor");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            MainActivityGonder();
        }
        return super.onOptionsItemSelected(item);
    }

    private void MainActivityGonder() {
        Intent mainActivity = new Intent(PaylasimActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }

}