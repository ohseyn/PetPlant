package com.example.petplant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class waterquest_clear_answer extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterquest_clear_answer);

        String photoPath = getIntent().getStringExtra("photoPath");
        if (photoPath != null) {
            ImageView imageView = findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            Bitmap rotatedBitmap = rotateImageIfRequired(bitmap, photoPath);
            imageView.setImageBitmap(rotatedBitmap);
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap img, String photoPath) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
            return img;
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(this, reward_quiz.class);
        startActivity(intent);
    }
}
