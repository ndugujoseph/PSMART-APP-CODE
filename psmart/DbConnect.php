<?

$servername = "localhost";
$username = "alliancelanguage_ps";
$password = "solfix@africa";
$database = "alliancelanguage_ps";
 
 
$conn = new mysqli($servername, $username, $password, $database);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
 