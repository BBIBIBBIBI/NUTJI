package org.techtown.dontlate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.techtown.dontlate.editSchedule.context;

public class usersettingss extends Fragment {

    private interface FirebaseInput {
        void onInput(String result);
    }

    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private ArrayList<UserListItem> arrayList;
    private TextView address;
    private EditText name;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private Button save;
    private Button addPlace;

    String default_name;
    String Place;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.usersettings, container, false);

        addPlace = (Button) v.findViewById(R.id.addplace);
        recyclerView = (RecyclerView) v.findViewById(R.id.RecyclerView);
        address = (TextView) v.findViewById(R.id.Address);
        save = (Button) v.findViewById(R.id.Save);
        name = (EditText) v.findViewById(R.id.Name);

        arrayList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Nutji").child("User").child("Place");

        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new UserListAdapter(arrayList, getContext());
        recyclerView.setAdapter(adapter);

        database.getReference().child("Nutji").child("User").child("UserName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                default_name = value;
                name.setText(default_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserListItem user = snapshot.getValue(UserListItem.class);
                    arrayList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"집", "회사", "학교"};
                final HashMap result = new HashMap<>();

                // 라디오버튼 다이얼로그 생성
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("장소를 선택하세요")
                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // which = position
                                Place = items[which];

                                result.put("PlaceName", Place);
                                result.put("Address", "클릭하세요");
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                FirebaseDatabase.getInstance().getReference().child("Nutji").child("User").child("Place").child(Place).setValue(result)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // 프래그먼트 초기화 (리사이클러뷰 갱신)
                                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                ft.detach(usersettingss.this).attach(usersettingss.this).commit();
                                            }
                                        });
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                }
                            }).create().show();

            }
        });

        adapter.setOnItemClicklistener(new OnRecycleItemClickListener() {
            @Override
            public void onItemClick(UserListAdapter.ViewHolder holder, View view, int position) {
                Intent i = new Intent(getActivity().getApplicationContext(), addressSearch.class);
                getActivity().overridePendingTransition(0, 0);
                startActivityForResult(i, 10000);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String add_name = name.getText().toString();

                HashMap result = new HashMap<>();
                result.put("UserName", add_name);

                FirebaseDatabase.getInstance().getReference().child("Nutji").child("User").updateChildren(result);

            }
        });

        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);

        switch (requestCode) {
            case 10000:
                if (resultCode == Activity.RESULT_OK) {
                    String data = i.getExtras().getString("data");
                    if (data != null) {

                        HashMap result = new HashMap<>();
                        result.put("Address", data);

                        FirebaseDatabase.getInstance().getReference().child("Nutji").child("User").child("Place").child("회사").updateChildren(result);

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(this).attach(this).commit();
                    }
                    break;
                }
        }
    }
}

