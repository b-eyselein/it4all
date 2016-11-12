function processCorrection(correction) {
  var newResults = JSON.parse(correction);
  
  document.getElementById("element_result_container").innerHTML = newResults.asHtml;
  
  var commitButton = document.getElementById("commit");
  commitButton.disabled = false;
  if(newResults.success == "COMPLETE") {
    commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben.";
    commitButton.className = "btn btn-success";
  } else {
    commitButton.title = "Sie können ihre Lösung abgeben, haben aber nicht alles richtig.";
    commitButton.className = "btn btn-warning";
  }
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function testTheSolution(url) {
  // AJAX-Objekt erstellen, Callback-Funktion bereitstellen
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      processCorrection(xhttp.responseText);
    }
  };
  
  // AJAX-Objekt mit Daten fuellen, absenden
  var parameters = "editorContent=" + encodeURIComponent(editor.getValue());
  xhttp.open("PUT", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
}
