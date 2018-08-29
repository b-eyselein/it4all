import * as $ from 'jquery';

let testBtn: JQuery;


interface QuestionResult {
    id: number,
    chosen: boolean,
    correct: boolean,
    explanation: string
}

/**
 * @param {Array<{id: Number, chosen: Boolean, correct: Boolean, explanation: String=}>} response
 */
function onAjaxSuccess(response: QuestionResult[]) {
    testBtn.prop('disabled', false);


    for (let entry of response) {
        let parent = $('#' + entry.id).parent();
        if (entry.correct) {
            parent.addClass('bg-success');
        } else {
            parent.addClass('bg-danger');
            if (entry.chosen) {
                parent.parent().append(`<p class="text-info">Diese Auswahl ist falsch, wurde aber ausgewählt${entry.explanation ? ': ' + entry.explanation : ''}!</p>`)
            } else {
                parent.parent().append(`<p class="text-info">Diese Auswahl ist korrekt, wurde aber nicht ausgewählt${entry.explanation ? ': ' + entry.explanation : ''}!</p>`)
            }
        }
    }
}

function onAjaxError(jqXHR) {
    testBtn.prop('disabled', false);
    console.error(jqXHR.responseText);
}

interface QuestionSolution {
    questionType: string,
    chosen: number[]
}

function testQuestionSol(): void {

    testBtn.prop('disabled', true);

    let chosen: number[] = $('#answerDiv').find('input[data-answerid]')
        .filter((index, elem: HTMLInputElement) => elem.checked)
        .map((index, elem) => parseInt(elem.dataset.answerid)).get();

    console.warn(chosen);

    let solution: QuestionSolution = {
        questionType: "CHOICE", // theQuestionType,
        chosen
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: testBtn.data('url'),
        data: JSON.stringify(solution),
        async: true,
        success: onAjaxSuccess,
        error: onAjaxError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testQuestionSol);
});