//save and load as file
/*
 Adapted from http://www.html5rocks.com/en/tutorials/file/dndfiles/#toc-reading-files
*/
var loaded_graph;

window.onload = function () {	

	$('#myModal2').on('hidden.bs.modal', function () {
		//sel_elementname = "";
	})
	
	$("#startFullscreen").click(function () {
		enterFullscreen(document.getElementById("fullscreen"));
	});	
	$("#endFullscreen").click(function () {
		exitFullscreen();
	});		
	$('#file').click(function () {
    var input = document.getElementById("file");
		input.addEventListener("change", function (e) {
			var file = e.target.files[0];

			// Only render plain text files
			if (!file.type === "text/plain")
				return;

			var reader = new FileReader();

			reader.onload = function (event) {
				loaded_graph = event.target.result;
			};
			reader.readAsText(file);
		});
	});


    $("#graph_imp").click(function () {
		//Test if Json
		if (!isJson(loaded_graph)) {
			try {			
			document.getElementById("code").innerHTML = "";

			log.push("Die geladene Datei ist nicht im JSON-Format");
			document.getElementById("code").appendChild(preparelog(log));
			} catch (e) {
			}
		} else {
			try {
				document.getElementById("code").removeChild(document.getElementById("list_error"));
			} catch (e) {
			}
			try {
				var parsed = JSON.parse(loaded_graph);
				graph.fromJSON(parsed.graph);
				parentChildNodes = parsed.parentChildNodes;
				rebuildGraph();
			} catch (e) {
				log.push("Die JSON-Datei konnte nicht geladen werden!");
				document.getElementById("code").appendChild(preparelog(log));
			}
		}		
    });

}
	
//Test: Is Json Objrect
function isJson(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

//writer via FileSaver.js --->  https://github.com/eligrey/FileSaver.js
function saveGraphAsTxt() {
	var input={"graph":graph.toJSON(),"parentChildNodes":parentChildNodes};
	input=JSON.stringify(input);
   // var input = JSON.stringify(graph.toJSON());
    var blob = new Blob([input], {type: "text/plain;charset=utf-8"});
    saveAs(blob, "graph.json");
}

function enterFullscreen(element) {
	localStorage.setItem("parentChildNodes", JSON.stringify(parentChildNodes));
	var currentGraph = graph.toJSON();
	if(element.requestFullscreen) {
		element.requestFullscreen();
	} else if(element.mozRequestFullScreen) {
		element.mozRequestFullScreen();
	} else if(element.msRequestFullscreen) {
		element.msRequestFullscreen();
	} else if(element.webkitRequestFullscreen) {
		element.webkitRequestFullscreen();
	}
	graph.fromJSON(currentGraph);
	parentChildNodes = JSON.parse(localStorage.getItem("parentChildNodes"));	
	reSetSelection();
}

function exitFullscreen() {
  if(document.exitFullscreen) {
    document.exitFullscreen();
  } else if(document.mozCancelFullScreen) {
    document.mozCancelFullScreen();
  } else if(document.webkitExitFullscreen) {
    document.webkitExitFullscreen();
  }
}

function prepareFormForSubmitting() {
    $('#preCode').val();
}

function onAjaxSuccess(response) {
    console.log(response);
}

function onAjaxError(jqXHR) {
    console.error(jqXHR.responseText);
}

function testSol(theUrl) {
    let answers = $('#preCode').val();

    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: theUrl,
        data: JSON.stringify(answers),
        async: true,
        success: onAjaxSuccess,
        error: onAjaxError
    });
}

