package io.github.aktera.scheduleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoActivity extends AppCompatActivity {

    Date currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Intentから日付データを受け取る
        Intent intent = getIntent();
        currentDate = new Date();
        currentDate.setTime(intent.getLongExtra("Date", -1));

        // ツールバーのタイトルを変更する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日の予定");
        toolbar.setTitle(sdf.format(currentDate));
        setSupportActionBar(toolbar);

        // ツールバーに戻るボタンを追加する
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // メモを読み込む
        loadMemo();
    }

    // ウィンドウが非表示にされた
    @Override
    protected void onPause() {
        super.onPause();

        // メモが存在するならメモを保存する
        if (isMemoExists()) {
            saveMemo();
        }
    }

    // メニューアイテム選択時に呼ばれる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            // 戻るメニュー
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    // メモを読み込む
    private void loadMemo() {
        // メモファイルパスを取得する
        String path = getMemoFilePath(currentDate);
        Log.d("ScheduleAppDebug", path);

        // テキストビュー
        TextView viewMemo = (TextView)findViewById(R.id.editMemo);
        String strMemo = "";

        try {
            // プライベートファイルを読み込むにはopenFileInputを使用する
            FileInputStream fis = getApplicationContext().openFileInput(path);
            InputStreamReader isr = new InputStreamReader(fis, "utf8");
            BufferedReader br = new BufferedReader(isr);

            // テキスト読み込み
            // 最後の行が改行で終わってない場合でも改行を付加してしまうのが気に入らないが
            while (true) {
                String line  = br.readLine();
                if (line == null) {
                    break;
                }
                strMemo += line + "\n";
            }

            // 読み込んだテキストファイルをビューに反映する
            viewMemo.setText(strMemo);
            br.close();
        }
        catch (FileNotFoundException e) {
            // 初回起動時にはファイルがない
        }
        catch (IOException e) {
            showMessage(e.toString());
        }
    }

    // メモを保存する
    private void saveMemo() {
        // メモファイルパスを取得する
        String path = getMemoFilePath(currentDate);

        // テキストビューから文字列を取得する
        TextView viewMemo = (TextView) findViewById(R.id.editMemo);
        String strMemo = viewMemo.getText().toString();

        try {
            // プライベートファイルに書き込むにはopenFileOutputを使用する
            FileOutputStream fos = getApplicationContext().openFileOutput(path, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf8");
            BufferedWriter bw = new BufferedWriter(osw);

            // ビューのテキストをテキストファイルに書き込む
            bw.write(strMemo);
            bw.flush();
            bw.close();
        }
        catch (IOException e) {
            showMessage(e.toString());
        }
    }

    // メモが存在するかチェックする
    private boolean isMemoExists() {
        // テキストビューから文字列を取得する
        TextView viewMemo = (TextView) findViewById(R.id.editMemo);
        String strMemo = viewMemo.getText().toString();

        // メモが入力されているならtrueで戻る
        if (!strMemo.isEmpty()) {
            return true;
        }
        Log.d("ScheduleAppDebug", "strMemo.isEmpty");

        // メモが存在しなくて、ファイルが存在するなら、ファイルを削除しておく
        File file = this.getFileStreamPath(getMemoFilePath(currentDate));
        if (file.exists()) {
            Log.d("ScheduleAppDebug", "file.exists");
            file.delete();
        }

        return false;
    }

    // メモファイルパスを取得する
    private String getMemoFilePath(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return "ScheduleApp_" + sdf.format(date) + ".memo";
    }

    // 画面下部にメッセージをポップアップする
    private void showMessage(String message) {
        Snackbar.make(this.getWindow().getDecorView(), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
