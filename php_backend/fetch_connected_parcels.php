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
    die(json_encode(["success" => false, "error" => "Database connection failed: " . $conn->connect_error]));
}
$sql = "SELECT u.username, u.userphonenumber, t.useremail AS travelleremail, c.status_of_trip, c.from_location, c.to_location FROM connected_trip_details c JOIN travellers t ON c.traveller_email = t.useremail JOIN users u ON t.useremail = u.useremail WHERE c.from_location = t.travelling_from AND c.to_location = t.travelling_to";

$result = $conn->query($sql);
$data = [];

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
}
echo json_encode($data);
?>
