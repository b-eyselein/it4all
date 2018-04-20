let currentPart = 1;

function nextPart() {
    currentPart++;
    $('#sec_' + currentPart).prop('hidden', false);
}

function correctBool(button) {
    let clickedButton = $(button);

    let inputGroup = clickedButton.parent();

    let clickedValue = parseInt(clickedButton.text());
    let sampleSolution = inputGroup.find('code').data('solution');

    if (clickedValue === sampleSolution) {
        clickedButton.removeClass('btn-default btn-primary btn-danger').addClass('btn-success');
        inputGroup.find('button:contains(' + ((clickedValue + 1) % 2) + ')').removeClass('btn-success btn-danger btn-primary').addClass('btn-default');
    } else {
        inputGroup.find('button:contains(' + sampleSolution + ')').removeClass('btn-default btn-success btn-danger').addClass('btn-primary');
        clickedButton.removeClass('btn-default btn-primary btn-success').addClass('btn-danger');
    }
}