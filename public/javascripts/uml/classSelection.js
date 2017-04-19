var learnerSolution = {
  classes: [],
  connections: [/* remains empty in classSelectin */]
};

function prepareFormForSubmitting() {
  document.getElementById("learnerSolution").value = JSON.stringify({
    classes: learnerSolution.classes.map(function(clazz) {
      return {
        name: clazz,
        methods: [],
        attributes: []
      };
    }),
    connections: []
  });
}

function select(span) {
  var baseform = span.dataset.baseform;
  
  if(learnerSolution.classes.indexOf(baseform) < 0) {
    learnerSolution.classes.push(baseform);
  } else {
    learnerSolution.classes.splice(learnerSolution.classes.indexOf(baseform), 1);
  }
  
  document.getElementById("classesList").innerHTML = asList(learnerSolution.classes);
  
  for(var otherSpan of document.getElementById("exercisetext").getElementsByTagName("span")) {
    if(learnerSolution.classes.indexOf(otherSpan.dataset.baseform) < 0) {
      otherSpan.className = "nonMarked";
    } else {
      otherSpan.className = "marked bg-info";
    }
  }
}

function asList(array) {
  return array.length == 0 ? "<li>--</li>" : "<li>" + array.join("</li><li>") + "</li>";
}
