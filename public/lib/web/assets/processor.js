function processCorrection(correction) {
  
  alert(correction);
  
  var newCorrection = JSON.parse(correction);
  
  // TODO: entfernen!
  // var corrs = correction.split("\n");
  for(i = 0; i < newCorrection.results.length; i++) {
    var res = newCorrection.results[i];
    var taskDiv = document.getElementById("ex" + res.ex + "_task" + res.task);
    if(res.suc === "+") {
      taskDiv.className = "panel panel-success";
    } else if(res.suc === "-") {
      taskDiv.className = "panel panel-danger";
    } else if(res.suc === "o") {
      taskDiv.className = "panel panel-warning";
    } else {
      alert("Es gab einen Fehler!");
    }
  }
  
  // TODO: Update Live-Ansicht
  var live = document.getElementById("live");
  live.src = live.src;
}
