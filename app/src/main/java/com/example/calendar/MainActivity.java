package com.example.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private RecyclerView calendarRecyclerView;
    private ArrayList<String> daysOfMonth;
    private Calendar calendar;
    Button prevButton;
    Button nextButton;
    TextView monthText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();

        prevButton = findViewById(R.id.prevButton);
        monthText = findViewById(R.id.current_month);
        nextButton = findViewById(R.id.nextButton);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);

        prevButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        updateCalendar();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        String day = null;
        if (dayText.length() > 0) {
            day = String.valueOf(dayText.charAt(0));  // 첫 번째 문자 추출
        }
        // 날짜 클릭 시 NoteActivity로 이동
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        noteActivityLauncher.launch(intent);

        String selectedDate = getSelectedDateString(day);
        intent.putExtra("selectedDate", selectedDate);
        startActivity(intent);
    }

    // prev, next Button 클릭 시 캘린더 갱신
    private void updateCalendar() {
        // TextView의 텍스트를 갱신
        String monthKey = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.getTime());
        monthText.setText(monthKey);

        // "yyyy-MM" 형식에서 year와 month를 분리
        String[] dateParts = monthKey.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);

        daysOfMonth = getDaysOfMonthArray();

        CalendarAdapter calendarAdapter = new CalendarAdapter(year, month, daysOfMonth, this, this);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));  // 7열 그리드
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    // yyyy-mm-dd 형태의 결과 리턴
    private String getSelectedDateString(String dayText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // 0이 1월이므로 +1
        return year + "-" + month + "-" + dayText;
    }

    // 해당 월의 ArrayList 계산 (비어있는 경우 빈셀)
    private ArrayList<String> getDaysOfMonthArray() {
        ArrayList<String> daysList = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // 해당 달의 시작 요일 구하기 (예: 일요일 = 1, 월요일 = 2 ...)
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 달의 첫날 전까지 빈 셀 추가
        for (int i = 1; i < firstDayOfWeek; i++) {
            daysList.add("");  // 빈 셀 추가
        }

        // 해당 월의 날짜 추가 (1 ~ maxDaysInMonth)
        int maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= maxDaysInMonth; day++) {
            daysList.add(String.valueOf(day));
        }

        return daysList;
    }

    private ActivityResultLauncher<Intent> noteActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // NoteActivity에서 저장 후 돌아왔을 때 처리
                    updateCalendar(); // 필요한 업데이트 작업 수행
                }
            });

}