function processCorrection(correction) {
  document.getElementById("correction").innerHTML = correction;
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function extractParameters() {
  return "editorContent=" + encodeURIComponent(editor.getValue());
}
