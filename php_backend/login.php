<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Credentials: true"); // Allow session cookies

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "user_database";

// Connect to database
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed: " . $conn->connect_error]);
    exit();
}

// Check if request is POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get input data
    $useremail = $_POST["useremail"];
    $checkpassword = $_POST["checkpassword"];
    // Validate input
    if (empty($useremail) || empty($checkpassword)) {
        echo json_encode(["success" => false, "message" => "Email and password are required"]);
        exit();
    }

    // Prepare and execute query
    $stmt = $conn->prepare("SELECT userpassword FROM users WHERE useremail = ?");
    $stmt->bind_param("s", $useremail);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows > 0) {
        $stmt->bind_result($originalpassword);
        $stmt->fetch();
        
        // Verify password using password_verify() if hashed
        if ($checkpassword === $originalpassword) {
            echo json_encode([
                "success" => true,
                "message" => "Login successful",
            ]);
        } else {
            echo json_encode(["success" => false, "message" => "Invalid password"]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "User not found"]);
    }

    $stmt->close();
}

$conn->close();
?>
