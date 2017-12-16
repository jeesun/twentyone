package com.jeesun.twentyone;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        String SearchContent = getIntent().getStringExtra(SearchManager.QUERY);
        Toast.makeText(this, SearchContent, Toast.LENGTH_SHORT).show();
    }
}
