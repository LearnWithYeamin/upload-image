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
