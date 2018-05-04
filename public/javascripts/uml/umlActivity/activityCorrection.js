function testSol() {
    let toolType = $('#toolType').val(), exerciseId = $('#exerciseId').val(), exercisePart = $('#exercisePart').val();
    let url = jsRoutes.controllers.ExerciseController.correctLive(toolType, exerciseId, exercisePart).url;
    $('#testButton').prop('disabled', true);
    let dataToSend = {
        exercisePart,
        solution: $('#preCode').text()
    };
    $.ajax({
        type: 'PUT',
        contentType: 'application/json',
        url,
        data: JSON.stringify(dataToSend),
        async: true,
        success: onProgCorrectionSuccess,
        error: onProgCorrectionError
    });
}
$(document).ready(function () {
    $('#testButton').click(testSol);
});
//# sourceMappingURL=activityCorrection.js.map