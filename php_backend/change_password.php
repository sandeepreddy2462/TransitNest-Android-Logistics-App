<?php
header('Content-Type: application/json');

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "user_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    echo json_encode(["success" => false, "error" => "Database connection failed: " . $conn->connect_error]);
    exit();
}

// Check if request method is POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = isset($_POST["sessionemail"]) ? $_POST["sessionemail"] : "";
    $password = isset($_POST["newpassword"]) ? $_POST["newpassword"] : "";

    // Validate input
    if (empty($email) || empty($password)) {
        echo json_encode(["success" => false, "message" => "Email and password fields cannot be empty"]);
        exit();
    }

    // Encrypt password
    // Update password in database
    $sql = "UPDATE users SET userpassword = ? WHERE useremail = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $password, $email);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Password updated successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "Failed to update password"]);
    }

    $stmt->close();
    $conn->close();
} else {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
}
?>
