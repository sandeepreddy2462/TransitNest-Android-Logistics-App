<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "user_database";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "error" => "Database connection failed: " . $conn->connect_error]));
}
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $from_location = $_POST['from_location'];
    $to_location = $_POST['to_location'];
    $admin_email = $_POST['admin_email'];
    $traveller_email = $_POST['traveller_email'];

    $query = "SELECT status_of_trip FROM connected_trip_details 
              WHERE from_location = ? AND to_location = ? 
              AND adminemail = ? AND traveller_email = ?";
    
    $stmt = $conn->prepare($query);
    $stmt->bind_param("ssss", $from_location, $to_location, $admin_email, $traveller_email);
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
        echo "no_parcel";
    }
    
    $stmt->close();
    $conn->close();
}
?>
