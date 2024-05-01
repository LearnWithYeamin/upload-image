package com.myeamin.uploadimage;


import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editTextName;
    Button uploadButton;
    PermissionHandler mPermissionHandler;
    String encodeImageString;
    AlertDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        editTextName = findViewById(R.id.editTextName);
        uploadButton = findViewById(R.id.uploadButton);

        mPermissionHandler = new PermissionHandler(this);

        // Check storage permission when clicking imageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check Storage Permission
                if (mPermissionHandler.checkStoragePermission()) {
                    // Open image picker when permission is granted
                    mGetContent.launch("image/*");
                } else {
                    mPermissionHandler.requestPermissions();
                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadIntoDatabase();
            }
        });

    } // onCreate Bundle end here...

    // Write this code where onCreate Bundle is end...
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && permissions.length == grantResults.length) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (!allPermissionsGranted) {
                mPermissionHandler.showDialog(permissions, requestCode);
            } else {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle result of selecting an image
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    if (uri != null) {
                        try {
                            // Convert uri image to bitmap
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            // Set bitmap image into imageView
                            imageView.setImageBitmap(bitmap);

                            // Convert bitmap image to base64 string
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();
                            encodeImageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

    // Upload image to server
    private void uploadIntoDatabase() {
        // Get user Name from ediText input box
        String name = editTextName.getText().toString().trim();

        if (name.isEmpty() || encodeImageString.isEmpty()) {
            // Show error message if required fields are empty
            showAlertDialog("Warning Alert", "Some fields are empty. Please provide all values.");
        } else {
            String url = "your_api_url";
            showProgressDialog();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Dismiss progress dialog
                    customProgressDialog.dismiss();
                    // Reset image view and input fields
                    imageView.setImageResource(R.drawable.ic_launcher_background);
                    editTextName.setText("");
                    editTextName.setHint("Enter your Name");
                    showAlertDialog("Server Response", "" + response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Dismiss progress dialog and show error message
                    customProgressDialog.dismiss();
                    showAlertDialog("Error Alert", "Something went wrong. Please check your internet connection and try again.");
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("name", name);
                    params.put("image", encodeImageString);
                    return params;
                }
            };

            // Add stringRequest to requestQueue
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    // Show progress dialog
    public void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.custom_progress_dialog, null);

        // Set the custom layout to the dialog builder
        builder.setView(dialogView);
        builder.setCancelable(false);

        // Create the dialog
        customProgressDialog = builder.create();

        // Show the dialog
        customProgressDialog.show();
    }

    // Show alert dialog
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("" + title)
                .setCancelable(false)
                .setMessage("" + message)
                .setNegativeButton("OKAY", null)
                .show();
    }

} // Public class end here...