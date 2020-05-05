function fakultaet() {
    const num = document.getElementById('number').value;
    let fak = 1;
    for (let i = 1; i <= num; i++) {
        fak = fak * i;
    }
    document.getElementById('result').textContent = fak;
}
