function extractParameters() {
  var editorContent = editor.getValue();
  var parameters = "editorContent=" + encodeURIComponent(editorContent);
  
  var feedbackLevel = document.getElementById("feedbackLevel");
  var realLevel = feedbackLevel.options[feedbackLevel.selectedIndex].value;
  parameters += "&feedbackLevel=" + realLevel;
  return parameters;
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function processCorrection(correction) {
  var parsedCorr = JSON.parse(correction);
  document.getElementById("resultPanel").hidden = false;
  document.getElementById("resultDiv").innerHTML = parsedCorr.asHtml;
}
