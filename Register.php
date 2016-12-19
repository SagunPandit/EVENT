<?php
    require("password.php");
    $connect = mysqli_connect("localhost:3306", "avashadh_android", "Nepali_Babu_Adhikari123", "avashadh_event");

    if (!$connect)
    {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }

    $username = $_POST["username"];
    $email = $_POST["email"];
    $password = $_POST["password"];							
    $response = array();
    $response["success"] = 0;  

    function registerUser() {
        global $connect, $username, $email, $password;
        $passwordHash = password_hash($password, PASSWORD_DEFAULT);
        $statement = mysqli_prepare($connect, "INSERT INTO user (username, email, password) VALUES (?, ?, ?)");
        mysqli_stmt_bind_param($statement, "sss", $username, $email, $passwordHash);
        mysqli_stmt_execute($statement);
          
    }
	
    function usernameAvailable() {
        global $connect, $username;
        $statement = mysqli_prepare($connect, "SELECT * FROM user WHERE username = ?"); 
        mysqli_stmt_bind_param($statement, "s", $username);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement); 
        if ($count < 1){
            return true; 
        }else {
            return false; 
        }
    }
    
    function emailAvailable() {
        global $connect, $email;
        $statement = mysqli_prepare($connect, "SELECT * FROM user WHERE email = ?"); 
        mysqli_stmt_bind_param($statement, "s", $email);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement); 
        if ($count < 1){
            return true; 
        }else {
            return false; 
        }
    }						

    if (usernameAvailable())
    {      
        if (filter_var($email, FILTER_VALIDATE_EMAIL)){
            if(emailAvailable()){
				registerUser();
				$response["success"]=1;
			}
			else
				$response["success"]=2;            
        }
	}
	else
		$response["success"]=3; 
		
    
    echo json_encode($response);
?>