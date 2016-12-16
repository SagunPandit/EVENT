<?php
    require("password.php");
    $con = mysqli_connect("us-imm-sql1.000webhost.io", "id329812_nevent", "semesterpro", "id329812_nevent");
    if (!$con) {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
    $username = $_POST["username"];
    $email = $_POST["email"];
    $password = $_POST["password"];

    
    
    function regiseterUser(){
        global $username, $email, $password;
        $hashedpassword=password_hash($password, DEFAULT_PASSWORD);
        $statement = mysqli_prepare($con, "INSERT INTO User (username, email, password) VALUES (?, ?, ?)");
        mysqli_stmt_bind_param($statement, "sss", $username, $email, $hashedpassword);
        mysqli_stmt_execute($statement);
    }
    
      
    function usernameAvailable(){
        global $con, $username;
        $statement = mysqli_prepare($con, "SELECT * FROM User WHERE username=?");
        mysqli_stmt_bind_param($statement, "s", $username);
        mysqli_stmt_execute($statement);
        mysqli_store_result($statement);
        $count= mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement);

        if($count ==1)
            return true;
        else 
            return false;
    
    }

    function emailAvailable(){
        global $con, $username;
        $statement = mysqli_prepare($con, "SELECT * FROM User WHERE email=?");
        mysqli_stmt_bind_param($statement, "s", $email);
        mysqli_stmt_execute($statement);
        mysqli_store_result($statement);
        $count= mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement);

        if($count ==1)
            return true;
        else 
            return false;
    
    }


    $response = array();
    $response["success"]=0;
    
    if(usernameAvailable()){
            if(emailAvailable()){
                regiseterUser();
                $response["success"] = 1;
            }
            else
                $response["success"] = 2;

    }
    else
        $response["success"] = 3;
    
    echo json_encode($response);
?>