<?php
   
    $connect = mysqli_connect("localhost:3306", "avashadh_android", "Nepali_Babu_Adhikari123", "avashadh_event");

    if (!$connect)
    {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }

    $event_name = $_POST["event_name"];
    $location = $_POST["location"];
    $date = $_POST["date"];	
    $category_name=$_POST["category_name"];
    $username=$_POST["username"];	
    $details=$_POST["details"];			
    $response = array();
    $response["success"] = 0;  

    function getid()
    {
        global $connect, $username;
        $statement= mysqli_prepare($connect,"SELECT user_id FROM user WHERE username=?");
        mysqli_stmt_bind_param($statement, "s", $username);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $user_id);

        while(mysqli_stmt_fetch($statement)){
            $inuser= $user_id;
        }
        return $inuser;

    }

    function upload($user_id) {
        global $connect, $event_name, $location, $date, $category_name,$details;
        $statement = mysqli_prepare($connect, "INSERT INTO upload (event_name, location, dates, category_name, user_id, event_details) VALUES (?, ?, ?, ?, ?,?)");
        mysqli_stmt_bind_param($statement, "ssssis", $event_name, $location, $date, $category_name, $user_id,$details);
        mysqli_stmt_execute($statement);
          
    }
	
    function event_nameAvailable() {
        global $connect, $event_name;
        $statement = mysqli_prepare($connect, "SELECT * FROM upload WHERE event_name = ?"); 
        mysqli_stmt_bind_param($statement, "s", $event_name);
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
    
    						

    if (event_nameAvailable())
    {      
        $getuserid=getid();
        upload($getuserid);
	$response["success"]=1;          
        
	}
	else
		$response["success"]=0; 
		
    
    echo json_encode($response);
?>


