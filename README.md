<p align="center">
  <a href="https://github.com/i-rin-eam">
    <img src="https://avatars.githubusercontent.com/u/154800878?s=400&u=5d18880cc28646190a19a971bfcdbc54644eab07&v=4" alt="Logo" width="100" height="100">
  </a> 
<h1 align='center'>Upload image into MySql database</h1>
<h3 align='center'>
    <a href="https://www.youtube.com/@LearnWithYeamin">Watch the video</a> to learn how to upload image into MySql database in Android Studio using PHP and Volley.
</p>
  
## Step 1: Request Runtime Storage Permissions: <a href="https://www.youtube.com/watch?v=I3nGvV--2IU">Watch the video</a>
  
## Step 2: Here is `activity_main.xml` code: 
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_margin="20dp"
    android:gravity="center_horizontal"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:clickable="true"
        android:foreground="?attr/selectableItemBackgroundBorderless"
        android:scaleType="fitXY"
        android:src="@drawable/ic_launcher_background" />


    <EditText
        android:id="@+id/editTextName"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_marginTop="20dp"
        android:hint="Enter a Name"
        android:inputType="text"
        android:textColor="@color/black" />

    <Button
        android:id="@+id/uploadButton"
        android:layout_width="300dp"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginTop="20dp"
        android:background="#006BBF"
        android:clickable="true"
        android:foreground="?attr/selectableItemBackground"
        android:text="Upload to Database"
        android:textColor="@color/white" />


</LinearLayout>
```

## Step 3: Pick Image from Gallery `MainActivity.java` code: 
```java
// Open image picker when permission is granted
mGetContent.launch("image/*");
```
```java
    // Handle result of selecting an image
    // Write this code where onCreate Bundle is end...
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
```
## Step 4: Volley SringRequest method `MainActivity.java` code: 
> [!NOTE]
> Add Volley Library in your project build.gradle (Module :app). <a href="https://google.github.io/volley/">Visit the documentation page.</a>

> In `AndroidManifest.xml` Add below Permissions.
```xml
<!-- Internet Permission -->
<uses-permission android:name="android.permission.INTERNET" />
```
```java               
    // Method for Upload image into server
    // Write this code where onCreate Bundle is end...
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
```
```java
    // Method for Show Alert Dialog
    // Write this code where onCreate Bundle is end...
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("" + title)
                .setCancelable(false)
                .setMessage("" + message)
                .setNegativeButton("OKAY", null)
                .show();
    }
```
```java
    // Method for Show progress dialog
    // Write this code where onCreate Bundle is end...
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
```
## Step 5: Here is `custom_progress_dialog.xml` code: 
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="20dp">

    <TextView
        android:id="@+id/textViewTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:text="Please Wait... Yeamin"
        android:textColor="@android:color/black"
        android:textSize="18sp" />

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/textViewTitle"
        android:layout_marginTop="20dp" />

    <TextView
        android:id="@+id/textViewMessage"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@id/textViewTitle"
        android:layout_marginLeft="10dp"
        android:layout_marginTop="30dp"
        android:layout_toRightOf="@id/progressBar"
        android:text="Trying to Upload Data"
        android:textColor="@android:color/black"
        android:textSize="16sp" />
</RelativeLayout>
```
## Step 5: Here is `upload.php` code: 
```php
<?php

// Database configuration settings
$hostName = "localhost"; 
$userName = "database_username"; 
$password = "database_password"; 
$dbName = "database_name"; 

// Establish connection with the MySQL database
$conn = mysqli_connect($hostName, $userName, $password, $dbName);

// Check if the database connection was successful
if (!$conn) {
    // If connection failed, terminate script execution and display an error message
    die("Connection failed: " . mysqli_connect_error());
}

// Set the character set of the database connection to 'utf8mb4' for full Unicode support
mysqli_set_charset($conn, "utf8mb4");

// Ensure that the table's collation is compatible with utf8mb4
$collationSql = "ALTER TABLE tableName CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
mysqli_query($conn, $collationSql);

// Handle image upload if the required parameters are provided
if (isset($_POST['image']) && isset($_POST['name'])) {
    $image = $_POST['image']; // Image data received via POST request
    $name = $_POST['name']; // Name data received via POST request

    // Define the target directory where the image will be stored
    $targetFolder = "folderName";  

    // Generate a unique filename for the image
    $imageName = rand() . "_" . time() . ".jpeg";

    // Construct the full path to save the image
    $fullPath = $targetFolder . "/" . $imageName;

    // Save the image file to the target directory
    file_put_contents($fullPath, base64_decode($image));  

    // Prepare and execute the SQL query to insert image data into the database
    $insertQuery = "INSERT INTO tableName (image, name) VALUES ('$imageName', '$name')";  
    $response = mysqli_query($conn, $insertQuery);

    // Check if the query was successful
    if ($response) {
        echo "Image Uploaded Successfully."; // Display success message
    } else {
        echo "Failed to upload image."; // Display error message
    }
} else {
    // If required parameters are not provided, display an error message
    echo "Invalid request parameters.";
}

// Close the database connection
mysqli_close($conn);

?>
```
## Authors

**MD YEAMIN** - Android Software Developer <a href="https://www.youtube.com/@LearnWithYeamin">**(Learn With Yeamin)**</a> 

<h1 align="center">Thank You ❤️</h1>
