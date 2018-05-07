function activityCorrection(): void {
    let toolType = $('#toolType').val(), exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();

    // noinspection JSUnresolvedFunction, JSUnresolvedVariable
    let url = jsRoutes.controllers.ExerciseController.correctLive(toolType, exerciseId, exercisePart).url;

    $('#testButton').prop('disabled', true);

    let dataToSend = {
        exercisePart,
        solution: $('#preCode').text()
    };

    $.ajax({
        type: 'PUT',
        // dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url,
        data: JSON.stringify(dataToSend),
        async: true,
        success: onProgCorrectionSuccess,
        error: onProgCorrectionError
    });
}

$(document).ready(function () {
    $('#testButton').click(activityCorrection);
});
