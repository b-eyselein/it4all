import * as $ from 'jquery';

let testBtn: JQuery, solutionInput: JQuery;

interface NaryConversionSolution {
    startingNB: string,
    targetNB: string,
    value: string,
    solution: string
}

interface NaryConversionResult {
    correct: boolean
}

function onNaryConversionSuccess(response: NaryConversionResult): void {
    testBtn.prop('disabled', false);

    if (response.correct) {
        solutionInput.removeClass('is-invalid').addClass('is-valid');
        $('#correctnessHook').html('<h1 class="text-center">&check;</h1>');
    } else {
        solutionInput.removeClass('is-valid').addClass('is-invalid');
        $('#correctnessHook').html('');
    }
}

function onNaryConversionError(jqXHR): void {
    testBtn.prop('disabled', false);
    console.error(jqXHR);
}

function testSol(): void {

    const solution = solutionInput.val() as string;

    if (solution === '') {
        alert('Sie können keine leere Lösung abgeben!');
        return;
    }

    testBtn.prop('disabled', true);

    const completeSolution: NaryConversionSolution = {
        startingNB: $('#startingNB').data('base'),
        targetNB: $('#targetNB').data('base'),
        value: $('#value').val() as string,
        solution
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify(completeSolution),
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        success: onNaryConversionSuccess,
        error: onNaryConversionError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    solutionInput = $('#solution');
});