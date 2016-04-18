function processCorrection(correction) {
  try {
    var newCorrection = JSON.parse(correction);
    
    for(i = 0; i < newCorrection.length; i++) {
      handleResult(newCorrection[i]);
    }
  } catch(err) {
    alert(err);
  }
  
  // TODO: Evtl. Update Live-Ansicht in andere Funktion auslagern
  // --> andere Aktivierung (onclick Data-toggle?)
  var live = document.getElementById("live");
  live.src = live.src;
}

function handleResult(res) {
  var taskID = res.task.key.id;
  var taskDiv = document.getElementById("pan_task" + taskID);
  
  if(res.success === "COMPLETE") {
    taskDiv.className = "panel panel-success";
    $("#task" + taskID).collapse('hide');
  } else if(res.success === "PARTIALLY") {
    taskDiv.className = "panel panel-warning";
    $("#task" + taskID).collapse('show');
  } else if(res.success === "NONE") {
    taskDiv.className = "panel panel-danger";
    $("#task" + taskID).collapse('show');
  } else {
    alert("Es gab einen Fehler!");
  }
  
  var comDiv = document.getElementById("com_task" + taskID);
  comDiv.innerHTML = "";
  
  if(res.success === "COMPLETE" || res.success === "PARTIALLY") {
    comDiv.innerHTML += "<div class=\"alert alert-success\">Element wurde gefunden!</div>";
  } else {
    comDiv.innerHTML += "<div class=\"alert alert-danger\">Element konnte nicht gefunden werden!</div>";
  }
  
  for(attCount = 0; attCount < res.attributeResults.length; attCount++) {
    var attr = res.attributeResults[attCount];
    if(attr.success === "COMPLETE") {
      comDiv.innerHTML += "<div class=\"alert alert-success\">Attribut \"" + attr.key + "\" hat gesuchten Wert.</div>";
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
}
