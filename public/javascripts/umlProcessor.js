function processCorrection(correction) {
  var newResults = JSON.parse(correction);
  
  var numOfSuccessfulResults = 0;
  
  var resultsContainer = document.getElementById("element_result_container");
  
  var toSet = "";
  for(var i = 0; i < newResults.length; i++) {
    toSet += newResults[i].asHtml;
    if(newResults[i].errorType === "NONE") {
      numOfSuccessfulResults++;
    }
  }
  
  resultsContainer.innerHTML = toSet;
  
  var commitButton = document.getElementById("commit");
  commitButton.disabled = false;
  if(numOfSuccessfulResults == newResults.length) {
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

function affirmFinalCommit() {
  window.alert("Erfolgreich abgegeben!");
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
  //var parameters = "editorContent=" + encodeURIComponent(editor.getValue());
  var parameters =json;
  xhttp.open("PUT", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
}
