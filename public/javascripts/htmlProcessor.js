function testTheSolution(url, type) {
  // AJAX-Objekt erstellen, Callback-Funktion bereitstellen
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState == 4 && xhttp.status == 200) {
      processCorrection(xhttp.responseText, type);
    }
  };
  
  // AJAX-Objekt mit Daten fuellen, absenden
  var parameters = "editorContent=" + encodeURIComponent(editor.getValue());
  xhttp.open("PUT", url, true);
  xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  xhttp.setRequestHeader("Accept", "application/json");
  xhttp.send(parameters);
}

function prepareFormForSubmitting() {
  document.getElementById("editorContent").value = editor.getValue();
}

function processCorrection(correction, type) {
  try {
    var newCorrection = JSON.parse(correction);
    
    var numOfSuccessfulResults = 0;
    
    for(i = 0; i < newCorrection.length; i++) {
      if(type == "html") {
        handleHtmlResult(newCorrection[i]);
      } else {
        handleCssResult(newCorrection[i]);
      }
      if(newCorrection[i].success === "COMPLETE") {
        numOfSuccessfulResults++;
      }
    }
    
    var commitButton = document.getElementById("commit");
    commitButton.disabled = false;
    if(numOfSuccessfulResults == newCorrection.length) {
      commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben.";
      commitButton.className = "btn btn-success";
    } else {
      commitButton.title = "Sie k&ouml;nnen ihre L&ouml;sung abgeben, haben aber nicht alles richtig.";
      commitButton.className = "btn btn-warning";
    }
  } catch(err) {
    alert(err);
  }
  
  // TODO: Evtl. Update Live-Ansicht in andere Funktion auslagern
  // --> andere Aktivierung (onclick Data-toggle?)
  var live = document.getElementById("live");
  live.src = live.src;
}

function updatePreview() {
  var toWrite = unescapeHTML(editor.getValue());
  var theIFrame = document.getElementById("preview").contentWindow.document;
  theIFrame.open();
  theIFrame.write(toWrite);
  theIFrame.close();
}

function unescapeHTML(escapedHTML) {
  return escapedHTML.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&');
}

function handleHtmlResult(result) {
  var taskID = result.task.key.taskId;
  var taskDiv = document.getElementById("pan_task" + taskID);
  
  if(result.success === "COMPLETE") {
    taskDiv.className = "panel panel-success";
    $("#task" + taskID).collapse('hide');
  } else if(result.success === "PARTIALLY") {
    taskDiv.className = "panel panel-warning";
    $("#task" + taskID).collapse('show');
  } else if(result.success === "NONE") {
    taskDiv.className = "panel panel-danger";
    $("#task" + taskID).collapse('show');
  } else {
    alert("Es gab einen Fehler!");
  }
  
  var comDiv = document.getElementById("com_task" + taskID);
  comDiv.innerHTML = "";
  
  if(result.success === "COMPLETE" || result.success === "PARTIALLY") {
    comDiv.innerHTML += "<div class=\"alert alert-success\">Element wurde gefunden!</div>";
    if(result.parentsMissing !== null) {
      comDiv.innerHTML += "<div class=\"alert alert-danger\">Element hat nicht die richtigen Elternelemente. Folgende fehlen: "
          + result.parentsMissing.join(", ") + "</div>";
    }
  } else {
    comDiv.innerHTML += "<div class=\"alert alert-danger\">Element konnte nicht gefunden werden!</div>";
  }
  
  for(attCount = 0; attCount < result.attributeResults.length; attCount++) {
    var attr = result.attributeResults[attCount];
    if(attr.success === "COMPLETE") {
      comDiv.innerHTML += "<div class=\"alert alert-success\">Attribut \"" + attr.key
          + "\" hat den gesuchten Wert.</div>";
    } else if(attr.success === "PARTIALLY") {
      comDiv.innerHTML += "<div class=\"alert alert-danger\">Attribut \"" + attr.key
          + "\" hat nicht den gesuchten Wert!</div>";
    } else if(attr.success === "NONE") {
      comDiv.innerHTML += "<div class=\"alert alert-danger\">Attribut \"" + attr.key
          + "\" konnte nicht gefunden werden!</div>";
    } else {
      comDiv.innerHTML += "<div class=\"alert alert-warning\">Es gab einen Fehler beim Suchen des Attributes \""
          + attr.key + "\"</div>";
    }
  }
  
  // TODO: childResults!
  for(childCount = 0; childCount < result.childResults.length; childCount++) {
    var childResult = result.childResults[childCount];
    if(childResult.success === "COMPLETE") {
      comDiv.innerHTML += "<div class=\"alert alert-success\">Kindelement \"" + childResult.key
          + "\" hat den gesuchten Wert \"" + childResult.value + "\".</div>";
    } else if(childResult.success === "PARTIALLY") {
      comDiv.innerHTML += "<div class=\"alert alert-danger\">Kindelement \"" + childResult.key
          + "\" hat nicht den gesuchten Wert \"" + childResult.value + "\"!</div>";
    } else if(childResult.success === "NONE") {
      comDiv.innerHTML += "<div class=\"alert alert-danger\">Kindelement \"" + childResult.key + "\" mit Wert \""
          + childResult.value + "\" konnte nicht gefunden werden!</div>";
    } else {
      comDiv.innerHTML += "<div class=\"alert alert-warning\">Es gab einen Fehler beim Suchen des Kindelements \""
          + childResult.key + "\"</div>";
    }
  }
}

function handleCssResult(result) {
  var taskID = result.task.key.taskId;
  var taskDiv = document.getElementById("pan_task" + taskID);
  
  if(result.success === "COMPLETE") {
    taskDiv.className = "panel panel-success";
    $("#task" + taskID).collapse('hide');
  } else if(result.success === "PARTIALLY") {
    taskDiv.className = "panel panel-warning";
    $("#task" + taskID).collapse('show');
  } else if(result.success === "NONE") {
    taskDiv.className = "panel panel-danger";
    $("#task" + taskID).collapse('show');
  } else {
    alert("Es gab einen Fehler!");
  }
  
  var comDiv = document.getElementById("com_task" + taskID);
  comDiv.innerHTML = "";
  
  if(result.success === "NONE") {
    var newDiv = document.createElement("div");
    newDiv.className = "alert alert-danger";
    newDiv.innerHTML = "Es konnten keine entsprechenden Elemente gefunden werden! Haben Sie den Html-Teil der Aufgabe komplett bearbeitet?";
    comDiv.appendChild(newDiv);
  }
  if(result.success === "PARTIALLY") {
    var newDiv = document.createElement("div");
    newDiv.className = "alert alert-warning";
    newDiv.innerHTML = "Nicht alle Elemente haben die entsprechende Eigenschaft!";
    comDiv.appendChild(newDiv);
  }
  if(result.success === "SUCCESS") {
    var newDiv = document.createElement("div");
    newDiv.className = "alert alert-success";
    newDiv.innerHTML = "Alle Elemente haben die entsprechende Eigenschaft!";
    comDiv.appendChild(newDiv);
  }
}
