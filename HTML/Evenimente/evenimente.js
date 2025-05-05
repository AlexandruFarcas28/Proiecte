
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.buy-button').forEach(button => {
        button.addEventListener('click', function() {
            if (isLoggedIn) {
                openTicketMenu();
            } else {
                alert('Trebuie să fii logat pentru a cumpăra bilete.');
            }
        });
    });
});

function openTicketMenu(eventType) {
    document.getElementById('overlay').style.display = 'block';
    document.getElementById('ticketMenu').style.display = 'block';
    document.getElementById('eventName').value = eventType;
}

function closeTicketMenu() {
    document.getElementById('overlay').style.display = 'none';
    document.getElementById('ticketMenu').style.display = 'none';
}

function togglePaymentMethod() {
    var paymentMethod = document.getElementById('paymentMethod').value;
    var cardPaymentFields = document.getElementById('cardPaymentFields');
    if (paymentMethod === 'card') {
        cardPaymentFields.style.display = 'block';
    } else {
        cardPaymentFields.style.display = 'none';
    }
}

function buyTickets(event) {
    event.preventDefault();
    var ticketCount = parseInt(document.getElementById('ticketCount').value);
    var paymentMethod = document.getElementById('paymentMethod').value;
    var cardNumber = document.getElementById('cardNumber').value;
    
    var eventName = document.getElementById('eventName').value;
    var message = 'Ai cumpărat ' + ticketCount + ' bilete.';
    
    if (eventName === 'Cargo' && ticketCount == 2) {
        ticketCount += Math.floor(ticketCount / 2);
        message = 'Ai cumpărat 2 bilete și ai primit 1 bilet gratuit. Total bilete: ' + ticketCount;
    } else if (eventName === 'Motans' && ticketCount == 1) {
        ticketCount += 1;
        message = 'Ai cumpărat 1 bilet și ai primit 1 bilet gratuit. Total bilete: ' + ticketCount;
    } else if (ticketCount == 4) {
        ticketCount += 1;
        message = 'Ai cumpărat ' + (ticketCount - 1) + ' bilete și ai primit 1 bilet gratuit. Total bilete: ' + ticketCount;
    }

    if (paymentMethod === 'card') {
        if (cardNumber.match(/^\d{16}$/)) {
            alert(message + ' Plata cu cardul a fost efectuată cu succes.');
        } else {
            alert('Numărul cardului este invalid. Te rugăm să introduci un număr valid de 16 cifre.');
            return;
        }
    } else {
        alert(message + ' Poți ridica biletele la eveniment.');
    }

    closeTicketMenu();
}

document.querySelectorAll('.buy-button').forEach(button => {
    button.addEventListener('click', function() {
        document.querySelectorAll('.event').forEach(event => event.classList.remove('selected'));
        button.closest('.event').classList.add('selected');
        openTicketMenu(button.closest('.event').getAttribute('data-event'));
    });
});

function goHome() {
    window.location.href = '/';
}
