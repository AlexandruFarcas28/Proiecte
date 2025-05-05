<?php

session_start();

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "baza_mare";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if (isset($_POST["submit"])) {
    $utilizator = $_POST['nume_utiz'];
    $parola = $_POST['parola'];

    function existaUtilizator($conn, $utilizator) {
        $sql = "SELECT * FROM creare_cont WHERE nume_utiz = ?;";
        $stmt = $conn->prepare($sql);

        if (!$stmt) {
            die("Prepare failed: " . $conn->error);
        }

        if (!$stmt->bind_param("s", $utilizator)) {
            die("Binding parameters failed: " . $stmt->error);
        }

        if (!$stmt->execute()) {
            die("Execute failed: " . $stmt->error);
        }

        $resultData = $stmt->get_result();

        if ($row = $resultData->fetch_assoc()) {
            return $row;
        } else {
            return false;
        }

        $stmt->close();
    }

    function verificaParola($parola_introdusa, $parola_stocata) {
        return $parola_introdusa === $parola_stocata;
    }

    function autentificaUtilizator($conn, $utilizator, $parola) {
        $info = existaUtilizator($conn, $utilizator);
        if ($info === false) {
            header("Location: ../Autentificare/index.html?error=nu_exista");
            exit();
        }
        
        $parola_stocata = $info["parola"];
        
        
        $parola_corecta = verificaParola($parola, $parola_stocata);

        if ($parola_corecta) {
            echo "Password is correct.<br>";
        } else {
            echo "Password is incorrect.<br>";
        }

        if ($parola_corecta === false) {
            header("Location: ../Autentificare/index.html?error=parola_gresita");
            exit();
        } else {
            $_SESSION["ID"] = $info["id_user"];
			    echo "<script>
            alert('Autentificare resuita!');
            window.location.href = '../Evenimente/index.html';
          </script>";
            exit();
        }
    }

    autentificaUtilizator($conn, $utilizator, $parola);
}

$conn->close();
?>
