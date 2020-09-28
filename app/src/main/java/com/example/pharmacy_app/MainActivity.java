package com.example.pharmacy_app;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    Button capturebtn;
    Button savebtn;
    ImageView imgView;

    Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = findViewById(R.id.image_view);
        capturebtn = findViewById(R.id.capture);
        savebtn = findViewById(R.id.save_img);

        capturebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permission, 1000);
                    } else {
                        AccessCamera();
                    }
                } else {
                    AccessCamera();
                }
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable bitdraw = (BitmapDrawable) imgView.getDrawable();
                Bitmap bmap = bitdraw.getBitmap();

                FileOutputStream outStream = null;
                File file = Environment.getExternalStorageDirectory();
                File dir = new File(file.getAbsolutePath() + "/DemoPics");
                dir.mkdir();

                String filename = String.format("%d.jpeg", System.currentTimeMillis());
                File outfile = new File(dir, filename);

                try {
                    outStream = new FileOutputStream(outfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    outStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void AccessCamera() {
        ContentValues vl = new ContentValues();
        vl.put(MediaStore.Images.Media.TITLE, "First Pic");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, vl);
        Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cam.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cam, 1001);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            AccessCamera();
        } else {
            Toast.makeText(this, "Permission Denied..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            imgView.setImageURI(image_uri);
            savebtn.setEnabled(true);
        }
    }
}
