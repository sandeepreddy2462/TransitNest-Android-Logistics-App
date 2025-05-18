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

// Check if the request method is POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get session email from POST request
    $user_email = isset($_POST['adminemail']) ? $_POST['adminemail'] : '';

    if (empty($user_email)) {
        echo json_encode(["success" => false, "error" => "Session email not provided"]);
        exit;
    }

    // Prepare SQL query
    $sql = "SELECT content, item, sender_address, receiver_address, weight_kgs, receiver_mobile, receiver_name 
            FROM senders_details 
            WHERE useremail = ?";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $user_email);
    $stmt->execute();
    $result = $stmt->get_result();

    $parcels = [];

    while ($row = $result->fetch_assoc()) {
        $parcels[] = $row;
    }
    echo json_encode($parcels);
    $stmt->close();
    $conn->close();
}
?>
