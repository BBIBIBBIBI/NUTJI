package org.techtown.dontlate;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class scheduless extends Fragment {

    // Firebase 호출 시점 지정
    private interface FirebaseCallback {
        void onCallback(List<String> listA, List<String> listB, List<String> listN, List<String> listM);
    }

    private View view;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String day;
    private String elementS;
    private String elementE;

    LinearLayout table, summary;
    float time = 0;

    ArrayList list1 = new ArrayList<ScheduleListItem>();
    ArrayList list2 = new ArrayList<ScheduleListItem>();
    ArrayList list3 = new ArrayList<ScheduleListItem>();
    ArrayList list4 = new ArrayList<ScheduleListItem>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.schedules, container, false);

        table = (LinearLayout) view.findViewById(R.id.table);
        summary = (LinearLayout) view.findViewById(R.id.summary);

        Button addbtn = view.findViewById(R.id.Addbtn);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 리스트 다이얼로그 생성 후 editSchedule로 요일 값 전달
                Intent intent = new Intent(getActivity().getApplicationContext(), editSchedule.class);
                final String[] items = new String[]{"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("요일을 선택하세요");
                builder.setItems(items, (dialog, pos) -> {
                    String selectedText = items[pos];
                    if (selectedText.equals("월요일")) {
                        intent.putExtra("요일", "월요일");
                        startActivity(intent);
                    } else if (selectedText.equals("화요일")) {
                        intent.putExtra("요일", "화요일");
                        startActivity(intent);
                    } else if (selectedText.equals("수요일")) {
                        intent.putExtra("요일", "수요일");
                        startActivity(intent);
                    } else if (selectedText.equals("목요일")) {
                        intent.putExtra("요일", "목요일");
                        startActivity(intent);
                    } else if (selectedText.equals("금요일")) {
                        intent.putExtra("요일", "금요일");
                        startActivity(intent);
                    } else if (selectedText.equals("토요일")) {
                        intent.putExtra("요일", "토요일");
                        startActivity(intent);
                    } else if (selectedText.equals("일요일")) {
                        intent.putExtra("요일", "일요일");
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });

        // 시스템 시간 가져오기
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();
        day = dayFormat.format(calendar.getTime());

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        readData(new FirebaseCallback() {
            @Override
                public void onCallback(List<String> listS, List<String> listE, List<String> listN, List<String> listM) {

                // 시간을 계산하여 테이블 크기를 동적 할당
                for (int i=0; i<listS.size(); i++) {
                    elementS = listS.get(i);
                    elementE = listE.get(i);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    Date inputsijak = null;
                    Date inputggut = null;

                    try {
                        inputsijak = simpleDateFormat.parse(elementS);
                        inputggut = simpleDateFormat.parse(elementE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long min = (inputggut.getTime() - inputsijak.getTime()) / (60 * 1000);
                    time = (min / 10);

                    // 일정 테이블 색상 랜덤 설정
                    ArrayList<String> color = new ArrayList<>();
                    color.add("#C5E1A5");
                    color.add("#7CB342");
                    color.add("#9CCC65");
                    color.add("#558B2F");
                    color.add("#AED581");
                    color.add("#689F38");
                    color.add("#8BC34A");

                    table.removeAllViews();
                    summary.removeAllViews();

                    Random random = new Random();
                    int ran = 0;

                    // 일정 테이블 추가
                    for (int j=0; j<listN.size(); j++) {

                        ran = random.nextInt(6 - 0 + 1) + 0;

                        TextView nameText = new TextView(getActivity().getApplicationContext());
                        nameText.setText(listN.get(j));
                        nameText.setTextColor(Color.parseColor("#FFFFFF"));
                        nameText.setBackgroundColor(Color.parseColor(color.get(ran)));
                        nameText.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                time);
                        params.topMargin = 2;
                        table.addView(nameText, params);
                    }

                    // 일정 내용 텍스트 추가
                    for (int k=0; k<listS.size(); k++) {

                        TextView memoText = new TextView(getActivity().getApplicationContext());
                        memoText.setText(listM.get(k));
                        memoText.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                time);
                        paramss.topMargin = 2;
                        summary.addView(memoText, paramss);
                    }
                }
            }
        });

        return view;
    }

    // Firebase 시간 순서대로 호출
    public void readData(FirebaseCallback firebaseCallback) {
        databaseReference.child("Nutji").child("Schedule").child(day).orderByChild("StartTime")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear(); list2.clear(); list3.clear(); list4.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String str1 = dataSnapshot.child("StartTime").getValue(String.class);
                    String str2 = dataSnapshot.child("EndTime").getValue(String.class);
                    String str3 = dataSnapshot.child("ScheduleName").getValue(String.class);
                    String str4 = dataSnapshot.child("ScheduleMemo").getValue(String.class);
                    list1.add(str1); list2.add(str2); list3.add(str3); list4.add(str4);
                    firebaseCallback.onCallback(list1, list2, list3, list4);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}


