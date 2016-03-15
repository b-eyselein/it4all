function processCorrection(correction) {
  
  try {
    var newCorrection = JSON.parse(correction);
  } catch(err) {
    alert(err);
  }
  
  for(i = 0; i < newCorrection.results.length; i++) {
    var res = newCorrection.results[i];
    var taskDiv = document.getElementById("pan_ex" + res.ex + "_task" + res.task);
    if(res.suc === "+") {
      taskDiv.className = "panel panel-success";
    } else if(res.suc === "-") {
      taskDiv.className = "panel panel-danger";
    } else if(res.suc === "o") {
      taskDiv.className = "panel panel-warning";
    } else {
      alert("Es gab einen Fehler!");
    }
    
    var comDiv = document.getElementById("com_ex" + res.ex + "_task" + res.task);
    comDiv.innerHTML = res.message;
    
  }
  
  // TODO: Evtl. Update Live-Ansicht in andere Funktion auslagern
  // --> andere Aktivierung (onclick Data-toggle?)
  var live = document.getElementById("live");
  live.src = live.src;
}
