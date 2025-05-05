function showStudentInfo() {
    var studentCheckbox = document.getElementById("student");
    var studentInfo = document.getElementById("student-info");
    studentInfo.style.display = studentCheckbox.checked ? "block" : "none";
}

function validateForm() {
    var firstName = document.getElementById("nume").value;
    var lastName = document.getElementById("prenume").value;
    var username = document.getElementById("nume_utilizator").value;
    var email = document.getElementById("email").value;
    var address = document.getElementById("adresa").value;
    var birthdate = document.getElementById("data_nasterii").value;
    var password = document.getElementById("parola").value;
    var confirmPassword = document.getElementById("confirm_password").value;
    var isStudent = document.getElementById("student").checked;
    var cnp = document.getElementById("cnp").value;
    var legitimatie = document.getElementById("leg").value;
    var errorMsg = '';

    if (firstName === '') {
        errorMsg += 'Nume este necesar.\n';
    }

    if (lastName === '') {
        errorMsg += 'Prenume este necesar.\n';
    }

    if (username === '') {
        errorMsg += 'Nume utilizator este necesar.\n';
    }

    if (email === '') {
        errorMsg += 'Adresă de email este necesară.\n';
    }

    if (address === '') {
        errorMsg += 'Adresă este necesară.\n';
    }

    if (birthdate === '') {
        errorMsg += 'Data nașterii este necesară.\n';
    }

    if (isStudent) {
        if (cnp === '') {
            errorMsg += 'CNP este necesar pentru studenți.\n';
        }

        if (legitimatie === '') {
            errorMsg += 'Număr legitimatie este necesar pentru studenți.\n';
        }
    }

    if (password === '') {
        errorMsg += 'Parolă este necesară.\n';
    }

    if (password !== confirmPassword) {
        errorMsg += 'Parola și confirmarea parolei nu se potrivesc.\n';
    }
	
	console.log("legitimatie:",legitimatie);
	
    if (errorMsg) {
        alert(errorMsg);
        return false;
    } else {
        return true;
    }
	
}