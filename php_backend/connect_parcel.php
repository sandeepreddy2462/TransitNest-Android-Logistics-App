<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "user_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode(["success" => false, "error" => "Database connection failed: " . $conn->connect_error]));
}

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $from_location = $_POST['from_location'];
    $to_location = $_POST['to_location'];
    $admin_email = $_POST['admin_email'];
    $traveller_email = $_POST['traveller_email'];
    $status_of_trip = 'Active';
    $senders_email = '';

    $query = "INSERT INTO connected_trip_details (senders_email,adminemail, status_of_trip, from_location, to_location, traveller_email) 
              VALUES (?, ?, ?, ?, ?, ?)";

    $stmt = $conn->prepare($query);
    $stmt->bind_param("ssssss", $senders_email, $admin_email, $status_of_trip, $from_location, $to_location, $traveller_email);

    if ($stmt->execute()) {
        echo "success";
    } else {
        echo "error";
    }

    $stmt->close();
    $conn->close();
}
?>
