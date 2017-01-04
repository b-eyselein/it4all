function extractParameters() {
  return "editorContent=" + encodeURIComponent(editor.getValue());
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function processCorrection(correction) {
  var parsedCorr = JSON.parse(correction);
  
  document.getElementById("correction").innerHTML = parsedCorr.asHtml;
}
