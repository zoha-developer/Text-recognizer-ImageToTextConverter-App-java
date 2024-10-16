package com.example.textrecognizer_imagetotextconverter_scanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.Text;

import java.io.File;
import java.io.IOException;

public class homescreen extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private ImageView imageView;
    private Uri photoURI;
    private TextRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        imageView = findViewById(R.id.imageView);
        Button buttonPickImage = findViewById(R.id.buttonPickImage);
        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        Button buttonSubmitImage = findViewById(R.id.buttonSubmitImage);

        // Initialize the Text Recognizer
        recognizer = TextRecognition.getClient();

        buttonPickImage.setOnClickListener(v -> openGallery());
        buttonTakePhoto.setOnClickListener(v -> takePhoto());
        buttonSubmitImage.setOnClickListener(v -> submitImage());

        checkPermissions();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                photoURI = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile("JPEG_" + timeStamp, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                loadImageFromUri(imageUri);
            } else if (requestCode == CAMERA_REQUEST) {
                imageView.setImageURI(photoURI);
                recognizeTextFromImage(photoURI);
            }
        }
    }

    private void loadImageFromUri(Uri imageUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            imageView.setImageBitmap(bitmap);
            recognizeTextFromImage(imageUri); // Call this method to recognize text after loading the image
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizeTextFromImage(Uri imageUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            InputImage image = InputImage.fromBitmap(bitmap, 0);

            recognizer.process(image)
                    .addOnSuccessListener(visionText -> handleRecognizedText(visionText))
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, "Text recognition failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRecognizedText(Text visionText) {
        String resultText = visionText.getText();
        // Start MainActivity with the recognized text
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("recognizedText", resultText);
        startActivity(intent);
    }

    private void submitImage() {
        // If the user has selected an image, proceed with text recognition
        // Otherwise, show a toast message
        if (photoURI != null) {
            recognizeTextFromImage(photoURI); // Use the photo taken from camera
        } else {
            Toast.makeText(this, "Please select or capture an image first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted
            } else {
                Toast.makeText(this, "Permissions are required for this app to function", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
