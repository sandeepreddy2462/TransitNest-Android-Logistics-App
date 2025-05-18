<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Credentials: true");

// Database credentials
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

// Allow only POST requests
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // $sessionemail = isset($_POST["sessionemail"]) ? $_POST["sessionemail"] : "";
    // $sessionemail = "sand@gmail.com";
    $sessionemail = $_POST['sessionemail'];


    // Check if sessionemail is provided
    if (empty($sessionemail)) {
        echo json_encode(["success" => false, "message" => "sessionemail is required"]);
        exit();
    }

    // Prepare statement
    $stmt = $conn->prepare("SELECT username, userphonenumber, useremail FROM users WHERE useremail = ?");
    if (!$stmt) {
        echo json_encode(["success" => false, "message" => "Query preparation failed: " . $conn->error]);
        exit();
    }

    $stmt->bind_param("s", $sessionemail);
    $stmt->execute();
    $result = $stmt->get_result();

    // Check if user exists
    // if ($stmt->num_rows > 0) {
    //     // Bind results
    //     $stmt->bind_result($username, $userphonenumber, $useremail);
    //     $stmt->fetch();

    //     // Prepare the response
    //     echo json_encode(["success" => true,"username" => $username, "userphonenumber" => $userphonenumber, "useremail" => $useremail]);
    // } else {
    //     echo json_encode(["success" => false, "message" => "User not found"]);
    // }

    if ($row = $result->fetch_assoc()) {
        echo json_encode(["success" => true, "username" => $row["username"], "userphonenumber" => $row["userphonenumber"], "useremail" => $row["useremail"]]);
    } else {
        echo json_encode(["success" => false, "message" => "User not found"]);
    }

    $stmt->close();
    $conn->close();
}

?>
