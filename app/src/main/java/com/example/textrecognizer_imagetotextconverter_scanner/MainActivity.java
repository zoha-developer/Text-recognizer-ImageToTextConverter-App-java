package com.example.textrecognizer_imagetotextconverter_scanner;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView recognizedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recognizedTextView = findViewById(R.id.recognizedTextView);

        // Get the recognized text data from the intent
        String recognizedText = getIntent().getStringExtra("recognizedText");
        if (recognizedText != null) {
            // Display recognized text in TextView
            recognizedTextView.setText(recognizedText);
        } else {
            Toast.makeText(this, "No recognized text found", Toast.LENGTH_SHORT).show();
        }
    }
}
