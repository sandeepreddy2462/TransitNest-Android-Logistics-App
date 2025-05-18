<?php
// session_start();
header("Access-Control-Allow-Credentials: true");
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

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $sender_email = isset($_POST["sender_email"]) ? $_POST["sender_email"] : "";
    $content = $_POST["content"];
    $item = $_POST["item"];
    $sender_address = $_POST["sender_address"];
    $receiver_address = $_POST["receiver_address"];
    $weight_kgs = $_POST["weight_kgs"];
    $weight_gms = $_POST["weight_gms"];
    $instructions = $_POST["instruction_to_bringers"];
    $receiver_mobile = $_POST["receiver_mobile"];
    $receiver_email = $_POST["receiver_email"];
    $receiver_name = $_POST["receiver_name"];

    if (empty($content) || empty($item) || empty($sender_address) || empty($receiver_address)) {
        echo json_encode(["success" => false, "message" => "Please fill in all required fields"]);
        exit();
    }

    $sql = "INSERT INTO senders_details (useremail, content, item, sender_address, receiver_address, weight_kgs, weight_gms, instruction_to_bringers, receiver_mobile, receiver_email, receiver_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sssssssssss", $sender_email, $content, $item, $sender_address, $receiver_address, $weight_kgs, $weight_gms, $instructions, $receiver_mobile, $receiver_email, $receiver_name);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Parcel stored successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "Failed to store parcel"]);
    }

    $stmt->close();
}

$conn->close();
?>
