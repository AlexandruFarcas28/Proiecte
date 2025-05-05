<?php
session_start();
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "baza_mare";



$conn = new mysqli($servername, $username, $password, $dbname);


if ($conn->connect_error) {
    die("Conexiunea a esuat: " . $conn->connect_error);
}


$nume = $_POST['nume'];
$prenume = $_POST['prenume'];
$nume_utilizator = $_POST['nume_utiz'];
$email = $_POST['email'];
$adresa = $_POST['adresa'];
$data_nasterii = $_POST['data_nasterii'];
$parola = $_POST['parola'];
$confirm_password = $_POST['confirm_password'];
$cnp = isset($_POST['cnp']) ? $_POST['cnp'] : null;
$nr_leg =  isset($_POST['leg']) ? $_POST['leg'] : null;

if ($parola !== $confirm_password) {
    die("Parola și confirmarea parolei nu se potrivesc.");
}

$sql = "INSERT INTO creare_cont (nume, prenume, nume_utiz, email, adresa, data_nasterii, cnp, leg, parola, login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";


$stmt = $conn->prepare($sql);
$stmt->bind_param("sssssssss", $nume, $prenume, $nume_utilizator, $email, $adresa, $data_nasterii, $cnp, $nr_leg, $parola);


if ($stmt->execute()) {

    echo "
    <script>
        alert('Înregistrarea a fost realizată cu succes!');
        setTimeout(function() {
            window.location.href = '../Main/home.html';
        }, 1000);
    </script>";
} else {
    echo "Eroare: " . $stmt->error;
}


$stmt->close();
$conn->close();
?>
