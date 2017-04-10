var learnerSolution = {
  classes: [],
  methods: [/* remains empty in diagDrawingHelp */],
  attributes: [/* remains empty in diagDrawingHelp */],
  connections: [/* remains empty in classSelectin */]
};

function prepareFormForSubmitting() {
  document.getElementById("learnerSolution").value = extractParameters();
}

function extractParameters() {
  for (var cell of graph.getCells()) {
    var clazz = {
      name: cell.attributes.name,
      methods: cell.attributes.methods,
      attributes: cell.attributes.attributes
    };
    learnerSolution.classes.push(clazz);
  }
  
  for (var conn of graph.getLinks()) {
    var connection = {
      type: conn.attributes.type,
      source: graph.getCell(conn.attributes.source.id).attr('.uml-class-name-text/text'),
      target: graph.getCell(conn.attributes.target.id).attr('.uml-class-name-text/text'),
      mulStart: conn.attributes.labels[0].attrs.text.text,
      mulTarget: conn.attributes.labels[1].attrs.text.text
    }
    learnerSolution.connections.push(connection);
  }
  
  return JSON.stringify(learnerSolution, null, 2);
}
