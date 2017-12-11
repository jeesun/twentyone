package com.jeesun.twentyone;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView tvGithub;

    String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.about);

        tvGithub = findViewById(R.id.github);

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("version: " + versionName + "\n");
        sb.append("author: jeesun\n");
        sb.append("github: ");
        sb.append(getString(R.string.github));
        tvGithub.setText(sb);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            AboutActivity.this.finish();
            return false;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
}
