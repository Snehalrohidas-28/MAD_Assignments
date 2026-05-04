package com.example.myremainder;

import android.app.*;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    EditText etTitle;
    Button btnTime, btnDate, btnSetAlarm;
    TextView tvDateTime;

    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitle = findViewById(R.id.etTitle);
        btnTime = findViewById(R.id.btnTime);
        btnDate = findViewById(R.id.btnDate);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        tvDateTime = findViewById(R.id.tvDateTime);

        calendar = Calendar.getInstance();
        updateDateTime();

        btnTime.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(calendar.get(Calendar.MINUTE))
                    .setTitleText("Select Time")
                    .build();

            picker.show(getSupportFragmentManager(), "TIME_PICKER");

            picker.addOnPositiveButtonClickListener(view -> {
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                updateDateTime();
            });
        });

        btnDate.setOnClickListener(v -> {
            DatePickerDialog dp = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        calendar.set(year, month, day);
                        updateDateTime();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dp.getDatePicker().setMinDate(System.currentTimeMillis());
            dp.show();
        });

        btnSetAlarm.setOnClickListener(v -> {

            String title = etTitle.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show();
                return;
            }

            long time = calendar.getTimeInMillis();

            if (time < System.currentTimeMillis()) {
                Toast.makeText(this, "Select future time", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("title", title);

            PendingIntent pi = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (am != null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!am.canScheduleExactAlarms()) {
                            Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(i);
                            Toast.makeText(this, "Allow exact alarm permission", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    am.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            time,
                            pi
                    );

                    Toast.makeText(this, "Alarm Set 🔔", Toast.LENGTH_SHORT).show();

                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Permission required", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvDateTime.setText(sdf.format(calendar.getTime()));
    }
}
