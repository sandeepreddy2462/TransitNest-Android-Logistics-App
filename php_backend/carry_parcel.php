<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "user_database";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "error" => "Database connection failed: " . $conn->connect_error]));
}

if ($_SERVER["REQUEST_METHOD"] === "POST") { 
    $traveller_mail = isset($_POST["travelleremail"]) ? $_POST["travelleremail"] : "";
    $from = isset($_POST["travelling_from"]) ? $_POST["travelling_from"] : "";
    $to = isset($_POST["travelling_to"]) ? $_POST["travelling_to"] : "";
    $departure = isset($_POST["departure_datetime"]) ? $_POST["departure_datetime"] : "";
    $arrival = isset($_POST["arrival_datetime"]) ? $_POST["arrival_datetime"] : "";
    $transport = isset($_POST["transportation_mode"]) ? $_POST["transportation_mode"] : "";

    if (empty($from) || empty($to) || empty($departure) || empty($arrival) || empty($transport)) {
        echo json_encode(["success" => false, "error" => "Missing required fields"]);
        exit();
    }

    // Prepare SQL statement
    $sql = "INSERT INTO travellers (useremail, travelling_from, travelling_to, departure_datetime, arrival_datetime, transportation_mode) 
            VALUES (?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssssss", $traveller_mail, $from, $to, $departure, $arrival, $transport);
    if ($stmt->execute()) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false, "error" => $stmt->error]);
    }

    $stmt->close();
   
}

$conn->close();
?>
