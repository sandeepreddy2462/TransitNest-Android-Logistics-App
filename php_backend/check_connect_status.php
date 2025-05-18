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

    // Check if connection exists
    $sql_check = "SELECT status_of_trip FROM connected_trip_details WHERE senders_email = ? AND adminemail = ?";
    $stmt = $conn->prepare($sql_check);
    $stmt->bind_param("ss",$senderemail, $sessionemail);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        if ($row["status_of_trip"] == "Active") {
            echo "Active";
        } else {
            echo "Completed";
        }
    } else {
        echo "no_trips";
    }
    $stmt->close();
}

$conn->close();
?>
