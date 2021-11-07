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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class usersettingss extends Fragment {

    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private ArrayList<UserListItem> arrayList;
    private TextView address;
    private EditText name;

    DatabaseReference databaseReference;

    private Button save;
    private Button addPlace;

    String default_name;
    String default_address;
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        recyclerView.setLayoutManager(linearLayoutManager);


        FirebaseDatabase.getInstance().getReference().child("Nutji").child("User").child("UserName").addValueEventListener(new ValueEventListener() {
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

//        FirebaseDatabase.getInstance().getReference().child("Nutji").child("User").child("Place").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                arrayList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    UserListItem user = dataSnapshot.getValue(UserListItem.class);
//                    arrayList.add(user);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        adapter = new UserListAdapter();
        recyclerView.setAdapter(adapter);


        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = new String[]{"집", "회사", "학교"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("장소를 선택하세요")
                        .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Place = items[which];
                                adapter.addItem(new UserListItem(Place, "클릭하세요"));
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();
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

                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Nutji").child("User").updateChildren(result);

            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        //conditionRef.add
    }


        public void onActivityResult(int requestCode, int resultCode, Intent i) {
            super.onActivityResult(requestCode, resultCode, i);
            Log.i("test", "onActivityResult");

            switch (requestCode) {
                case 10000:
                    if (resultCode == Activity.RESULT_OK) {
                        String data = i.getExtras().getString("data");
                        if (data != null) {

                            UserListItem item = new UserListItem(Place, data);
                            ArrayList<UserListItem> list = new ArrayList<>();
                            list.add(item);

                            adapter.setItems(list);
                            adapter.notifyDataSetChanged();
                            //adapter.notifyItemChanged(adapter.getItemCount()-1, "data");
                        }
                    }
                    break;
            }
        }
    }

