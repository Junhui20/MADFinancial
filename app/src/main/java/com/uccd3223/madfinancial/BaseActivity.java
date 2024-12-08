package com.uccd3223.madfinancial;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupToolbar(String title) {
        ImageButton backButton = findViewById(R.id.backButton);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }
}
