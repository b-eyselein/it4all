function passwordStrength() {
    let passwordValue = document.getElementById('password').value;
    let errorsSpan = document.getElementById('errors');

    if (passwordValue === 'meinpasswort') {
        errorsSpan.textContent = 'Zu einfach';
    } else if (passwordValue.length < 8) {
        errorsSpan.textContent = 'Zu kurz';
    } else {
        errorsSpan.textContent = '';
    }
}
