package com.example.bizesor.Activityler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bizesor.Adapterlar.PaylasimAdapter;
import com.example.bizesor.Modeller.ModelPaylasim;
import com.example.bizesor.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private Button yeniSoruGonder;
    private RecyclerView recyclerView;
    private List<ModelPaylasim> paylasimList;
    private PaylasimAdapter paylasimAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans,paylasimReference;

    private String kullaniciID;

    private CircleImageView circleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            KullaniciGirisActivityGonder();
            return;
        }



        try {
            kullaniciID = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        paylasimReference = FirebaseDatabase.getInstance().getReference().child("Paylasimlar");


        toolbar = findViewById(R.id.inc_mainSayfaToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bize Sor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        recyclerView=findViewById(R.id.main_activity_paylasimList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        paylasimList=new ArrayList<>();
        paylasimAdapter=new PaylasimAdapter(MainActivity.this,paylasimList);

        recyclerView.setAdapter(paylasimAdapter);


        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_baslik);
        circleImageView = navView.findViewById(R.id.navigation_profil_resmi);
        yeniSoruGonder = findViewById(R.id.btn_soru_gonder);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                KullaniciMenuSec(item);
                return false;
            }
        });
        try {
            kullaniciReferans.child(kullaniciID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        try {
                            String resim = snapshot.child("resim").getValue().toString();
                            System.out.println(resim);
                            Picasso.with(MainActivity.this).load(resim).placeholder(R.drawable.profile).into(circleImageView);
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

        yeniSoruGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KullaniciPostEkleActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        paylasimYukle();
    }

    private void paylasimYukle() {
        paylasimReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paylasimList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelPaylasim modelPaylasim=ds.getValue(ModelPaylasim.class);
                    paylasimList.add(modelPaylasim);
                }
                Collections.sort(paylasimList, new Comparator<ModelPaylasim>() {
                    @Override
                    public int compare(ModelPaylasim o1, ModelPaylasim o2) {
                        return o2.getTarihSaat().compareTo(o1.getTarihSaat());
                    }
                });
                paylasimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,""+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void KullaniciPostEkleActivity() {
        Intent postIntent = new Intent(MainActivity.this, PaylasimActivity.class);
        startActivity(postIntent);
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
            case R.id.nav_profilim:
                Intent profilimIntent=new Intent(MainActivity.this, ProfilActivity.class);
                startActivity(profilimIntent);
               // drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
    }

}