package io.github.aktera.scheduleapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private CompactCalendarView compactCalendarView;
    private Date currentDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // カレンダーをセットアップする
        compactCalendarView = this.findViewById(R.id.calendar);
        compactCalendarView.setUseThreeLetterAbbreviation(false);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(true);
        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.JAPAN);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date date) {
                // キャプションを更新する
                currentDate = date;
                setAppTitle();

                // 日付データを渡して、メモアクティビティを開く
                Intent intent = new Intent(getApplication(), MemoActivity.class);
                intent.putExtra("Date", currentDate.getTime());
                startActivity(intent);
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // キャプションを更新する
                currentDate = firstDayOfNewMonth;
                setAppTitle();
            }
        });

        setAppTitle();
    }

    private void setAppTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月のスケジュール");
        setTitle(sdf.format(currentDate));
    }
}
