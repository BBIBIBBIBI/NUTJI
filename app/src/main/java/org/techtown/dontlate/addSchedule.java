package org.techtown.dontlate;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.techtown.dontlate.createalarm.CreateAlarmViewModel;
import org.techtown.dontlate.createalarm.TimePickerUtil;
import org.techtown.dontlate.data.Alarm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class addSchedule extends AppCompatActivity {

    private String starttime;
    private String endtime;
    private int Ahour;
    private int Amin;
    private String day;
    private String active;
    private String add_name;
    private String add_memo;
    private CreateAlarmViewModel model;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addschedule);

        model = ViewModelProviders.of(this).get(CreateAlarmViewModel.class);

        EditText SCname = (EditText) findViewById(R.id.SCName);
        EditText SCmemo = (EditText) findViewById(R.id.SCMemo);

        Intent intent = getIntent();
        day = intent.getStringExtra("요일");

        // Timepicker 시간 및 데이터 형식 커스텀
        TimePicker startpicker = (TimePicker) findViewById(R.id.startPicker);
        startpicker.setIs24HourView(true);
        startpicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int min) {
                starttime = hour + ":" + min;
                Ahour = hour;
                Amin = min;
            }
        });

        TimePicker endpicker = (TimePicker) findViewById(R.id.endPicker);
        endpicker.setIs24HourView(true);
        endpicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int min) {
                endtime = hour + ":" + min;
            }
        });

        CheckBox check = (CheckBox) findViewById(R.id.Check);
        active = String.valueOf(check.isChecked());

        Button okay = (Button) findViewById(R.id.Okay);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_name = SCname.getText().toString();
                add_memo = SCmemo.getText().toString();

                // 입력받은 값 HashMap으로 변환
                HashMap result = new HashMap<>();
                result.put("AlarmActive", active);
                result.put("EndTime", endtime);
                result.put("ScheduleName", add_name);
                result.put("ScheduleMemo", add_memo);
                result.put("StartTime", starttime);

                if(check.isChecked()){
                    // 알람 생성 메소드
                    scheduleAlarm();
                }

                // 일정 이름을 ID로 지정하고 데이터 삽입
                FirebaseDatabase.getInstance().getReference().child("Nutji").child("Schedule").child(day).child(add_name).setValue(result)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(addSchedule.this, "저장 완료", Toast.LENGTH_SHORT).show();
                            }
                        });

                finish();
                Intent intent = new Intent(addSchedule.this, editSchedule.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        Button back = (Button) findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void scheduleAlarm() {
        int alarmId = new Random().nextInt(Integer.MAX_VALUE);

        Alarm alarm = new Alarm(
                alarmId,
                Ahour,
                Amin,
                add_name,
                add_memo,
                System.currentTimeMillis(),
                true,
                true,
                true,
                false,
                false,
                false,
                false,
                false,
                false
        );

        model.insert(alarm);
        alarm.schedule(getApplicationContext());
    }
}
