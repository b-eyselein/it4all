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

var classes = [];
var methods = [];
var attributes = [];

function mark(span) {
  switch (span.className){
  case "nomen":
    span.className = "nomen-blue bg-info";
    break;
  case "nomen-blue bg-info":
    span.className = "nomen-green bg-success";
    break;
  case "nomen-green bg-success":
    span.className = "nomen-red bg-danger";
    break;
  case "nomen-red bg-danger":
    span.className = "nomen";
    break;
  default:
    span.className = "nomen";
    break;
  }
}

function extractParameters() {
  var exercisetext = document.getElementById("exercisetext");
  var cclasses = exercisetext.getElementsByClassName("bg-info");
  var cmethods = exercisetext.getElementsByClassName("bg-success");
  var cattributes = exercisetext.getElementsByClassName("bg-danger");
  var classes = [];
  for(i = 0; i < cclasses.length; i++) {
    if(cclasses[i].getAttribute('data-baseform') != null) {
      classes.push(cclasses[i].getAttribute('data-baseform'));
    } else {
      classes.push(cclasses[i].innerText);
    }
  }
  var methods = [];
  for(i = 0; i < cmethods.length; i++) {
    if(cmethods[i].getAttribute('data-baseform') != null) {
      methods.push(cmethods[i].getAttribute('data-baseform'));
    } else {
      methods.push(cmethods[i].innerText);
    }
  }
  var attributes = [];
  for(i = 0; i < cattributes.length; i++) {
    if(cattributes[i].getAttribute('data-baseform') != null) {
      attributes.push(cattributes[i].getAttribute('data-baseform'));
    } else {
      attributes.push(cattributes[i].innerText);
    }
  }
  console.log(classes);
  console.log(methods);
  console.log(attributes);
  
  var json = "\{\"classes\":[";
  for(i = 0; i < classes.length - 1; i++) {
    json += "\"" + classes[i] + "\"" + ",";
  }
  json += "\"" + classes[classes.length - 1] + "\"" + "],";
  json += "\"methods\":[";
  for(i = 0; i < methods.length - 1; i++) {
    json += "\"" + methods[i] + "\"" + ",";
  }
  json += "\"" + methods[methods.length - 1] + "\"" + "],";
  json += "\"attributes\":[";
  for(i = 0; i < attributes.length - 1; i++) {
    json += "\"" + attributes[i] + "\"" + ",";
  }
  json += "\"" + attributes[attributes.length - 1] + "\"" + "]" + "\}";
  console.log(json);
  
  return json;
}

function processCorrection(text) {
  console.log("Processing correction: " + text);
}

function prepareFormForSubmitting() {
  document.getElementById("learnerSolution").value = extractParameters();
  console.log(document.getElementById("learnerSolution").value);
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
  // var parameters = "editorContent=" +
  // encodeURIComponent(editor.getValue());
  var json = "";
  var parameters = json;
  xhttp.open("PUT", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
}
