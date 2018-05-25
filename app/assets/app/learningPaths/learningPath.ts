import * as $ from 'jquery';

let currentPart: number = 0;

let maxPart: number;

function nextPart(): void {
    currentPart++;
    $('#sec_' + currentPart).prop('hidden', false);

    if (currentPart >= maxPart) {
        $('#nextPartButton').prop('disabled', true);
    }
}

function correctTrueFalse(button: HTMLButtonElement): void {
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

$(() => {
    maxPart = $('section[id^="sec_"]').length;

    $('#nextPartButton').on('click', nextPart);

    $('.questionSection').find('button').on('click', event => correctTrueFalse(event.target as HTMLButtonElement));

    nextPart();
});