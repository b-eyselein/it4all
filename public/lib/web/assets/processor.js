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
    comDiv.innerHTML = "";
    // TODO: Use message?
    // comDiv.innerHTML = res.message;
    
    if(res.suc === "-") {
      // Element wurde nicht gefunden
      comDiv.innerHTML += "<div class=\"alert alert-danger\">Element konnte nicht gefunden werden!</div>";
    }
    
    for(attCount = 0; attCount < res.attrs.length; attCount++) {
      var attr = res.attrs[attCount];
      if(attr.suc === "+") {
        comDiv.innerHTML += "<div class=\"alert alert-success\">Attribut \"" + attr.key
            + "\" hat gesuchten Wert.</div>";
      } else if(attr.suc === "-") {
        comDiv.innerHTML += "<div class=\"alert alert-danger\">Attribut \"" + attr.key
            + "\" hat nicht den gesuchten Wert oder konnte nicht gefunden werden!</div>";
      } else {
        comDiv.innerHTML += "<div class=\"alert alert-warning\">Es gab einen Fehler beim Suchen des Attributes \""
            + attr.key + "\"</div>";
      }
    }
    
  }
  
  // TODO: Evtl. Update Live-Ansicht in andere Funktion auslagern
  // --> andere Aktivierung (onclick Data-toggle?)
  var live = document.getElementById("live");
  live.src = live.src;
}
