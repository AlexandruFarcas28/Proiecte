document.addEventListener("DOMContentLoaded", function() {
    function appearText() {
        var text = document.getElementById("marg");
        text.style.opacity = 1; 
    }

    function disappearText() {
        var text = document.getElementById("marg");
        text.style.opacity = 0; 
    }

    var n = 0;
    var intervalId = setInterval(function() {
        if (n == 0) {
            appearText(); 
            n = 1;
        } else {
            disappearText();
            n = 0;
        }
    }, 1200);

    var button = document.getElementById("rezervareButton");

    button.addEventListener("click", function() {
        if (button.style.textDecoration === "underline") {
            button.style.textDecoration = "none"; 
        } else {
            button.style.textDecoration = "underline";
        }
    });

    var images = ["next1.jpg", "next2.jpg", "next3.jpg"];
    var currentIndex = 0;
    var imageContainer = document.getElementById("sir_imagini");

    function showImage(index) {
        imageContainer.src = images[index];
    }

    function prevImage() {
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        showImage(currentIndex);
    }

    function nextImage() {
        currentIndex = (currentIndex + 1) % images.length;
        showImage(currentIndex);
    }

    var prevButton = document.getElementById("prevButton");
    var nextButton = document.getElementById("nextButton");

    prevButton.addEventListener("click", prevImage);
    nextButton.addEventListener("click", nextImage);

    showImage(currentIndex);

    var textOferta = document.getElementById('oferta');
    var lettersOferta = textOferta.textContent.split('');
    textOferta.innerHTML = '';
    lettersOferta.forEach(function(letter) {
        var span = document.createElement('span');
        span.textContent = letter;
        textOferta.appendChild(span);
    });

    function animateOferta() {
        var counterOferta = 0;
        var animationDelayOferta = 100;
        var timerOferta = setInterval(onTickOferta, animationDelayOferta);

        function onTickOferta() {
            var span = textOferta.querySelectorAll('span')[counterOferta];
            span.classList.add('visible');
            counterOferta++;
            if (counterOferta === lettersOferta.length) {
                clearInterval(timerOferta);
                setTimeout(function() {
                    textOferta.innerHTML = '';
                    lettersOferta.forEach(function(letter) {
                        var span = document.createElement('span');
                        span.textContent = letter;
                        textOferta.appendChild(span);
                    });
                    animateOferta();
                }, 1000);
            }
        }
    }

    animateOferta();
});
