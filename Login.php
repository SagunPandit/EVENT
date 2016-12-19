<?php
    require("password.php");
    $con = mysqli_connect("localhost:3306", "avashadh_android", "Nepali_Babu_Adhikari123", "avashadh_event");
    if (mysqli_connect_errno())
    {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
    
    $username = $_POST["username"];
    $password = $_POST["password"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ?");
    mysqli_stmt_bind_param($statement, "s", $username);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $username, $email, $colPassword);
    
    $response = array();
    $response["success"] = false;  

    while(mysqli_stmt_fetch($statement)){
        $response["inside"]=$colPassword;
        if (password_verify($password, $colPassword)) {     
            $response["success"] = true; 
            $response["email"] = $email; 
            $response["username"] = $username;
        }
    }
    echo json_encode($response);
?>