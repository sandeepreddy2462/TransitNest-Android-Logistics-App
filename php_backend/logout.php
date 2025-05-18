<?php
session_start();
session_unset()
session_destroy();

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Credentials: true");

echo json_encode(["success" => true, "message" => "Logout successful"]);
?>
