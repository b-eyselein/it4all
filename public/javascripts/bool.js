function extractParameters() {
    let parameters = '';
    let inputs = document.getElementById('valueDiv').getElementsByTagName('input');
    for (let i = 0; i < inputs.length; i++) {
        if (parameters !== '') {
            parameters += '&';
        }
        parameters += inputs[i].name + '=' + inputs[i].value;
    }
    parameters += '&learnerSolution=' + document.getElementById('learnerSolution').value;
    return parameters;
}

function testTheSolution(url) {
    // AJAX-Objekt erstellen, Callback-Funktion bereitstellen
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState === 4 && xhttp.status === 200) {
            processCorrection(xhttp.responseText);
        }
    };

    // AJAX-Objekt mit Daten fuellen, absenden
    let parameters = extractParameters();
    xhttp.open('PUT', url, true);
    xhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    xhttp.setRequestHeader('Accept', 'application/json');
    xhttp.send(parameters);
}

function processCorrection(correctionAsJson) {
    let completeCorrection = JSON.parse(correctionAsJson);

    let messageDiv = document.getElementById('messageDiv');
    messageDiv.innerHTML = '';

    if (completeCorrection.success === 'NONE') {
        messageDiv.innerHTML = '<div class=\'alert alert-danger\'>' + completeCorrection.asHtml + '</div>';
    } else {
        let correctionArray = completeCorrection.solutions;

        for (let i = 0; i < correctionArray.length; i++) {
            let correction = correctionArray[i];
            let solutionCell = document.getElementById(correction.assignmentsForJson);
            solutionCell.className = 'text-center ' + correction.color;
            solutionCell.innerHTML = '<span class=\'text-' + correction.color + '\'>' + correction.learnerValue + '</span>';
        }
    }

}
