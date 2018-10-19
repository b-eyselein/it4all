import * as $ from 'jquery';

let testBtn: JQuery;


interface QuestionResult {
    id: number,
    chosen: boolean,
    correct: boolean,
    explanation: string
}

function onAjaxSuccess(response: QuestionResult[]): void {
    testBtn.prop('disabled', false);

    for (let entry of response) {
        let parent: JQuery<HTMLLabelElement> = $('#' + entry.id).parent() as JQuery<HTMLLabelElement>;

        parent.find('.correctnessHook').remove();
        parent.parent().find('.text-info').remove();

        if (entry.correct) {
            parent.removeClass('text-danger').addClass('text-success');
            parent.append('<b class="correctnessHook">&check;</b>');
        } else {
            parent.removeClass('text-success').addClass('text-danger');
            if (entry.chosen) {
                parent.parent().append(`
<p class="text-info">Diese Auswahl ist falsch, wurde aber ausgewählt. ${entry.explanation ? 'Erklärung: ' + entry.explanation : ''}</p>`)
            } else {
                parent.parent().append(`
<p class="text-info">Diese Auswahl ist korrekt, wurde aber nicht ausgewählt. ${entry.explanation ? 'Erklärung: ' + entry.explanation : ''}</p>`)
            }
        }
    }
}

function onAjaxError(jqXHR): void {
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
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        success: onAjaxSuccess,
        error: onAjaxError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testQuestionSol);
});