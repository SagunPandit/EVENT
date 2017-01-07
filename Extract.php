<?php
   
    $connect = mysqli_connect("localhost:3306", "avashadh_android", "Nepali_Babu_Adhikari123", "avashadh_event");

    if (!$connect)
    {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }

    $usernameorid=$_POST["username"];
    $check_code=$_POST["check"];						
    $response = array();
    $response["success"] = false;  

    function getid()
    {
        global $connect, $usernameorid, $userid;
        $statement= mysqli_prepare($connect,"SELECT user_id FROM user WHERE username=?");
        mysqli_stmt_bind_param($statement, "s", $usernameorid);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $id);

        while(mysqli_stmt_fetch($statement)){
            $inuser= $id;
        }
        $userid= $inuser;
        return $inuser;

    }
    
    function getdetails()
    {
        global $connect, $usernameorid, $response;
        $upload_id=(int)$usernameorid;
        $statement= mysqli_prepare($connect,"SELECT event_details FROM upload WHERE upload_id=?");
        mysqli_stmt_bind_param($statement, "i", $upload_id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement, $event_details);

        while(mysqli_stmt_fetch($statement)){
            $details= $event_details;
        }
        $response["event_details"]=$details;
        $response["success"]=true;


    }

    function getevent($id) {
    	global $connect, $response;
        $reventname = array();
        $locationname = array();
        $eventdate = array();
        $eventcategory = array();
        $eventid= array();
        $i=0;
        $statement = mysqli_prepare($connect, "SELECT *FROM upload WHERE user_id = ?"); 
        mysqli_stmt_bind_param($statement, "i", $id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$time);
        
        while(mysqli_stmt_fetch($statement)){
        	$locationname[$i]=$location;
            	$reventname[$i]=$ename;
            	$eventdate[$i]=$dates;
            	$eventcategory[$i]=$category_name ;
            	$eventid[$i]=$uid;
            	$i++;
        }
        mysqli_stmt_close($statement); 
        
        $response["event_id"]=$eventid;
        $response["event_name"]=$reventname;
        $response["location_name"]=$locationname;
        $response["event_date"]=$eventdate;
        $response["event_category"]=$eventcategory;
        $response["success"]=true;
          
    }
    
    function getorganizer($user_id){
    	global $connect;
    	$statement = mysqli_prepare($connect, "SELECT username FROM user WHERE user_id=? "); 
        mysqli_stmt_bind_param($statement, "i", $user_id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$tusername);
        
        while(mysqli_stmt_fetch($statement)){
        	$user_name=$tusername;
        }
        mysqli_stmt_close($statement); 
        return $user_name;
    }
    
    function getall() {
    	global $connect, $response;
        $reventname = array();
        $locationname = array();
        $eventdate = array();
        $eventcategory = array();
        $eventorganizer = array();
        $eventid= array();
        $i=0;
        $statement = mysqli_prepare($connect, "SELECT * FROM upload "); 
        mysqli_stmt_bind_param($statement, "i", $id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$time);
        
        while(mysqli_stmt_fetch($statement)){
        	$locationname[$i]=$location;
            	$reventname[$i]=$ename;
            	$eventdate[$i]=$dates;
            	$eventcategory[$i]=$category_name ;
            	$eventid[$i]=$uid;
            	$eventorganizer[$i]=getorganizer($userid);
            	$i++;
        }
        mysqli_stmt_close($statement); 
        
        $response["event_id"]=$eventid;
        $response["event_name"]=$reventname;
        $response["location_name"]=$locationname;
        $response["event_date"]=$eventdate;
        $response["event_category"]=$eventcategory;
        $response["event_organizer"]=$eventorganizer;
        $response["success"]=true;
          
    }
    
    if($check_code=="own")
    	getevent(getid());
    elseif($check_code=="details")
    	getdetails();
    else
    	getall();
  
    echo json_encode($response);
?>
