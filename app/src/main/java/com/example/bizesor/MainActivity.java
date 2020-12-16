package com.example.bizesor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    //private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans;

    private String kullaniciID;

    private CircleImageView navigationProfilResim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        kullaniciID=mAuth.getCurrentUser().getUid();
        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");

        toolbar = findViewById(R.id.inc_mainSayfaToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bize Sor");

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_baslik);
        navigationProfilResim=navView.findViewById(R.id.navigation_profil_resmi);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                KullaniciMenuSec(item);
                return false;
            }
        });

       kullaniciReferans.child(kullaniciID).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()) {
                   String resim = snapshot.child("resim").getValue().toString();
                   System.out.println(resim);
                   Picasso.with(MainActivity.this).load(resim).into(navigationProfilResim);
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
        });

    }

    @Override
    protected void onStart() { //Bu metot direk program run edildiğinde çalışır.
        super.onStart();
        FirebaseUser baglantiKullanici = mAuth.getCurrentUser();
        if (baglantiKullanici == null) {
            KullaniciGirisActivityGonder();
        }
        else{
            KullanicininVarliginiKontrolEt();
        }
    }

    private void KullanicininVarliginiKontrolEt() {//Burada dbden silinen kullanıcının idsinin db e olup olmadığını kontrol edecek...
        final String mevcutKullaniciKimligi = mAuth.getCurrentUser().getUid();
        Log.i("id", mevcutKullaniciKimligi);

    }

    private void KullaniciGirisActivityGonder() { //Bu metot girilmiş bir hesap olmadığında bizi girisActivitye yönlendirir.
        Intent girisIntent = new Intent(MainActivity.this, GirisActivity.class); //Intent activityler arası geçişi sağlar.
        girisIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(girisIntent);
        finish();//Geri gitmeyi önler
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //Navigation kısmındaki burger buttonun çalışmasını sağlar
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void KullaniciMenuSec(MenuItem item) { //Burası navigation içindeki menüleri seçme metodu
        switch (item.getItemId()) {
            case R.id.nav_cikis:
                mAuth.signOut();
                KullaniciGirisActivityGonder();
                break;
            case R.id.nav_ayarlar:
                Intent ayarlarIntent=new Intent(MainActivity.this,ProfilDuzenleActivity.class);
                startActivity(ayarlarIntent);
        }
    }

}