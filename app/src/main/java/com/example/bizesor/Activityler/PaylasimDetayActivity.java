package com.example.bizesor.Activityler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bizesor.Adapterlar.YorumAdapter;
import com.example.bizesor.Modeller.ModelYorum;
import com.example.bizesor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PaylasimDetayActivity extends AppCompatActivity {

    String myUid, myName, myDp, paylasimId, pLikes, hisDp, hisName;
    int i;

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans, paylasimReference,likeRef,cevaplarReference;
    private Query query;
    private String kullaniciId,gelenKullaniciId,kullaniciResmiString;

    private TextView kullaniciAd, tarih, ders, gonderiYazisi, puan, yorum;
    private CircleImageView kullaniciResmi;
    private Button begen, yorumYap;
    private ImageView resim;
    private LinearLayout kullaniciProfil;
    private RecyclerView recyclerViewYorum;

    private CircleImageView yorumResim;
    private EditText yorumEdit;
    private ImageButton yorumGonder,moreButton;

    private AlertDialog alertDialog;
    boolean mProcessYorum = false;
    boolean mProcessLike = false;

    private List<ModelYorum> yorumList;
    private YorumAdapter yorumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paylasim_detay);

        mAuth = FirebaseAuth.getInstance();
        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        paylasimReference = FirebaseDatabase.getInstance().getReference().child("Paylasimlar");
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        cevaplarReference=FirebaseDatabase.getInstance().getReference().child("Cevaplar");
        kullaniciId = mAuth.getCurrentUser().getUid();

        paylasimId = getIntent().getExtras().get("paylasimId").toString();

        query = paylasimReference.orderByChild("paylasimId").equalTo(paylasimId);

        alertDialog = new AlertDialog.Builder(this).create();

        kullaniciAd = findViewById(R.id.paylasim_kullanici_adi);
        tarih = findViewById(R.id.paylasim_saat);
        ders = findViewById(R.id.paylasim_ders);
        gonderiYazisi = findViewById(R.id.paylasim_yazi);
        puan = findViewById(R.id.paylasim_puan_text);
        yorum = findViewById(R.id.paylasim_yorum_text);
        kullaniciResmi = findViewById(R.id.paylasim_profil_resim);
        begen = findViewById(R.id.paylasim_like_button);
        //yorumYap = findViewById(R.id.paylasim_yorum_yap_button);
        resim = findViewById(R.id.paylasim_resim_imgView);
        kullaniciProfil = findViewById(R.id.kullanici_profil_link);
        recyclerViewYorum=findViewById(R.id.yorumlar_recycler_view_yorum);
        moreButton=findViewById(R.id.paylasim_more_buttonn);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());

        recyclerViewYorum.setLayoutManager(layoutManager);

        yorumList=new ArrayList<>();

        yorumAdapter=new YorumAdapter(getApplicationContext(),yorumList,kullaniciId,paylasimId);

        recyclerViewYorum.setAdapter(yorumAdapter);

        yorumResim = findViewById(R.id.yorum_avatar);
        yorumEdit = findViewById(R.id.yorum_edit);
        yorumGonder = findViewById(R.id.yorum_gonder_button);

        kullaniciProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kullaniciProfilIntent = new Intent(PaylasimDetayActivity.this, KullaniciProfilActivity.class);
                kullaniciProfilIntent.putExtra("kullaniciId", gelenKullaniciId);
                PaylasimDetayActivity.this.startActivity(kullaniciProfilIntent);
            }
        });

        loadPaylasimInfo();
        loadKullaniciInfo();
        loadYorumlar();
        yorumGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paylasimYorum();
            }
        });
        begen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePaylasim();
            }
        });
       likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(paylasimId).hasChild(kullaniciId)) {
                    begen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_liked, 0, 0, 0);
                    begen.setText("Beğendin");
                } else {
                    begen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_alt_24, 0, 0, 0);
                    begen.setText("Beğen");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void kullaniciyaPuanEkle() {
        kullaniciReferans.child(kullaniciId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String puan = "" + snapshot.child("pPuan").getValue();
                int yeniPuanSayisi = Integer.parseInt(puan) + i;
                kullaniciReferans.child(kullaniciId).child("pPuan").setValue("" + yeniPuanSayisi);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadYorumlar() {
        paylasimReference.child(paylasimId).child("yorumlar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yorumList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelYorum modelYorum=ds.getValue(ModelYorum.class);
                    modelYorum.setKullaniciResimi(ds.child("kullaniciResmi").getValue().toString());
                    yorumList.add(modelYorum);
                }
                Collections.sort(yorumList, new Comparator<ModelYorum>() {
                    @Override
                    public int compare(ModelYorum o1, ModelYorum o2) {
                        return o1.getSaat().compareTo(o2.getSaat());
                    }
                });
                yorumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void likePaylasim() {
        mProcessLike = true;
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessLike) {
                    if (snapshot.child(paylasimId).hasChild(kullaniciId)) {
                        paylasimReference.child(paylasimId).child("pLikes").setValue("" + (Integer.parseInt(pLikes) - 1));
                        likeRef.child(paylasimId).child(kullaniciId).removeValue();
                        mProcessLike = false;

                        begen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_alt_24,0,0,0);
                        begen.setText("Beğen");
                    } else {
                        paylasimReference.child(paylasimId).child("pLikes").setValue("" + (Integer.parseInt(pLikes)  + 1));
                        likeRef.child(paylasimId).child(kullaniciId).setValue("Beğendin");
                        mProcessLike = false;

                        begen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_liked,0,0,0);
                        begen.setText("Beğendin");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void paylasimYorum() {
        String yorum = yorumEdit.getText().toString().trim();
        if (TextUtils.isEmpty(yorum)) {
            Toast.makeText(PaylasimDetayActivity.this, "Yorum giriniz", Toast.LENGTH_LONG).show();
            return;
        } else {
            alertDialog.setTitle("Paylaşılıyor");
            alertDialog.setMessage("Lütfen biraz bekleyin");
            alertDialog.show();

            final String timeNow = String.valueOf(System.currentTimeMillis());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("yorumId", timeNow);
            hashMap.put("yorum", yorum);
            hashMap.put("saat", timeNow);
            hashMap.put("kullaniciId", kullaniciId);
            hashMap.put("kullaniciAd", myName);
            hashMap.put("kullaniciResmi", kullaniciResmiString);
            cevapGoster(kullaniciId, timeNow, paylasimId);
            paylasimReference.child(paylasimId).child("yorumlar").child(timeNow).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    alertDialog.dismiss();
                    Toast.makeText(PaylasimDetayActivity.this, "Yorum gönderildi..", Toast.LENGTH_SHORT).show();
                    yorumEdit.setText("");
                    yorumGuncelle();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alertDialog.dismiss();
                    Toast.makeText(PaylasimDetayActivity.this, "Yorum gönderilemedi..", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void cevapGoster(String kullaniciId, String yorumId, String paylasimId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("kullaniciId",kullaniciId);
        hashMap.put("yorumId",yorumId);
        hashMap.put("paylasimId",paylasimId);
        cevaplarReference.child(yorumId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Log.i("cevap","cevap kayıt edildi");

            }
        });
    }

    //burası
    private void yorumGuncelle() {
        mProcessYorum = true;
        paylasimReference.child(paylasimId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessYorum) {
                    String yorumlar = "" + snapshot.child("pYorum").getValue();
                    int yeniYorumSayisi = Integer.parseInt(yorumlar) + 1;
                    paylasimReference.child(paylasimId).child("pYorum").setValue("" + yeniYorumSayisi);
                    mProcessYorum = false;
                    kullaniciyaPuanEkle();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postIdYolla() {
        Intent postLink=new Intent(this,KullaniciProfilActivity.class);
        postLink.putExtra("paylasimId",paylasimId);
        this.startActivity(postLink);
    }

    private void loadKullaniciInfo() {
        kullaniciReferans.child(kullaniciId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        //kullaniciResmiString=snapshot.child("resim").getValue().toString();
                        myName = snapshot.child("isim").getValue().toString();
                        kullaniciResmiString = snapshot.child("resim").getValue().toString();
                        Picasso.with(PaylasimDetayActivity.this).load(kullaniciResmiString).networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.profile).into(yorumResim);
                    } catch (Exception e) {
                        Toast.makeText(PaylasimDetayActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PaylasimDetayActivity.this, "ife girmedi", Toast.LENGTH_LONG).show();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//burada yorum ve beğeni var
    private void loadPaylasimInfo() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String paylasimYazi = "b";
                    String paylasimResim = "a";
                    try {
                        paylasimYazi = ds.child("paylasimYazi").getValue().toString();
                    } catch (Exception e) {

                    }
                    try {
                        paylasimResim = ds.child("paylasimResim").getValue().toString();
                    } catch (Exception e) {

                    }
                    String derss = ds.child("ders").getValue().toString();
                    hisDp = ds.child("tarihSaat").getValue().toString();
                    String resimm = ds.child("resim").getValue().toString();
                    pLikes = ds.child("pLikes").getValue().toString();
                    hisName = ds.child("kullaniciAdSoyad").getValue().toString();
                    gelenKullaniciId = ds.child("kullaniciId").getValue().toString();
                    String puann = ds.child("puan").getValue().toString();
                    String yorumm = ds.child("pYorum").getValue().toString();
                    kullaniciAd.setText(hisName);
                    tarih.setText(hisDp);
                    ders.setText(derss);
                    yorum.setText(yorumm + " yorum");
                    i = Integer.parseInt(puann) + Integer.parseInt(pLikes);
                    puan.setText(String.valueOf(i) + " puan");
                    try {
                        gonderiYazisi.setText(paylasimYazi);
                        if (paylasimYazi == null || paylasimYazi.equals("null") || paylasimYazi == "b") {
                            gonderiYazisi.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {

                    }
                    try {
                        Picasso.with(PaylasimDetayActivity.this).load(resimm).networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.profile).into(kullaniciResmi);
                    } catch (Exception e) {

                    }
                    System.out.println("------------------------------------" + paylasimResim);
                    try {
                        Picasso.with(PaylasimDetayActivity.this).load(paylasimResim).into(resim);
                        if (paylasimResim == null || paylasimResim.equals("null") || paylasimResim == "a") {
                            resim.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {

                    }
                    MoreButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void MoreButton() {
        if(kullaniciId.equals(gelenKullaniciId)){
            moreButton.setVisibility(View.VISIBLE);
        }else{
            moreButton.setVisibility(View.INVISIBLE);
        }
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(PaylasimDetayActivity.this,moreButton, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE,0,0,"Paylaşımı Sil");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id=item.getItemId();
                        if(id==0){
                            Query pQuery=FirebaseDatabase.getInstance().getReference("Paylasimlar").orderByChild("paylasimId").equalTo(paylasimId);
                            pQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot ds: snapshot.getChildren()){
                                        ds.getRef().removeValue();
                                    }
                                    Toast.makeText(PaylasimDetayActivity.this,"Paylaşım slindi",Toast.LENGTH_LONG).show();
                                    MainActivityGonder();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }
    private void MainActivityGonder() {
        Intent mainActivity = new Intent(PaylasimDetayActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }
}