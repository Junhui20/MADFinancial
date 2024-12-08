package com.uccd3223.madfinancial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Set title and enable back button
        setTitle("Share Our App");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set up click listeners for each social media icon
        setupSocialMediaRedirects();
    }

    private void setupSocialMediaRedirects() {
        findViewById(R.id.facebookIcon).setOnClickListener(v -> openSocialMediaPage("https://www.facebook.com/login"));
        findViewById(R.id.instagramIcon).setOnClickListener(v -> openSocialMediaPage("https://www.instagram.com/accounts/login"));
        findViewById(R.id.whatsappIcon).setOnClickListener(v -> openSocialMediaPage("https://web.whatsapp.com"));
        findViewById(R.id.youtubeIcon).setOnClickListener(v -> openSocialMediaPage("https://accounts.google.com/ServiceLogin?service=youtube"));
        findViewById(R.id.twitterIcon).setOnClickListener(v -> openSocialMediaPage("https://twitter.com/login"));
    }

    private void openSocialMediaPage(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Handle back button press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
