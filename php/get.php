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

// Base URL for where images are stored
$baseUrl = "https://example.com/images/";

// Initialize an array to hold the data that will be output
$data = array();  

// SQL query to retrieve all data from the usersData table
$getQuery = "SELECT * FROM image_table";  
$response = mysqli_query($conn, $getQuery);  

foreach ($response as $item) {
    // Extract each row's data
    $id = $item['id'];  
    $name = $item['name'];  
    $image = $item['image'];  

    // Prepare user information to be JSON encoded
    $userInfo['id'] = $id;  
    $userInfo['name'] = $name;  
    $userInfo['images'] = $baseUrl . $image;  // Concatenate base URL with the image file name
    array_push($data, $userInfo);  
} 

// Set the header to output JSON with UTF-8 encoding
header('Content-Type: application/json; charset=utf-8');

// Encode data array to JSON and output it
echo json_encode($data, JSON_UNESCAPED_UNICODE);

// Close the database connection
mysqli_close($conn);

?>
