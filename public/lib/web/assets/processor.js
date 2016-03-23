function processCorrection(correction) {
  
  try {
    var newCorrection = JSON.parse(correction);
    
    for(i = 0; i < newCorrection.results.length; i++) {
      var res = newCorrection.results[i];
      var taskDiv = document.getElementById("pan_task" + res.task);
      if(res.suc === "+") {
        taskDiv.className = "panel panel-success";
        $("#task" + res.task).collapse('hide');
      } else if(res.suc === "-") {
        taskDiv.className = "panel panel-danger";
        $("#task" + res.task).collapse('show');
      } else if(res.suc === "o") {
        taskDiv.className = "panel panel-warning";
        $("#task" + res.task).collapse('show');
      } else {
        alert("Es gab einen Fehler!");
      }
      
      var comDiv = document.getElementById("com_ex" + res.ex + "_task" + res.task);
      comDiv.innerHTML = "";
      
      var messages = res.messages;
      for(messageCount = 0; messageCount < messages.length; messageCount++) {
        if(messages[messageCount].suc === "+") {
          comDiv.innerHTML += "<div class=\"alert alert-success\">" + messages[messageCount].mes + "</div>";
        } else if(messages[messageCount].suc === "o") {
          comDiv.innerHTML += "<div class=\"alert alert-warning\">" + messages[messageCount].mes + "</div>";
        } else if(messages[messageCount].suc === "-") {
          comDiv.innerHTML += "<div class=\"alert alert-danger\">" + messages[messageCount].mes + "</div>";
        } else {
          comDiv.innerHTML += "<div class=\"alert alert-danger\">" + messages[messageCount].mes + "</div>";
        }
      }
      
      if(res.suc !== "-") {
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
      
    }
  } catch(err) {
    alert(err);
  }
  
  // TODO: Evtl. Update Live-Ansicht in andere Funktion auslagern
  // --> andere Aktivierung (onclick Data-toggle?)
  var live = document.getElementById("live");
  live.src = live.src;
}
