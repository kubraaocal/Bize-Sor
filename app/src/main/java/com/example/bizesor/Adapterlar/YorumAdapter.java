package com.example.bizesor.Adapterlar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bizesor.Modeller.ModelYorum;
import com.example.bizesor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class YorumAdapter extends RecyclerView.Adapter<YorumAdapter.MyHolder> {


    Context context;
    List<ModelYorum> yorumList;
    String myUid, paylasimId;


    public YorumAdapter(Context context, List<ModelYorum> yorumList, String myUid, String paylasimId) {
        this.context = context;
        this.yorumList = yorumList;
        this.myUid = myUid;
        this.paylasimId = paylasimId;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(context).inflate(R.layout.yorum_layout, parent, false);
        return new MyHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.bind(yorumList.get(position));
    }

    @Override
    public int getItemCount() {
        return yorumList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        Query pQuery;
        DatabaseReference paylasimReference;

        CircleImageView kullaniciResmi;
        TextView kullaniciAdi, kullaniciYorumu, tarih;
        String kullaniciId, yorumId, benimMi;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            kullaniciAdi = itemView.findViewById(R.id.yorum__txt_kullanici_isim);
            kullaniciYorumu = itemView.findViewById(R.id.yorum__txt_kullanici_yorum);
            tarih = itemView.findViewById(R.id.yorum__txt_tarih);
            kullaniciResmi = itemView.findViewById(R.id.yorum_circle_kullanici_resim);

        }

        public void bind(ModelYorum modelYorum) {
            kullaniciId = modelYorum.getKullaniciId();
            yorumId = modelYorum.getYorumId();
            kullaniciAdi.setText(modelYorum.getKullaniciAd());
            kullaniciYorumu.setText(modelYorum.getYorum());

            paylasimReference = FirebaseDatabase.getInstance().getReference().child("Paylasimlar").child(paylasimId);
            pQuery = paylasimReference.orderByChild("kullaniciId").equalTo(myUid);

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.setTimeInMillis(Long.parseLong(modelYorum.getSaat()));
            String pTarih = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

            tarih.setText(pTarih);
            try {
                Picasso.with(context).load(modelYorum.getKullaniciResimi()).networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.profile).into(kullaniciResmi);
            } catch (Exception e) {
                e.printStackTrace();
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (myUid.equals(kullaniciId)) {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
                        alertDialog.setTitle("Sil");
                        alertDialog.setMessage("Yorumu silmek istediğinize emin misiniz?");
                        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Paylasimlar").child(paylasimId);
                                databaseReference.child("yorumlar").child(yorumId).removeValue();
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String yorumlar = "" + snapshot.child("pYorum").getValue();
                                        int yeniYorumSayisi = Integer.parseInt(yorumlar) - 1;
                                        databaseReference.child("pYorum").setValue("" + yeniYorumSayisi);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                dialog.dismiss();
                            }

                        });
                        alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.create().show();
                    }else{
                        paylasimReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                paylasimReference.removeEventListener(this);
                                benimMi = "" + snapshot.child("kullaniciId").getValue();
                                if (benimMi.equals(myUid)) {
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getRootView().getContext());
                                    alertDialog.setTitle("Sil");
                                    alertDialog.setMessage("Yorumu silmek istediğinize emin misiniz?");
                                    alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Paylasimlar").child(paylasimId);
                                            databaseReference.child("yorumlar").child(yorumId).removeValue();
                                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String yorumlar = "" + snapshot.child("pYorum").getValue();
                                                    int yeniYorumSayisi = Integer.parseInt(yorumlar) - 1;
                                                    databaseReference.child("pYorum").setValue("" + yeniYorumSayisi);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });//bu kod 2 kere calısıyor 2 kere show yaparsan patlar nıe 2 kere calısıyor ona bakalım
                                    alertDialog.create().show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });
        }
    }
}
