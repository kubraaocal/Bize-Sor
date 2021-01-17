package com.example.bizesor.Activityler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.bizesor.Adapterlar.ViewPagerAdapter;
import com.example.bizesor.Fragmentler.CevaplarFragment;
import com.example.bizesor.Fragmentler.SorularFragment;
import com.example.bizesor.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends AppCompatActivity {
    private ImageView profilAyarla;
    private Toolbar toolbar;
    private CircleImageView circleProfilResmi;
    private TextView kullaniciAdiSoyadi,kullaniciHakkimda,kullaniciPuan,kullaniciSoruSayisi;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans,paylasimReference;

    private String kullaniciID;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mAuth = FirebaseAuth.getInstance();
        try {
            kullaniciID = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        paylasimReference = FirebaseDatabase.getInstance().getReference().child("Paylasimlar");

        tabLayout=findViewById(R.id.profil_activity_tablayout);
        viewPager=findViewById(R.id.viewpager);

        profilAyarla=findViewById(R.id.profil_activity_profil_ayar);
        profilAyarla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ayarlarIntent = new Intent(ProfilActivity.this, ProfilDuzenleActivity.class);
                startActivity(ayarlarIntent);
            }
        });
        circleProfilResmi=findViewById(R.id.profil_activity_profil_resmi);
        kullaniciAdiSoyadi=findViewById(R.id.profil_activity_kullanici_adi);
        kullaniciHakkimda=findViewById(R.id.profil_activity_kullanici_hakkimda);
        kullaniciPuan=findViewById(R.id.profil_text_puan);
        kullaniciSoruSayisi=findViewById(R.id.profil_text_soru_sayisi);
        toolbar=findViewById(R.id.profil_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profil Detayı");

    }

    @Override
    protected void onResume() {
        super.onResume();
        tabLayoutOlustur();
        ProfilResmiGoster();
    }

    private void tabLayoutOlustur(){
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),viewPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        Bundle bundle=new Bundle();
        bundle.putString("kullaniciId",kullaniciID);
        SorularFragment sorularFragment=new SorularFragment();
        sorularFragment.setArguments(bundle);
        CevaplarFragment cevaplarFragment=new CevaplarFragment();
        cevaplarFragment.setArguments(bundle);

        viewPagerAdapter.addFragment(sorularFragment,"Sorularım");
        viewPagerAdapter.addFragment(cevaplarFragment,"Cevaplarım");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void ProfilResmiGoster(){
        try {
            kullaniciReferans.child(kullaniciID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        try {
                            String resim = snapshot.child("resim").getValue().toString();
                            String kullaniciAdi=snapshot.child("isim").getValue().toString();
                            String kullaniciHakkim=snapshot.child("hakkimda").getValue().toString();
                            String puan=snapshot.child("pPuan").getValue().toString();
                            String soruSayisi=snapshot.child("pSoruSayisi").getValue().toString();
                            System.out.println(resim);
                            Picasso.with(ProfilActivity.this).load(resim).placeholder(R.drawable.profile).into(circleProfilResmi);
                            kullaniciAdiSoyadi.setText(kullaniciAdi);
                            kullaniciHakkimda.setText(kullaniciHakkim);
                            kullaniciPuan.setText(puan+"\n Puan");
                            kullaniciSoruSayisi.setText(soruSayisi+"\n Soru");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i("log", "Anasayfaya gitmek için kullanılıyor");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            MainActivityGonder();
        }
        return super.onOptionsItemSelected(item);
    }
    private void MainActivityGonder() {
        Intent mainActivity = new Intent(ProfilActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }

}