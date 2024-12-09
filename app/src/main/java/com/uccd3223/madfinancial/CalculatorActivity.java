package com.uccd3223.madfinancial;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {
    private TextView displayTextView;
    private double firstNumber = Double.NaN;
    private double secondNumber;
    private String currentOperator = "";
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        displayTextView = findViewById(R.id.displayTextView);
        backButton = findViewById(R.id.backButton);

        // Back button click listener
        backButton.setOnClickListener(v -> onBackPressed());

        // Number buttons
        int[] numberButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(v -> numberClick(((Button)v).getText().toString()));
        }

        // Operator buttons
        findViewById(R.id.btnPlus).setOnClickListener(v -> operatorClick("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> operatorClick("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> operatorClick("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> operatorClick("/"));

        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
        findViewById(R.id.btnClear).setOnClickListener(v -> clear());
    }

    private void numberClick(String number) {
        if (displayTextView.getText().toString().equals("0")) {
            displayTextView.setText(number);
        } else {
            displayTextView.append(number);
        }
    }

    private void operatorClick(String operator) {
        if (!displayTextView.getText().toString().isEmpty()) {
            firstNumber = Double.parseDouble(displayTextView.getText().toString());
            currentOperator = operator;
            displayTextView.setText("");
        }
    }

    private void calculateResult() {
        if (!Double.isNaN(firstNumber)) {
            secondNumber = Double.parseDouble(displayTextView.getText().toString());
            double result = 0;

            switch (currentOperator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "*":
                    result = firstNumber * secondNumber;
                    break;
                case "/":
                    if (secondNumber != 0) {
                        result = firstNumber / secondNumber;
                    } else {
                        displayTextView.setText("Error");
                        return;
                    }
                    break;
            }

            displayTextView.setText(String.valueOf(result));
            firstNumber = result;
        }
    }

    private void clear() {
        displayTextView.setText("0");
        firstNumber = Double.NaN;
        secondNumber = 0;
        currentOperator = "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}