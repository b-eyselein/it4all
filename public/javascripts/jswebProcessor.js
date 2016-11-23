function extractParameters() {
return  "editorContent=" + encodeURIComponent(editor.getValue());
}

function processCorrection(jsonResponseText) {
  document.getElementById("testsDiv").innerHTML = JSON.parse(jsonResponseText).asHtml;
}