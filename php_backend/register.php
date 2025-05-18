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

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = $_POST["username"];
    $useremail = $_POST["useremail"];
    $userphonenumber = $_POST["userphonenumber"];
    $userpassword = $_POST["userpassword"];

    if (empty($username) || empty($useremail) || empty($userphonenumber) || empty($userpassword)) {
        echo json_encode(["success" => false, "error" => "Missing required fields"]);
        exit();
    }

    // Check if email already exists
    $checkQuery = "SELECT * FROM users WHERE useremail=?";
    $stmt = $conn->prepare($checkQuery);
    $stmt->bind_param("s", $useremail);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($result->num_rows > 0) {
        echo json_encode(["success" => false, "error" => "Email already registered"]);
        exit();
    }

    $sql = "INSERT INTO users (username, useremail, userphonenumber, userpassword) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssss", $username, $useremail, $userphonenumber, $userpassword);

    if ($stmt->execute()) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false, "error" => $conn->error]);
    }

    $stmt->close();
}

$conn->close();
?>
