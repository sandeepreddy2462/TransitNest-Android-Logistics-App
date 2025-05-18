<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

// Database connection details
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "user_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Database connection failed: " . $conn->connect_error);
}

// Get POST data
$senders_email = $_POST['senders_email'];
$from_location = $_POST['from_location'];
$to_location = $_POST['to_location'];
$adminemail = $_POST['adminemail'];

$sql = "UPDATE connected_trip_details 
        SET status_of_trip = 'Completed' 
        WHERE senders_email = ? 
        AND from_location = ? 
        AND to_location = ? 
        AND adminemail = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ssss", $senders_email, $from_location, $to_location, $adminemail);

if ($stmt->execute()) {
    echo "success";
} else {
    echo "error";
}

$stmt->close();
$conn->close();
?>
