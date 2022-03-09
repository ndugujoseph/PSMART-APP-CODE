s<?php

$name = $_POST["name"];
$image = $_POST["image"];
$id = $_POST["id"];
$role = $_POST["role"];


$decodedImage = base64_decode("$image");

if($role == "Teacher"){
    
    $return = file_put_contents("Teachers/" . $id . ".jpg", $decodedImage);
}
else{
 $return = file_put_contents("Learners/" . $id . ".jpg", $decodedImage);
}


$response = array();
if ($return !== false) {
    $response['success'] = 1;
    $response['message'] = "Your image has ploaded successfully with Retrofit";
} else {
    $response['success'] = 0;
    $response['message'] = "Image failed to pload";
}

echo json_encode($response);
