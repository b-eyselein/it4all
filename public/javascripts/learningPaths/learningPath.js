let currentPart = 1;

let maxPart;

function nextPart() {
    currentPart++;
    $('#sec_' + currentPart).prop('hidden', false);

    if (currentPart >= maxPart) {
        $('#nextPartButton').prop('disabled', true);
    }
}

// noinspection JSUnusedGlobalSymbols
function correctTrueFalse(button) {
    let clickedButton = $(button);
    let clickedValue = clickedButton.data('value');

    let inputGroup = clickedButton.parent();

    let sampleSolution = inputGroup.find('code').data('solution');

    if (clickedValue === sampleSolution) {
        clickedButton.removeClass('btn-default btn-primary btn-danger').addClass('btn-success');
        inputGroup.find('button[data-value=' + (!clickedValue) + ']').removeClass('btn-success btn-danger btn-primary').addClass('btn-default');
    } else {
        inputGroup.find('button[data-value=' + sampleSolution + ']').removeClass('btn-default btn-success btn-danger').addClass('btn-primary');
        clickedButton.removeClass('btn-default btn-primary btn-success').addClass('btn-danger');
    }
}

$(document).ready(function () {
    maxPart = $('section[id^="sec_"]').length;
});