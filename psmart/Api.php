<?php 
 
 require_once 'DbConnect.php';
 
 $response = array();
 
 if(isset($_GET['apicall'])){
 
 switch($_GET['apicall']){
 
 case 'signup':
     
 if(isTheseParametersAvailable(array('username','email','password','gender'))){
 $username = $_POST['username']; 
 $email = $_POST['email']; 
 $password = md5($_POST['password']);
 $gender = $_POST['gender']; 
 
 $stmt = $conn->prepare("SELECT id FROM users WHERE full_name = ? OR email = ?");
 $stmt->bind_param("ss", $username, $email);
 $stmt->execute();
 $stmt->store_result();
 
 if($stmt->num_rows > 0){
 $response['error'] = true;
 $response['message'] = 'User already registered';
 $stmt->close();
 }else{
 $stmt = $conn->prepare("INSERT INTO users (full_name, email, password, gender) VALUES (?, ?, ?, ?)");
 $stmt->bind_param("ssss", $username, $email, $password, $gender);
 
 if($stmt->execute()){
 $stmt = $conn->prepare("SELECT id, id, full_name, email, gender FROM users WHERE full_name = ?"); 
 $stmt->bind_param("s",$username);
 $stmt->execute();
 $stmt->bind_result($userid, $id, $username, $email, $gender);
 $stmt->fetch();
 
 $user = array(
 'id'=>$id, 
 'username'=>$username, 
 'email'=>$email,
 'gender'=>$gender
 );
 
 $stmt->close();
 
 $response['error'] = false; 
 $response['message'] = 'User registered successfully'; 
 $response['user'] = $user; 
 }
 }
 
 }else{
 $response['error'] = true; 
 $response['message'] = 'required parameters are not available'; 
 }
 
 break; 
 
 case 'login':
 
 if(isTheseParametersAvailable(array('email', 'password'))){
 
 $email = $_POST['email'];
 $password = md5($_POST['password']); 
 
 $stmt = $conn->prepare("SELECT * FROM users WHERE email = ? AND password = ?");
 $stmt->bind_param("ss",$email, $password);
 
 $stmt->execute();
 
 $stmt->store_result();
 
 if($stmt->num_rows > 0){
 
 $stmt->bind_result($id, $email, $gender);
 $stmt->fetch();
 
 $user = array(
 'id'=>$id, 
//  'username'=>$username, 
 'email'=>$email,
 'gender'=>$gender
 );
 
 $response['error'] = false; 
 $response['message'] = 'Login successfull '.$email; 
 $response['user'] = $user; 
 }else{
 $response['error'] = false; 
 $response['message'] = 'Invalid email or password';
 }
 }
 break; 
 
 default: 
 $response['error'] = true; 
 $response['message'] = 'Invalid Operation Called';
 }
 
 }else{
 $response['error'] = true; 
 $response['message'] = 'Invalid API Call';
 }
 
 echo json_encode($response);
 
 function isTheseParametersAvailable($params){
 
 foreach($params as $param){
 if(!isset($_POST[$param])){
 return false; 
 }
 }
 return true; 
 }