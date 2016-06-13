function processCorrection(correction) {
  try {
    var newCorrection = JSON.parse(correction);
    
    var numOfSuccessfulResults = 0;
    
    for(i = 0; i < newCorrection.length; i++) {
      handleResult(newCorrection[i]);
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

function handleResult(result) {
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
