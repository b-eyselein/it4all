import * as $ from 'jquery';

let testBtn: JQuery, solutionInput: JQuery;

interface NaryAdditionSolution {
    base: number,
    summand1: string,
    summand2: string,
    solution: string
}

interface NaryAdditionResult {
    correct: boolean
}

function onNaryAdditionSuccess(response: NaryAdditionResult): void {
    testBtn.prop('disabled', false);

    if (response.correct) {
        solutionInput.removeClass('is-invalid').addClass('is-valid');
        $('#correctnessHook').html('<h1 class="text-center">&check;</h1>');
    } else {
        solutionInput.removeClass('is-valid').addClass('is-invalid');
        $('#correctnessHook').html('');
    }
}

function onNaryAdditionError(jqXHR): void {
    testBtn.prop('disabled', false);
    console.error(jqXHR);
}

function testSol(): void {

    const solution = (solutionInput.val() as string).split('').reverse().join('');

    if (solution === '') {
        alert('Sie können keine leere Lösung abgeben!');
        return;
    }

    testBtn.prop('disabled', true);

    const completeSolution: NaryAdditionSolution = {
        summand1: $('#firstSummand').val() as string,
        summand2: $('#secondSummand').val() as string,
        base: $('#base').data('base'),
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
        success: onNaryAdditionSuccess,
        error: onNaryAdditionError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    solutionInput = $('#solution');
});

