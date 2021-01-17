package com.example.bizesor.Adapterlar;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bizesor.Activityler.KullaniciProfilActivity;
import com.example.bizesor.Modeller.ModelPaylasim;
import com.example.bizesor.Activityler.PaylasimDetayActivity;
import com.example.bizesor.R;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PaylasimAdapter extends RecyclerView.Adapter<PaylasimAdapter.MyHolder> {

    Context context;
    List<ModelPaylasim> paylasimList;
    private String myUid;
    private DatabaseReference likeRef, paylasimRef;

    boolean mProcessLike = false;

    public PaylasimAdapter(Context context, List<ModelPaylasim> paylasimList) {
        this.context = context;
        this.paylasimList = paylasimList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        paylasimRef = FirebaseDatabase.getInstance().getReference().child("Paylasimlar");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(context).inflate(R.layout.tum_paylasimlar_layout, parent, false);
        return new MyHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.bind(paylasimList.get(position));

    }

    @Override
    public int getItemCount() {
        return paylasimList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        String kullaniciId, paylasimId, pLikes;
        CircleImageView kullaniciProfilResmi;
        ImageView paylasilanResim;
        TextView kullaniciFullAd, ders, saat, yazi, puan, yorum;
        Button likeButton, yorumYapButton;
        ImageButton moreButton;
        LinearLayout kullaniciProfil, paylasimDetay;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            kullaniciFullAd = itemView.findViewById(R.id.paylasim_kullanici_adi);
            kullaniciProfilResmi = itemView.findViewById(R.id.paylasim_profil_resim);
            paylasilanResim = itemView.findViewById(R.id.paylasim_resim);
            ders = itemView.findViewById(R.id.paylasim_ders);
            saat = itemView.findViewById(R.id.paylasim_saat);
            yazi = itemView.findViewById(R.id.paylasim_yazi);
            puan = itemView.findViewById(R.id.paylasim_puan_text);
            yorum = itemView.findViewById(R.id.paylasim_yorum_text);
            moreButton = itemView.findViewById(R.id.paylasim_more_button);
            likeButton = itemView.findViewById(R.id.paylasim_like_button);
            //dislikeButton=itemView.findViewById(R.id.paylasim_dislike_button);
            yorumYapButton = itemView.findViewById(R.id.paylasim_yorum_yap);
            kullaniciProfil = itemView.findViewById(R.id.kullanici_profil_link);
            paylasimDetay = itemView.findViewById(R.id.paylasim_link);
        }

        void bind(final ModelPaylasim model) {
            kullaniciId = model.getKullaniciId();
            paylasimId = model.getPaylasimId();
            pLikes = model.getpLikes();
            kullaniciFullAd.setText(model.getKullaniciAdSoyad());
            try {
                Picasso.with(context).load(model.getResim()).networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.profile).into(kullaniciProfilResmi);
            } catch (Exception e) {

            }

            try {
                Picasso.with(context).load(model.getPaylasimResim()).into(paylasilanResim);//dene bi böyle
                if (model.getPaylasimResim() == null || model.getPaylasimResim().equals("null")) {
                    paylasilanResim.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }
            ders.setText(model.getDers());
            saat.setText(model.getTarihSaat());
            int i = Integer.parseInt(pLikes) + Integer.parseInt(model.getPuan());
            puan.setText(i + " puan");
            yorum.setText(model.getpYorum() + " yorum");
            try {
                yazi.setText(model.getPaylasimYazi());
                if (model.getPaylasimYazi() == null || model.getPaylasimYazi().equals("null")) {
                    yazi.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }
            metod();

            likeButton.setOnClickListener(new View.OnClickListener() {
                int pLikes;

                @Override
                public void onClick(View v) {
                    try {
                        pLikes = Integer.parseInt(model.getpLikes());
                    } catch (Exception e) {

                    }

                    mProcessLike = true;

                    final String postIde = model.getPaylasimId();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (mProcessLike) {
                                if (snapshot.child(postIde).hasChild(myUid)) {
                                    paylasimRef.child(postIde).child("pLikes").setValue("" + (pLikes - 1));
                                    likeRef.child(postIde).child(myUid).removeValue();
                                    mProcessLike = false;
                                } else {
                                    paylasimRef.child(postIde).child("pLikes").setValue("" + (pLikes + 1));
                                    likeRef.child(postIde).child(myUid).setValue("Beğendin");
                                    mProcessLike = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            /*dislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Dislike",Toast.LENGTH_LONG).show();
                }
            });*/
            yorumYapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent paylasimLink = new Intent(context, PaylasimDetayActivity.class);
                    paylasimLink.putExtra("paylasimId", paylasimId);
                    context.startActivity(paylasimLink);
                }
            });
            if(kullaniciId.equals(myUid)){
                moreButton.setVisibility(View.VISIBLE);
            }else{
                moreButton.setVisibility(View.INVISIBLE);
            }
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu=new PopupMenu(context,moreButton,Gravity.END);
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
                                        Toast.makeText(context,"Paylaşım slindi",Toast.LENGTH_LONG).show();
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
            kullaniciProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent kullaniciProfilIntent = new Intent(context, KullaniciProfilActivity.class);
                    kullaniciProfilIntent.putExtra("kullaniciId", kullaniciId);
                   //kullaniciProfilIntent.putExtra("paylasimId",paylasimId);
                    context.startActivity(kullaniciProfilIntent);
                }
            });
            paylasimDetay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent paylasimLink = new Intent(context, PaylasimDetayActivity.class);
                    paylasimLink.putExtra("paylasimId", paylasimId);
                    context.startActivity(paylasimLink);

                }
            });

        }

        private void metod() {
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(paylasimId).hasChild(myUid)) {
                        likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_liked, 0, 0, 0);
                        likeButton.setText("Beğendin");
                    } else {
                        likeButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_alt_24, 0, 0, 0);
                        likeButton.setText("Beğen");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}



