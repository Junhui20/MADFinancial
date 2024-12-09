package com.uccd3223.madfinancial;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.ForegroundColorSpan;
import android.text.Spanned;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView burgerMenu;
    private ImageView ivMenu;
    private PieChart pieChart;
    private android.widget.Spinner metricSpinner;
    private android.widget.Spinner timeSpinner;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        burgerMenu = findViewById(R.id.burgerMenu);
        ivMenu = findViewById(R.id.ivMenu);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Initialize card views
        CardView cardTransaction = findViewById(R.id.cardTransaction);
        CardView cardCurrencyExchange = findViewById(R.id.cardCurrencyExchange);
        CardView cardSavings = findViewById(R.id.cardSavings);
        CardView cardHistory = findViewById(R.id.cardHistory);
        CardView cardCalculator = findViewById(R.id.cardCalculator);

        // Set click listeners for cards
        cardTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
            startActivity(intent);
        });

        cardCurrencyExchange.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CurrencyExchangeActivity.class);
            startActivity(intent);
        });

        cardSavings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SavingGoalsActivity.class);
            startActivity(intent);
        });

        cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        cardCalculator.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
            startActivity(intent);
        });

        // Burger menu click listener
        burgerMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.openDrawer(navigationView);
            } else {
                drawerLayout.closeDrawer(navigationView);
            }
        });

        // 3-dot menu click listener
        ivMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, ivMenu);
            popupMenu.getMenuInflater().inflate(R.menu.drawer_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.action_account) {
                    Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivity(accountIntent);
                    return true;
                } else if (itemId == R.id.action_help) {
                    Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                    startActivity(helpIntent);
                    return true;
                } else if (itemId == R.id.action_support) {
                    Intent supportIntent = new Intent(MainActivity.this, SupportActivity.class);
                    startActivity(supportIntent);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        // Navigation drawer item click listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Stay on current activity
                Toast.makeText(MainActivity.this,
                        "Back to Home", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_currency) {
                Intent intent = new Intent(MainActivity.this, CurrencyExchangeActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_profile) {
                Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(accountIntent);
            } else if (id == R.id.nav_transactions) {
                Intent intent = new Intent(MainActivity.this, TransactionActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_savings) {
                Intent intent = new Intent(MainActivity.this, SavingGoalsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_profile) {
                Toast.makeText(MainActivity.this,
                        "Profile Selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_share) {
                Intent accountIntent = new Intent(MainActivity.this, ShareActivity.class);
                startActivity(accountIntent);
            } else if (id == R.id.nav_feedback) {
                Intent accountIntent = new Intent(MainActivity.this, SupportActivity.class);
                startActivity(accountIntent);
            } else if (id == R.id.nav_logout) {
                Toast.makeText(MainActivity.this,
                        "Goodbye!", Toast.LENGTH_SHORT).show();
                finish();
            }

            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        setupFinancialAnalysis();
    }

    private void setupFinancialAnalysis() {
        dbHelper = new DatabaseHelper(this);
        pieChart = findViewById(R.id.pieChart);
        metricSpinner = findViewById(R.id.metricSpinner);
        timeSpinner = findViewById(R.id.timeSpinner);

        // Setup spinners
        android.widget.ArrayAdapter<String> metricAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Category", "Type"});
        metricAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metricSpinner.setAdapter(metricAdapter);

        android.widget.ArrayAdapter<String> timeAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Today", "This Week", "This Month", "This Year"});
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        // Setup listeners
        metricSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateChart();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        timeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateChart();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Setup pie chart
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawEntryLabels(false); // Hide default labels
        pieChart.setHighlightPerTapEnabled(true); // Enable highlighting on tap

        // Configure value display
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        // Enable rotation
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // Configure highlight
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setOnChartValueSelectedListener(new com.github.mikephil.charting.listener.OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(com.github.mikephil.charting.data.Entry e, com.github.mikephil.charting.highlight.Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pe = (PieEntry) e;
                    String label = pe.getLabel();
                    float value = pe.getValue();

                    // Calculate total from all entries
                    float total = 0f;
                    PieData data = pieChart.getData();
                    if (data != null) {
                        for (PieEntry entry : data.getDataSet().getEntriesForXValue(0)) {
                            total += entry.getValue();
                        }
                    }

                    float percentage = (total > 0) ? (value / total * 100) : 0;

                    String message = String.format(Locale.getDefault(), "%s: %.1f%%\nRM %.2f",
                            label, percentage, value);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Do nothing when no slice is selected
            }
        });

        com.github.mikephil.charting.components.Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);

        updateChart();
    }

    private void updateChart() {
        String metric = metricSpinner.getSelectedItem().toString();
        String timeFrame = timeSpinner.getSelectedItem().toString();

        // Get start time based on selected time frame
        long startTime = getStartTime(timeFrame);

        // Query transactions
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"type", "category", "amount"};
        String selection = "timestamp >= ?";
        String[] selectionArgs = {String.valueOf(startTime)};

        Map<String, Float> dataMap = new HashMap<>();
        float total = 0;

        try (Cursor cursor = db.query(
                "transactions",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null)) {

            while (cursor.moveToNext()) {
                String key = metric.equals("Category") ?
                        cursor.getString(cursor.getColumnIndexOrThrow("category")) :
                        cursor.getString(cursor.getColumnIndexOrThrow("type"));
                float amount = Math.abs(cursor.getFloat(cursor.getColumnIndexOrThrow("amount")));

                dataMap.put(key, dataMap.getOrDefault(key, 0f) + amount);
                total += amount;
            }
        }

        // Create pie data entries
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int[] COLORS = {
                Color.rgb(64, 89, 128),
                Color.rgb(149, 165, 124),
                Color.rgb(217, 184, 162),
                Color.rgb(191, 134, 134),
                Color.rgb(179, 48, 80)
        };

        int colorIndex = 0;
        for (Map.Entry<String, Float> entry : dataMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            colors.add(COLORS[colorIndex % COLORS.length]);
            colorIndex++;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();

        // Set center text
        pieChart.setCenterText(generateCenterSpannableText(total));
    }

    private SpannableString generateCenterSpannableText(float total) {
        String text = String.format(Locale.getDefault(), "Total\nRM %.2f", total);
        SpannableString ss = new SpannableString(text);

        int lineBreakIndex = text.indexOf('\n');
        ss.setSpan(new RelativeSizeSpan(0.7f), 0, lineBreakIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), lineBreakIndex + 1, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.GRAY), 0, lineBreakIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    private long getStartTime(String timeFrame) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        switch (timeFrame) {
            case "Today":
                // Already set to start of today
                break;
            case "This Week":
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                break;
            case "This Month":
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case "This Year":
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                break;
        }

        return calendar.getTimeInMillis();
    }
}
