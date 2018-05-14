/**
 * @param {Array<{id: Number, chosen: Boolean, correct: Boolean, explanation: String=}>} response
 */
function onAjaxSuccess(response) {
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
    console.error(jqXHR.responseText);
}

/**
 * @param {string} theUrl
 */
function testSol(theUrl) {
    $('#submit').prop('disabled', true);

    let theChosen = $('#answerDiv').find('input[data-answerid]').filter((index, elem) => elem.checked).map((index, elem) => parseInt(elem.dataset.answerid)).get();

    let theData = {
        quiz: 1,
        question: 1,
        questionType: theQuestionType,
        chosen: theChosen
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: theUrl,
        data: JSON.stringify(theData),
        async: true,
        success: onAjaxSuccess,
        error: onAjaxError
    });
}