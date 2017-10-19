// Class selection

let chosenClasses = [];

function prepareFormForSubmitting() {
  document.getElementById("learnerSolution").value = JSON.stringify({
    classes: chosenClasses.map(function (clazz) {
      return {
        name: clazz,
        classType: "CLASS",
        methods: [],
        attributes: []
      };
    }),
    associations: [],
    implementations: []
  });
}

function select(span) {
  let baseform = span.dataset.baseform;

  if (chosenClasses.indexOf(baseform) < 0) {
    chosenClasses.push(baseform);
  } else {
    chosenClasses.splice(chosenClasses.indexOf(baseform), 1);
  }

  document.getElementById("classesList").innerHTML = asList(chosenClasses);

  for (let otherSpan of document.getElementById("exercisetext").getElementsByTagName("span")) {
    if (chosenClasses.indexOf(otherSpan.dataset.baseform) < 0) {
      otherSpan.className = "non-marked";
    } else {
      otherSpan.className = "marked bg-info";
    }
  }
}

function asList(array) {
  return array.length === 0 ? "<li>--</li>" : "<li>" + array.join("</li><li>") + "</li>";
}

// Matching

function allowDrop(ev) {
  ev.preventDefault();
}

function drag(ev) {
  ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev, type) {
  ev.preventDefault();

  let data = ev.dataTransfer.getData("text");
  let dragged = document.getElementById(data);

  if (dragged.dataset.typ === type) {
    ev.target.appendChild(dragged);
  }
}
