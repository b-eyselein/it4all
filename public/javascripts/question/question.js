/**
 * @param {object} response
 * @param {number[]} response.correct
 * @param {number[]} response.wrong
 * @param {number[]} response.missing
 */
function onAjaxSuccess(response) {
    console.log(response);
    for (let correct of response.correct) {
        let parent = $('#' + correct).parent();
        parent.addClass('bg-success');
        parent.attr('title', 'TODO!');
    }
    for (let missing of response.missing) {
        let parent = $('#' + missing).parent();
        parent.addClass('bg-warning');
        parent.attr('title', 'TODO!');
    }
    for (let wrong of response.wrong) {
        let parent = $('#' + wrong).parent();
        parent.addClass('bg-danger');
        parent.attr('title', 'TODO!');
    }
}

function onAjaxError(jqXHR) {
    console.error(jqXHR.responseText);
}

/**
 * @param {string} theUrl
 */
function testSol(theUrl) {
    let theChosen = $('input[data-answerid]').filter((index, elem) => elem.checked).map((index, elem) => parseInt(elem.dataset.answerid)).get();

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