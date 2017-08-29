function deleteExercise(theUrl) {
  $.ajax({
    type: 'DELETE',
    url: theUrl,
    async: true,
    success: function(result) {
      $("#messages").append("<div class=\"alert alert-success\">" + result.message + "</div>");
      $("#tr1_" + result.exerciseId + ", #tr2_" + result.exerciseId).remove();
    },
    error: function(jqXHR, error, errorThrown) {
      $("#messages").append("<div class=\"alert alert-danger\">" + jqXHR.responseJSON + "</div>");
    }
  });
}
