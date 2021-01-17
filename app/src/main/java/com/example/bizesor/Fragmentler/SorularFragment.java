package com.example.bizesor.Fragmentler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bizesor.Adapterlar.PaylasimAdapter;
import com.example.bizesor.Modeller.ModelPaylasim;
import com.example.bizesor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SorularFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference kullaniciReferans, paylasimReference;
    private Query query;

    private String kullaniciID, gelenKullaniciId;

    private RecyclerView recyclerView;
    private List<ModelPaylasim> paylasimList;
    private PaylasimAdapter paylasimAdapter;
    private RecyclerView.LayoutManager layoutManager;


    View v;

    public SorularFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sorular, container, false);
        recyclerView = v.findViewById(R.id.fragment_recyclerview_sorularim);
        paylasimAdapter = new PaylasimAdapter(getContext(), paylasimList);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(paylasimAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        try {
            gelenKullaniciId = getArguments().get("kullaniciId").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            kullaniciID = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kullaniciReferans = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        paylasimReference = FirebaseDatabase.getInstance().getReference().child("Paylasimlar");
        query = paylasimReference.orderByChild("kullaniciId").equalTo(gelenKullaniciId);
        paylasimList = new ArrayList<>();
        paylasimYukle();

        //layoutManager.setStackFromEnd(true);
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //layoutManager.setReverseLayout(true)


    }

    private void paylasimYukle() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paylasimList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPaylasim modelPaylasim = ds.getValue(ModelPaylasim.class);
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
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
