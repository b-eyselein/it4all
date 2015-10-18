/**
 * Example:<br>
 * <code>
 * + (1_1_1) name {+type=text;+required=true}<br>
 * + (1_1_2) email {-type=email;-required=true}<br>
 * + (1_1_3) passwort {+type=password;-required=true}<br></code>
 * 
 * @param msg
 *          Feedback in string format
 */
function writeFeedback(msg) {
  var errors = msg.split("\n");
  for(err in errors) {
    var line = errors[err];
    var divID = line.substring(3, 8);
    var divTask = document.getElementById(divID);
    var attributeLine = line.match("{.*?}")[0];
    // Remove starting "{" and trailing "}"
    attributeLine = attributeLine.substr(1, attributeLine.length - 2);
    if(line.substring(0, 1) === "+") {
      var newClassName = "panel panel-success";
      if(attributeLine.length !== "") {
        var divAttrs = document.getElementById("task" + divID);
        divAttrs.innerHTML = "";
        var attributes = attributeLine.split(";");
        for(i = 0; i < attributes.length; i++) {
          if(attributes[i].substring(0, 1) == "-") {
            newClassName = "panel panel-info";
            // FIXME: Change Text!!!!!
            var text = "<span class=\"glyphicon glyphicon-remove\"></span> Attribut \"" + attributes[i].substring(1).split("=")[0] + "\" nicht gefunden oder falscher Wert!";
            divAttrs.innerHTML += "<div class=\"alert alert-danger\">" + text + "</div>";
          } else if(attributes[i].substring(0, 1) === "+") {
            var text = "<span class=\"glyphicon glyphicon-ok\"></span> Attribut \"" + attributes[i].substring(1).split("=")[0] + "\" gefunden.";
            divAttrs.innerHTML += "<div class=\"alert alert-success\">" + text + "</div>";
          } else {
            // TODO: should not happen...
          }
        }
      }
      divTask.className = newClassName;
    } else if(line.substring(0, 1) === "-") {
      // Element nicht gefunden, direkt falsch
      divTask.className = "panel panel-danger";
      document.getElementById("task" + divID).innerHTML = "<div class=\"alert alert-danger\">Element wurde nicht gefunden!</div>";
    } else {
      
    }
  }
}

function send() {
  var text = editor.getValue();
  ws.send(text);
  document.getElementById('iframeid').src = document.getElementById('iframeid').src;
}
