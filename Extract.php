<?php
   
    $connect = mysqli_connect("localhost:3306", "avashadh_android", "Nepali_Babu_Adhikari123", "avashadh_event");

    if (!$connect)
    {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }

    $usernameorid=$_POST["username"];
    $eventid=(int) $_POST["eventId"];
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
        $statement= mysqli_prepare($connect,"SELECT event_details,latitude,longitude FROM upload WHERE upload_id=?");
        mysqli_stmt_bind_param($statement, "i", $upload_id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement, $event_details,$latitude,$longitude);

        while(mysqli_stmt_fetch($statement)){
            $details= $event_details;
            $lat=$latitude;
            $long=$longitude;
        }
        mysqli_stmt_close($statement); 
        
        $response["event_details"]=$details;
        $response["event_lat"]=$lat;
        $response["event_long"]=$long;
        
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
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$latitude,$longitude,$ecount,$time);
        
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
        
        $response["event_id1"]=$eventid;
        $response["event_name1"]=$reventname;
        $response["location_name1"]=$locationname;
        $response["event_date1"]=$eventdate;
        $response["event_category1"]=$eventcategory;
        $response["event_organizer1"]=$eventorganizer;
        $response["viewcount1"]=$viewcount;
        $response["count1"]=$count;
        $response["success1"]=true;
          
    }
    
    function getgoingevents($id){
    	global $connect, $response;
        $reventname1 = array();
        $locationname1 = array();
        $eventdate1 = array();
        $eventcategory1 = array();
        $eventid1= array();
        $viewcount1=array();
	$arraychecking1=array();
        $statement = mysqli_prepare($connect, "SELECT upload_id FROM attendingevents WHERE user_id = ?"); 
        mysqli_stmt_bind_param($statement, "i", $id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $dcount = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$selectedid);
        
        while(mysqli_stmt_fetch($statement)){
            	$arrayeventid[$i]=$selectedid;
            	$i++;
        }
        mysqli_stmt_close($statement); 
        $i=0;
        
        foreach($arrayeventid as $singleventid){
        	
	        $statement2 = mysqli_prepare($connect, "SELECT * FROM upload WHERE upload_id = ?"); 
	        mysqli_stmt_bind_param($statement2, "i", $singleventid);
	        mysqli_stmt_execute($statement2);
	        mysqli_stmt_store_result($statement2);
	        $scount = mysqli_stmt_num_rows($statement2);
	        mysqli_stmt_bind_result($statement2,$uid1,$ename1,$location1,$dates1,$category_name1,$userid1,$details1,$latitude1,$longitude1,$ecount1,$time1);
	        
		while(mysqli_stmt_fetch($statement2)){
		        $locationname1[$i]=$location1;
	            	$reventname1[$i]=$ename1;
	            	$eventdate1[$i]=$dates1;
	            	$eventcategory1[$i]=$category_name1 ;
	            	$eventid1[$i]=$uid1;
	            	$eventorganizer1[$i]=getorganizer($id);
	            	$viewcount1[$i]=$ecount1;
	            	$i++;
	        }
	        mysqli_stmt_close($statement2); 
        }
        
        $response["event_id1"]=$eventid1;
        $response["event_name1"]=$reventname1;
        $response["location_name1"]=$locationname1;
        $response["event_date1"]=$eventdate1;
        $response["event_category1"]=$eventcategory1;
        $response["event_organizer1"]=$eventorganizer1;
        $response["viewcount1"]=$viewcount1;
        $response["count1"]=$dcount;
        $response["success1"]=true;
    
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
        global $connect, $response,$eventid,$usernameorid;
        $statement = mysqli_prepare($connect, "INSERT INTO attendingevents (user_id, upload_id) VALUES (?, ?)");
        mysqli_stmt_bind_param($statement, "ii",$attendinguserid,$eventid);
        mysqli_stmt_execute($statement);
        
        $statement = mysqli_prepare($connect, "SELECT * FROM attendingevents WHERE upload_id = ? "); 
        mysqli_stmt_bind_param($statement, "i", $eventid);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement); 
        $response["participants"]=$count;
        $response["success"]=true;

    }
	
    function getparticipants($userid){
    	global $connect, $response,$eventid,$usernameorid;
    	$going=false;
    	$statement = mysqli_prepare($connect, "SELECT * FROM attendingevents WHERE upload_id = ? "); 
        mysqli_stmt_bind_param($statement, "i", $eventid);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        
        $statement = mysqli_prepare($connect, "SELECT * FROM attendingevents WHERE upload_id = ? AND user_id=?"); 
        mysqli_stmt_bind_param($statement, "ii", $eventid, $userid);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $checkgoing = mysqli_stmt_num_rows($statement);
        mysqli_stmt_close($statement); 
        
        if($checkgoing>0)
          {$going=true;}
        else
          {$going=false;}
        
        $response["going"]=$going;
        $response["participants"]=$count;
        $response["success"]=true;
        
    
    
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
        $eventlatitude= array();
        $eventlongitude= array();
        $i=0;
        $statement = mysqli_prepare($connect, "SELECT * FROM upload ORDER BY upload_id DESC"); 
        mysqli_stmt_bind_param($statement, "i", $id);
        mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        $count = mysqli_stmt_num_rows($statement);
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$latitude,$longitude,$ecount,$time);
        
        while(mysqli_stmt_fetch($statement)){
        	$locationname[$i]=$location;
            	$reventname[$i]=$ename;
            	$eventdate[$i]=$dates;
            	$eventcategory[$i]=$category_name ;
            	$eventid[$i]=$uid;
            	$eventorganizer[$i]=getorganizer($userid);
            	$viewcount[$i]=$ecount;
            	$eventlatitude[$i]=$latitude;
            	$eventlongitude[$i]=$longitude;
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
        $response["latitude"]=$eventlatitude;
        $response["longitude"]=$eventlongitude;
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
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$latitude,$longitude,$ecount,$time);
        
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
        mysqli_stmt_bind_result($statement,$uid,$ename,$location,$dates,$category_name,$userid,$details,$latitude,$longitude,$ecount,$time);
        
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
        
        case "gaming":
        gettypes("Gaming");
        break;
        
        case "others":
        gettypes("Others");
        break;
        
        case "exhibition":
        gettypes("Exhibition");
        break;
        
        case "business":
        gettypes("Business");
        break;
        
        case "educational":
        gettypes("Educational");
        break;
        
        case "concert":
        gettypes("Concert");
        break;
 
        case "attending":
        attendingevents(getid());
        break;

        case "participant":
        getparticipants(getid());
        
        case "getgoing":
        getgoingevents(getid());;
        
    	default:
        getall(getid());
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