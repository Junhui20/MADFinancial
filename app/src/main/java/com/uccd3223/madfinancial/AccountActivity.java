package com.uccd3223.madfinancial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private EditText etNickname, etEmail, etMobileNumber;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;

    private static final String PREFS_NAME = "UserPreferences";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Set title and enable back button
        setTitle("Personal Information");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        etNickname = findViewById(R.id.etNickname);
        etEmail = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        // Load saved user data
        loadUserInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle back button press
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_confirm) {
            // Handle confirm button press
            saveUserInfo();
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show();

            // Navigate back to the home page
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Prevent duplicate activity
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu, menu);
        return true;
    }

    private void saveUserInfo() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Save nickname
        editor.putString(KEY_NICKNAME, etNickname.getText().toString());

        // Save gender
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId == R.id.rbMale) {
            gender = "Male";
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = "Female";
        }
        editor.putString(KEY_GENDER, gender);

        // Save email
        editor.putString(KEY_EMAIL, etEmail.getText().toString());

        // Save mobile number
        editor.putString(KEY_MOBILE, etMobileNumber.getText().toString());

        editor.apply(); // Save changes
    }

    private void loadUserInfo() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load and display nickname
        String nickname = preferences.getString(KEY_NICKNAME, "");
        etNickname.setText(nickname);

        // Load and display gender
        String gender = preferences.getString(KEY_GENDER, "");
        if ("Male".equals(gender)) {
            rbMale.setChecked(true);
        } else if ("Female".equals(gender)) {
            rbFemale.setChecked(true);
        }

        // Load and display email
        String email = preferences.getString(KEY_EMAIL, "");
        etEmail.setText(email);

        // Load and display mobile number
        String mobile = preferences.getString(KEY_MOBILE, "");
        etMobileNumber.setText(mobile);
    }
}
