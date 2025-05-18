<?php
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

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $sessionemail = $_POST["sessionemail"];
    $senderemail = $_POST["senderemail"];
    $from_location = $_POST["from_location"];
    $to_location = $_POST["to_location"];

    // Insert new connection with locations
    $sql_insert = "INSERT INTO connected_trip_details (senders_email, adminemail, from_location, to_location, status_of_trip) 
                   VALUES (?, ?, ?, ?, 'Active')";
    $stmt = $conn->prepare($sql_insert);
    $stmt->bind_param("ssss", $senderemail, $sessionemail, $from_location, $to_location);

    if ($stmt->execute()) {
        echo "success";
    } else {
        echo "error";
    }
    $stmt->close();
}

$conn->close();
?>
