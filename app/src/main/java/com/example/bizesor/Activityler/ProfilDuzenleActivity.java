package com.example.bizesor.Activityler;

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

import com.example.bizesor.R;
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

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfilDuzenleActivity extends AppCompatActivity {

    private EditText editProfilDuzenleHakkimda;
    private Button btnProfilDuzenleKaydet;
    private CircleImageView profilResim;


    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans;
    private StorageReference kullaniciProfilResimReferans;
    private StorageTask uploadTask;

    private Uri resimUri;
    private String uri = "";

    private String kullaniciID;
    private String resim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenle);

        mAuth = FirebaseAuth.getInstance();
        kullaniciID = mAuth.getCurrentUser().getUid();
        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(kullaniciID);
        kullaniciProfilResimReferans = FirebaseStorage.getInstance().getReference().child("profil resmi");


        editProfilDuzenleHakkimda = findViewById(R.id.activity_profil_duzenle_edit_hakkimda);
        //editProfilDuzenleIsim = findViewById(R.id.activity_profil_duzenle_edit_isim);
        btnProfilDuzenleKaydet = findViewById(R.id.activity_profil_duzenle_btn_kaydet);
        profilResim = findViewById(R.id.activity_profil_duzenle_circle_image);


        profilResim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1, 1).start(ProfilDuzenleActivity.this);
            }
        });
        //googleIleGiris();
        getKullaniciInfo();

        btnProfilDuzenleKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!resim.equals("null") || resimUri != null) {
                    yukleProfilResim();
                } else {
                    Toast.makeText(ProfilDuzenleActivity.this, "Lütfen resim yükleyiniz..", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void MainActivityGit() {
        Intent mainActivityGit = new Intent(ProfilDuzenleActivity.this, MainActivity.class);
        mainActivityGit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityGit);
        finish();
    }

    /*private void googleIleGiris() {
        GoogleSignInAccount googleSignInAccount=GoogleSignIn.getLastSignedInAccount(this);
        if(googleSignInAccount!=null){
            editProfilDuzenleIsim.setText(googleSignInAccount.getDisplayName());
        }
    }*/

    private void getKullaniciInfo() {
        kullaniciReferans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    if (snapshot.hasChild("resim")) {
                        resim = snapshot.child("resim").getValue().toString();
                        Picasso.with(ProfilDuzenleActivity.this).load(resim).placeholder(R.drawable.profile).into(profilResim);
                    }
                    if (snapshot.hasChild("hakkimda")) {
                        String isim = snapshot.child("isim").getValue().toString();
                        String hakkimda = snapshot.child("hakkimda").getValue().toString();
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
            File file = new File(result.getUri().getPath());

            File compressedImageFile = Compressor.getDefault(this).compressToFile(file);

            resimUri = Uri.fromFile(compressedImageFile);

            profilResim.setImageURI(resimUri);

        } else {
            Toast.makeText(this, "Hata:Tekrar deneyin", Toast.LENGTH_LONG).show();
        }
    }

    private void yukleProfilResim() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Yükleniyor");
        alertDialog.setMessage("Lütfen biraz bekleyin..");
        alertDialog.show();

        String kullaniciHakkimda = editProfilDuzenleHakkimda.getText().toString();

        if (TextUtils.isEmpty(kullaniciHakkimda)) {
            Toast.makeText(this, "Lütfen boş alan bırakmayınız", Toast.LENGTH_LONG).show();
        } else {
            HashMap kullaniciMap = new HashMap();
            kullaniciMap.put("hakkimda", kullaniciHakkimda);
            kullaniciReferans.updateChildren(kullaniciMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        //alertDialog.dismiss();
                    } else {
                        Toast.makeText(ProfilDuzenleActivity.this, "Kayıt edilemedi", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if (resimUri != null) {
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
                    if (task.isSuccessful()) {

                        Uri dowloadUri = task.getResult();
                        uri = dowloadUri.toString();

                        kullaniciReferans.child("resim").setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                alertDialog.dismiss();
                                MainActivityGit();
                            }
                        });
                    } else {
                        Log.i("Hata", "Else düştü,resim yüklenemedi");
                    }
                }
            });
        } else {
            alertDialog.dismiss();
            Log.i("Hata", "Else düştü, resim seçilemedi");
        }
    }
}
