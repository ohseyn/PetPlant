package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Home_smellquest extends AppCompatActivity {

    private EditText inputEditText;
    private TextView charCountTextView;
    private Button nextButton3;
    private Button back_home;

    private static final int MAX_CHAR_COUNT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smellquest); // Replace with your layout file name

        // Initialize views
        inputEditText = findViewById(R.id.inputEditText);
        charCountTextView = findViewById(R.id.charCountTextView);
        nextButton3 = findViewById(R.id.nextButton3);
        back_home = findViewById(R.id.back_home);

        // Set initial character count
        charCountTextView.setText("0/" + MAX_CHAR_COUNT);

        // Add TextWatcher to monitor text changes in the EditText
        inputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the character count as text changes
                int currentCharCount = s.length();
                charCountTextView.setText(currentCharCount + "/" + MAX_CHAR_COUNT);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Additional validation can be added here if needed
            }
        });

        // Handle next button click (if you need to perform an action on click)
        nextButton3.setOnClickListener(view -> {
            // Perform the action when the next button is clicked
            // For example, you can fetch the input text or validate it here
            String userInput = inputEditText.getText().toString().trim();

            if (!userInput.isEmpty()) {
                // Proceed to the next step or save the text input
                // Example: move to the next screen or save data to Firebase
            }
        });

        Button back_home = findViewById(R.id.back_home);
        back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                startActivity(intent);
            }
        });

        Button nextButton3 = findViewById(R.id.nextButton3);
        nextButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), smellquest_message.class);
                startActivity(intent);
            }
        });
    }
}
