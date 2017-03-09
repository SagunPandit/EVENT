<?php
   
    $connect = mysqli_connect("localhost:3306", "avashadh_android", "Nepali_Babu_Adhikari123", "avashadh_event");

    if (!$connect)
    {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }

    $usernameorid=$_POST["username"];
    $eventname=$_POST["eventname"];
    $check_code=$_POST["check"];						
    $response = array();
    $response["success"] = false;  

    function getid()
    {
        global $connect, $usernameorid;
        $statement= mysqli_prepare($connect,"SELECT user_id FROM user WHERE username=?");
        mysqli_stmt_bind_param($statement, "s", $usernameorid);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $id);

        while(mysqli_stmt_fetch($statement)){
            $inuser= $id;
        }
        return $inuser;

    }
    function geteventid()
    {
        global $connect, $eventname;
        $statement= mysqli_prepare($connect,"SELECT upload_id FROM upload WHERE event_name=?");
        mysqli_stmt_bind_param($statement, "s", $eventname);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $id);

        while(mysqli_stmt_fetch($statement)){
            $inuser= $id;
        }
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
        $viewcount=array();
        $i=0;
        $statement = mysqli_prepare($connect, "SELECT * FROM upload WHERE user_id = ? ORDER BY upload_id DESC"); 
        mysqli_stmt_bind_param($statement, "i", $id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$ecount,$time);
        
        while(mysqli_stmt_fetch($statement)){
        	$locationname[$i]=$location;
            	$reventname[$i]=$ename;
            	$eventdate[$i]=$dates;
            	$eventcategory[$i]=$category_name ;
            	$eventid[$i]=$uid;
            	$viewcount[$i]=$ecount;
            	$i++;
        }
        mysqli_stmt_close($statement); 
        
        $response["event_id"]=$eventid;
        $response["event_name"]=$reventname;
        $response["location_name"]=$locationname;
        $response["event_date"]=$eventdate;
        $response["event_category"]=$eventcategory;
        $response["viewcount"]=$viewcount;
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

    function attendingevents($attendinguserid)
    {
        global $connect, $response,$eventname,$usernameorid;
        $attendingeventid=geteventid();
        $statement = mysqli_prepare($connect, "INSERT INTO attendingevents (event_id, user_id) VALUES (?, ?)");
        mysqli_stmt_bind_param($statement, "ii",$attendingeventid,$attendinguserid);
        mysqli_stmt_execute($statement);

    }
    
    function getall() {
    	global $connect, $response;
        $reventname = array();
        $locationname = array();
        $eventdate = array();
        $eventcategory = array();
        $eventorganizer = array();
        $eventid= array();
        $viewcount= array();
        $i=0;
        $statement = mysqli_prepare($connect, "SELECT * FROM upload ORDER BY upload_id DESC"); 
        mysqli_stmt_bind_param($statement, "i", $id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$ecount,$time);
        
        while(mysqli_stmt_fetch($statement)){
        	$locationname[$i]=$location;
            	$reventname[$i]=$ename;
            	$eventdate[$i]=$dates;
            	$eventcategory[$i]=$category_name ;
            	$eventid[$i]=$uid;
            	$eventorganizer[$i]=getorganizer($userid);
            	$viewcount[$i]=$ecount;
            	$i++;
        }
        mysqli_stmt_close($statement); 
        
        $response["event_id"]=$eventid;
        $response["event_name"]=$reventname;
        $response["location_name"]=$locationname;
        $response["event_date"]=$eventdate;
        $response["event_category"]=$eventcategory;
        $response["event_organizer"]=$eventorganizer;
        $response["viewcount"]=$viewcount;
        $response["success"]=true;
          
    }
    
    function gettrend() {
    	global $connect, $response;
        $reventname = array();
        $locationname = array();
        $eventdate = array();
        $eventcategory = array();
        $eventorganizer = array();
        $eventid= array();
        $viewcount= array();
        $i=0;
        $statement = mysqli_prepare($connect, "SELECT * FROM upload ORDER BY count DESC"); 
        mysqli_stmt_bind_param($statement, "i", $id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$ecount,$time);
        
        while(mysqli_stmt_fetch($statement)){
        	$locationname[$i]=$location;
            	$reventname[$i]=$ename;
            	$eventdate[$i]=$dates;
            	$eventcategory[$i]=$category_name ;
            	$eventid[$i]=$uid;
            	$eventorganizer[$i]=getorganizer($userid);
            	$viewcount[$i]=$ecount;
            	$i++;
        }
        mysqli_stmt_close($statement); 
        
        $response["event_id"]=$eventid;
        $response["event_name"]=$reventname;
        $response["location_name"]=$locationname;
        $response["event_date"]=$eventdate;
        $response["event_category"]=$eventcategory;
        $response["event_organizer"]=$eventorganizer;
        $response["viewcount"]=$viewcount;
        $response["success"]=true;
          
    }
    
    
   function incrcount(){
   	global $connect, $response,$usernameorid;
   	$strcount=$_POST["viewcount"];
   	$count=(int)$strcount;
   	$statement = mysqli_prepare($connect, "UPDATE upload SET count= ? WHERE event_name= ?");
        mysqli_stmt_bind_param($statement, "is",$count,$usernameorid);
        mysqli_stmt_execute($statement);
   	$response["success"]=true;
   
   
   }
   
   function gettypes($types){
   	global $connect, $response;
        $reventname = array();
        $locationname = array();
        $eventdate = array();
        $eventcategory = array();
        $eventorganizer = array();
        $eventid= array();
        $viewcount= array();
        $i=0;
        $statement = mysqli_prepare($connect, "SELECT * FROM upload WHERE category_name= ? ORDER BY count DESC"); 
        mysqli_stmt_bind_param($statement, "s", $types);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$ecount,$time);
        
        while(mysqli_stmt_fetch($statement)){
        	$locationname[$i]=$location;
            	$reventname[$i]=$ename;
            	$eventdate[$i]=$dates;
            	$eventcategory[$i]=$category_name ;
            	$eventid[$i]=$uid;
            	$eventorganizer[$i]=getorganizer($userid);
            	$viewcount[$i]=$ecount;
            	$i++;
        }
        mysqli_stmt_close($statement); 
        
        $response["event_id"]=$eventid;
        $response["event_name"]=$reventname;
        $response["location_name"]=$locationname;
        $response["event_date"]=$eventdate;
        $response["event_category"]=$eventcategory;
        $response["event_organizer"]=$eventorganizer;
        $response["viewcount"]=$viewcount;
        $response["success"]=true;
   }
   
   switch($check_code){
   	case "own":
	getevent(getid());
	break;
    	
    	case "details":
        getdetails();
        break;
    	
    	case "trending":
        gettrend();
        break;
        
        case "incr":
        incrcount();
        break;
    	
    	case "conference":
        gettypes("Conference");
        break;
        
        case "parties":
        gettypes("Parties");
        break;
        
        case "sports":
        gettypes("Sports");
        break;
        
        case "donations":
        gettypes("Donations");
        break;

        case "attending":
        attendingevents(getid());
        break;
        
    	default:
        getall();
   }
   
   /*if($check_code=="own")
    	getevent(getid());
    elseif($check_code=="details")
    	getdetails();
    elseif($check_code=="trending")
    	gettrend();
    elseif($check_code=="incr")
    	incrcount();
    else
        getall();*/
  
    echo json_encode($response);
?>
