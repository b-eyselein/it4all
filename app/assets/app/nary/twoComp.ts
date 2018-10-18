import * as $ from 'jquery';

let testBtn: JQuery, binaryAbs: JQuery, invertedAbs: JQuery, solutionInput: JQuery;

interface TwoCompSolution {
    value: number,
    binaryAbs: string,
    invertedAbs: string,
    solution: string
}

interface TwoCompResult {
    correct: boolean
    invertedAbs: boolean
    binaryAbs: boolean
}

function colorElement(element: JQuery, correct: boolean): void {
    testBtn.prop('disabled', false);

    if (correct) {
        element.removeClass('is-invalid').addClass('is-valid');
    } else {
        element.removeClass('is-valid').addClass('is-invalid');
    }
}

function onAjaxSuccess(response: TwoCompResult): void {
    testBtn.prop('disabled', false);

    colorElement(binaryAbs, response.binaryAbs);
    $('#binAbsCorrectnessHook').html(response.binaryAbs ? '<h1 class="text-center">&check;</h1>' : '');

    colorElement(invertedAbs, response.invertedAbs);
    $('#invAbsCorrectnessHook').html(response.invertedAbs ? '<h1 class="text-center">&check;</h1>' : '');

    colorElement(solutionInput, response.correct);
    $('#correctnessHook').html(response.correct ? '<h1 class="text-center">&check;</h1>' : '');
}

function testSol(): void {

    const solution: string = solutionInput.val() as string;

    if (solution === '') {
        alert('Sie können keine leere Lösung abgeben!');
        return;
    }

    testBtn.prop('disabled', true);

    const completeSolution: TwoCompSolution = {
        value: parseInt($('#value').val() as string),
        binaryAbs: binaryAbs.val() as string,
        invertedAbs: invertedAbs.val() as string,
        solution
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify(completeSolution),
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        async: true,
        success: onAjaxSuccess
    });
}

$(() => {
    binaryAbs = $('#binaryAbs');
    invertedAbs = $('#invertedAbs');
    solutionInput = $('#solution');

    testBtn = $('#testBtn');
    testBtn.on('click', testSol);
});

