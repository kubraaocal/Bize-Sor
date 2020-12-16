package com.example.bizesor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilDuzenleActivity extends AppCompatActivity {
    private EditText editProfilDuzenleIsim, editProfilDuzenleSoyisim, editProfilDuzenleHakkimda;
    private Button btnProfilDuzenleKaydet;
    private CircleImageView profilResim;

    final static int galeri_sec = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans;
    private StorageReference kullaniciProfilResimReferans;
    private StorageTask uploadTask;

    private Uri resimUri;
    private String uri = "";

    private String kullaniciID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenle);

        mAuth = FirebaseAuth.getInstance();
        kullaniciID = mAuth.getCurrentUser().getUid();
        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(kullaniciID);
        kullaniciProfilResimReferans = FirebaseStorage.getInstance().getReference().child("profil resmi");


        editProfilDuzenleHakkimda = findViewById(R.id.activity_profil_duzenle_edit_hakkimda);
        editProfilDuzenleIsim = findViewById(R.id.activity_profil_duzenle_edit_isim);
        editProfilDuzenleSoyisim = findViewById(R.id.activity_profil_duzenle_edit_soyisim);
        btnProfilDuzenleKaydet = findViewById(R.id.activity_profil_duzenle_btn_kaydet);
        profilResim = findViewById(R.id.activity_profil_duzenle_circle_image);

        btnProfilDuzenleKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yukleProfilResim();
                ProfilDuzenleVerileriKaydet();
            }
        });

        profilResim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1, 1).start(ProfilDuzenleActivity.this);

                /*Intent galeriIntent = new Intent();
                galeriIntent.setAction(Intent.ACTION_GET_CONTENT);
                galeriIntent.setType("image/*");
                startActivityForResult(galeriIntent, galeri_sec);*/
            }
        });

        getKullaniciInfo();
    }

    private void ProfilDuzenleVerileriKaydet() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Yükleniyor");
        alertDialog.setMessage("Lütfen biraz bekleyin..");
        alertDialog.show();

        String kullaniciIsim=editProfilDuzenleIsim.getText().toString();
        String kullaniciSoyisim=editProfilDuzenleSoyisim.getText().toString();
        String kullaniciHakkimda=editProfilDuzenleHakkimda.getText().toString();

        if(TextUtils.isEmpty(kullaniciIsim)||TextUtils.isEmpty(kullaniciSoyisim)||TextUtils.isEmpty(kullaniciHakkimda)){
            Toast.makeText(this,"Lütfen boş alan bırakmayınız",Toast.LENGTH_LONG).show();
        }
        else {
            HashMap kullaniciMap=new HashMap();
            kullaniciMap.put("isim",kullaniciIsim);
            kullaniciMap.put("soyisim",kullaniciSoyisim);
            kullaniciMap.put("hakkimda",kullaniciHakkimda);
            kullaniciReferans.updateChildren(kullaniciMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        alertDialog.dismiss();
                    }
                    else{
                        Toast.makeText(ProfilDuzenleActivity.this,"Kayıt edilemedi",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void getKullaniciInfo() {
        kullaniciReferans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    if (snapshot.hasChild("resim")) {
                        String resim = snapshot.child("resim").getValue().toString();
                        Picasso.with(ProfilDuzenleActivity.this).load(resim).into(profilResim);
                        Log.i("aaa","Burası ilk açılınca");
                    }
                    if(snapshot.hasChild("isim")&&snapshot.hasChild("soyisim")&&snapshot.hasChild("hakkimda")){
                        String isim=snapshot.child("isim").getValue().toString();
                        String soyisim=snapshot.child("soyisim").getValue().toString();
                        String hakkimda=snapshot.child("hakkimda").getValue().toString();
                        editProfilDuzenleIsim.setText(isim);
                        editProfilDuzenleSoyisim.setText(soyisim);
                        editProfilDuzenleHakkimda.setText(hakkimda);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resimUri = result.getUri();

            profilResim.setImageURI(resimUri);

        } else {
            Toast.makeText(this, "Hata:Tekrar deneyin", Toast.LENGTH_LONG).show();
        }
    }

    private void yukleProfilResim() {
       // Log.i("a1", "Metod çalıştı");
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Yükleniyor");
        alertDialog.setMessage("Lütfen biraz bekleyin..");
        alertDialog.show();

        if (resimUri != null) {
            //Log.i("a11", "ife girdi");
            final StorageReference dosyaRef = kullaniciProfilResimReferans.child(kullaniciID + ". jpg");

            uploadTask = dosyaRef.putFile(resimUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }
                    return dosyaRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                   // Log.i("a1111", "burası çalıştı");
                    if (task.isSuccessful()) {
                        //Log.i("a1111", "İfe girdi");
                            Uri dowloadUri = task.getResult();
                            uri = dowloadUri.toString();

                            HashMap<String, Object> kullaniciMap = new HashMap<>();
                            kullaniciMap.put("resim", uri);
                            kullaniciReferans.updateChildren(kullaniciMap);
                           // Log.i("a111", "Yüklendi");
                            alertDialog.dismiss();
                    }
                    else{
                        Log.i("Hata", "Else düştü,resim yüklenemedi");
                    }
                }
            });
        } else {
            alertDialog.dismiss();
            Log.i("Hata", "Else düştü, resim seçilemedi");
            //Toast.makeText(this, "Resim seçilemedi", Toast.LENGTH_LONG).show();
        }
    }
}
