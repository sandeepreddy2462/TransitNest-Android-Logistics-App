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


if ($_SERVER["REQUEST_METHOD"] == "POST") { 
  $from_location = isset($_POST['from_location']) ? trim($_POST['from_location']) : '';
  $to_location = isset($_POST['to_location']) ? trim($_POST['to_location']) : '';

  // Validate inputs
  if (empty($from_location) || empty($to_location)) {
      echo json_encode(["error" => "Both locations are required"]);
      exit();
  }

  // SQL query to join users and travelers tables
  $sql = "SELECT u.username, u.useremail,u.userphonenumber, t.departure_datetime, t.arrival_datetime, t.transportation_mode FROM travellers t JOIN users u ON t.useremail = u.useremail WHERE t.travelling_from = ? AND t.travelling_to = ?";
  $stmt = $conn->prepare($sql);
  $stmt->bind_param("ss", $from_location, $to_location);
  $stmt->execute();
  $result = $stmt->get_result();

  $travelers = [];

  while ($row = $result->fetch_assoc()) {
      $travelers[] = $row;
  }
  echo json_encode($travelers);

$stmt->close();
$conn->close();
}
?>
