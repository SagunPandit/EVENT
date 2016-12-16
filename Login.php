<?php
    require("password.php");
    $con = mysqli_connect("us-imm-sql1.000webhost.io", "id329812_nevent", "semesterpro", "id329812_nevent");
    if (!$con) {
        exit();
    }
    
    $username = $_POST["username"];
    $email = $_POST["email"];
    $password = $_POST["password"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM User WHERE username = ?
        AND email = ?");
    mysqli_stmt_bind_param($statement, "ss", $username,$email);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $colUserID, $colUsername, $colEmail, $colPassword);
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement)){
        if (password_verify($password, $colPassword)) {
            $response["success"] = true;  
            $response["username"] = $colUsername;
            $response["email"] = $colEmail;
        }
    }
    echo json_encode($response);
?>