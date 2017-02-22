<?php
   
    $connect = mysqli_connect("localhost:3306", "avashadh_android", "Nepali_Babu_Adhikari123", "avashadh_event");

    if (!$connect)
    {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }

    $username=$_POST["username"];
    $eventname=$_POST["eventname"];
    $eventcategory=$_POST["eventcategory"];
    $eventdate=$_POST["eventdate"];
    $eventlocation=$_POST["eventlocation"];
    						
    $response = array();
    $response["success"] = false;  

    function getid()
    {
        global $connect, $username;
        $statement= mysqli_prepare($connect,"SELECT user_id FROM user WHERE username=?");
        mysqli_stmt_bind_param($statement, "s", $username);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $id);

        while(mysqli_stmt_fetch($statement)){
            $inuser= $id;
        }
        return $inuser;

    }

    function deleteevent($id) {
    	global $connect, $response,$eventname,$eventcategory,$eventdate,$eventlocation;
        $reventname = array();
        $i=0;
        $statement = mysqli_prepare($connect, "DELETE FROM upload WHERE user_id = ? AND event_name=? AND category_name=? AND location=? AND dates=?"); 
        mysqli_stmt_bind_param($statement, "issss", $id, $eventname,$eventcategory,$eventlocation,$eventdate);
        $response["success"]=mysqli_stmt_execute($statement);
        mysqli_stmt_close($statement); 
          
    }
   
    deleteevent(getid());

    

		
    
    echo json_encode($response);
?>