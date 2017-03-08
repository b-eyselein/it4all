//console.log("methoden: "+document.getElementsByClassName("uml-class-attrs-text"));
					var divWidth=document.getElementById("sizepaper").parentNode.offsetWidth;
					var divTextWidth=document.getElementById("text").parentNode.offsetWidth;
					var divHeight=document.getElementById("sizepaper").parentNode.offsetHeight;
					//console.log("Beite Hoehe: "+divWidth+" "+divHeight);
                    var idList = new Array(); // Linkverbindungen
                    var graph = new joint.dia.Graph();
                    var uml = joint.shapes.uml;
					var erd = joint.shapes.erd;
										
                    var paper = new joint.dia.Paper({
                        el: $('#paper'),
						width: 0.7*window.screen.availWidth,
                        height: 0.7*window.screen.availHeight,
                        gridSize: 1,
                        model: graph
                    });
		
                    paper.on('cell:pointerclick',
    						//document.body.style.cursor = "copy"
                            function(cellView, evt, x, y) {
    							//Klassen l&ouml;schen
    							if(document.getElementById(5).value == "on"){
    									idList.push(cellView.model.id);
    									graph.getCell(idList[0]).remove();
    									idList=[];
    							// Methoden hinzuf&uuml;gen
    							}else if(document.getElementById(6).value == "on"){
    									idList=[];
    									idList.push(cellView.model.id);
    									var input = window.prompt("Bitte geben sie die Methode an, welche hinzugf&uuml;gt werden soll");
    									var old = graph.getCell(idList[0]).attr('.uml-class-methods-text/text');
    									var arr = old.split("\n");
    									arr.push(input);
    									var text = "";
    									for (i = 0; i < arr.length-1; i++) { 
    										text += arr[i] + "\n";
    									}
    									text += arr[arr.length-1];
    									graph.getCell(idList[0]).attr('.uml-class-methods-text/text', text);
    									graph.getCell(idList[0]).attributes.methods.push(input);
    									idList=[];
    									// Attribute hinzuf&uuml;gen		
    							}else if(document.getElementById(7).value == "on"){		
    									idList=[];
    									idList.push(cellView.model.id);
    									var input = window.prompt("Bitte geben sie das Attribut an, welche hinzugf&uuml;gt werden soll");
    									var old = graph.getCell(idList[0]).attr('.uml-class-attrs-text/text');
    									//console.log(graph.getCell(idList[0]).attributes);
    									//console.log(graph.getCell(idList[0]).attributes.methods);
    									//console.log(graph.getCell(idList[0]).attributes.attributes);
    									//console.log(graph.getCell(idList[0]).attributes.name);
    									var arr = old.split("\n");
    									arr.push(input);
    									var text = "";
    									for (i = 0; i < arr.length-1; i++) { 
    										text += arr[i] + "\n";
    									}
    									text += arr[arr.length-1];
    									graph.getCell(idList[0]).attr('.uml-class-attrs-text/text', text);
    									graph.getCell(idList[0]).attributes.attributes.push(input);
    									idList=[];	
    							// Methoden entfernen		
    							}else if(document.getElementById(8).value == "on"){

    									idList=[];
    									idList.push(cellView.model.id);
    									var input = window.prompt("Bitte geben sie die Methode an, welche entfernt werden soll");
    									input= input-1;
    									console.log("input: "+input);
    									var old = graph.getCell(idList[0]).attr('.uml-class-methods-text/text');
    									var arr = old.split("\n");
    									console.log("Methoden: "+ old);
    									if(-1 < input && input <= arr.length-1 ){
    										arr.splice(input,1);
    										var text = "";
    										if(arr.length > 0){
    											for (i = 0; i < arr.length-1; i++) { 
    											text += arr[i] + "\n";
    											}
    											text += arr[arr.length-1];
    										}
    										graph.getCell(idList[0]).attr('.uml-class-methods-text/text', text);
    										graph.getCell(idList[0]).attributes.methods.splice(input,1);
    									}else{
    										window.alert("Index ist zu gro� oder zu klein");	
    									}
    							// Attribute entfernen		
    							}else if(document.getElementById(9).value == "on"){
    							idList=[];
    									idList.push(cellView.model.id);
    									var input = window.prompt("Bitte geben sie das Attribut an, welches entfernt werden soll");
    									input= input-1;
    									console.log("input: "+input);
    									var old = graph.getCell(idList[0]).attr('.uml-class-attrs-text/text');
    								
    									var arr = old.split("\n");
    									if(-1 < input & input <= arr.length-1 ){
    										arr.splice(input,1);
    										var text = "";
    										if(arr.length > 0){
    											for (i = 0; i < arr.length-1; i++) { 
    												text += arr[i] + "\n";
    											}
    											text += arr[arr.length-1];
    										}
    										graph.getCell(idList[0]).attr('.uml-class-attrs-text/text', text);
    										console.log(graph.getCell(idList[0]).attr('.uml-class-methods-text/text', text));
    										graph.getCell(idList[0]).attributes.attributes.splice(input,1);
    									}else{
    										window.alert("Index ist zu gro� oder zu klein");	
    									}
    							}else{
    								if (idList.length < 2) {
    									idList.push(cellView.model.id);
    								}
    								if (idList.length == 2) {
    									console.log("0: "+idList[0]+" 1: "+idList[1]);
    									if(idList[0]!=idList[1]){
    										link();
    									}else{
    										console.log("gleich");
    										idList=[];
    									}
    								}						
    							}
                            }
                        );
    					
    					function allowDrop(ev) {
    						ev.preventDefault();
    					}
    					function drag(ev) {
    						document.getElementById(5).value = "off";
    						if( ev.target.getAttribute('data-baseform') != null){
    							ev.dataTransfer.setData("text", ev.target.getAttribute('data-baseform'));
    						}else{
    							ev.dataTransfer.setData("text", ev.target.innerHTML);		
    						}
    					}
    					function drop(ev) {
    						ev.preventDefault();
    						var data = ev.dataTransfer.getData("text");
    						addClass(data);
    					}

var sel;
function selectButton(elem){
	for(i=1; i<11; i++){
	console.log("id: "+i);
	document.getElementById(i).value="off";
	}
	document.getElementById(elem.id).value="on";
	sel=elem.id;
}
													
function link() {
	if(document.getElementById(sel).value="on"){
		var source_name = graph.getCell(idList[0]).attr('.uml-class-name-text/text');
		var destin_name = graph.getCell(idList[1]).attr('.uml-class-name-text/text');
		var source_mult = window.prompt("Bitte geben Sie die Multiplizität von "+source_name+" nach " + destin_name+" an.");
		var destin_mult = window.prompt("Bitte geben Sie die Multiplizität von "+destin_name+" nach " + source_name+" an.");
		switch (sel) {
			case '1':
				graph.addCell(new uml.Composition({
					source: {
					id: idList[0]
					},
                                        target: {
                                            id: idList[1]
                                        },
										labels: [{ position: 25, attrs: { text: { text: source_mult } }},
										         { position: -25, attrs: { text: { text: destin_mult } }}
									]
                                    }));
                                    idList = [];
                                    break;
                                case '2':
                                    graph.addCell(new uml.Aggregation({
                                        source: {
                                            id: idList[0]
                                        },
                                        target: {
                                            id: idList[1]
                                        },
										labels: [{ position: 25, attrs: { text: { text: source_mult } }},
										         { position: -25, attrs: { text: { text: destin_mult } }}
									]
                                    }));
                                    idList = [];
                                    break;
                                case '3':
                                    graph.addCell(new uml.Implementation({
                                        source: {
                                            id: idList[0]
                                        },
                                        target: {
                                            id: idList[1]
                                       },
										labels: [{ position: 25, attrs: { text: { text: source_mult } }},
										         { position: -25, attrs: { text: { text: destin_mult } }}
									]
                                    }));
                                    idList = [];
                                    break;
                                case '4':
                                    graph.addCell(new uml.Generalization({
                                        source: {
                                            id: idList[0]
                                        },
                                        target: {
                                            id: idList[1]
                                        },
										labels: [{ position: 25, attrs: { text: { text: source_mult } }},
										         { position: -25, attrs: { text: { text: destin_mult } }}
									]
                                    }));
                                    idList = [];
                                    break;
									
								case '10':
                                    graph.addCell(new joint.dia.Link({
                                        source: {
                                            id: idList[0]
                                        },
                                        target: {
                                            id: idList[1]
                                        },
										labels: [{ position: 25, attrs: { text: { text: source_mult } }},
										         { position: -25, attrs: { text: { text: destin_mult } }}
									]
                                    }));
                                    idList = [];
                                    break;
								default:
								    idList = [];
                                    break;
		}
	}
}

function loeschen(){
	selectButton(document.getElementById(5));
}

function addMeth(){
	selectButton(document.getElementById(6));
}

function addAttr(){
	selectButton(document.getElementById(7));			
}

function delMeth(){
	selectButton(document.getElementById(8));
}

function delAttr(){
	selectButton(document.getElementById(9));
}



function addClass(data) {
                        flugzeug: var flug = new uml.Class({
                            position: {
                                x: Math.random()*250,
                                y: Math.random()*250
                            },
                            size: {
                                width: 140,
                                height: 140
                            },
                            name: data,
                            attributes: ["",""],
                            methods: ["","" ],
                            attrs: {
                                '.uml-class-name-rect': {
                                    fill: '#ffffff',
                                },
                                '.uml-class-attrs-rect, .uml-class-methods-rect': {
                                    fill: '#ffffff',
                                },
                                '.uml-class-attrs-text': {
                                    ref: '.uml-class-attrs-rect',
                                    'ref-y': 0.5,
                                    'y-alignment': 'middle'
                                },
                                '.uml-class-methods-text': {
                                    ref: '.uml-class-methods-rect',
                                    'ref-y': 0.5,
                                    'y-alignment': 'middle'
                                }
                            }
                        });
                        graph.addCell(flug);
                    }
/*
function addClass(name,posx,posy,a1,a2,m1,m2) {
                        flugzeug: var flug = new uml.Class({
                            position: {
                                x: posx,
                                y: posy
                            },
                            size: {
                                width: 140,
                                height: 140
                            },
                            name: name,    
                            attributes: [ a1,a2 ],
                            methods: [ m1,m2 ],
                            attrs: {
                                '.uml-class-name-rect': {
                                    fill: '#ffffff',
                                },
                                '.uml-class-attrs-rect, .uml-class-methods-rect': {
                                    fill: '#ffffff',
                                },
                                '.uml-class-attrs-text': {
                                    ref: '.uml-class-attrs-rect',
                                    'ref-y': 0.5,
                                    'y-alignment': 'bottom'
                                },
                                '.uml-class-methods-text': {
                                    ref: '.uml-class-methods-rect',
                                    'ref-y': 0.5,
                                    'y-alignment': 'bottom'
                                }
                            }
                        });
                        graph.addCell(flug);
                    }
 var x = 485;
 var y = 10; 

addClass("Telekonverter",x,y,"Verlängerungsfaktor:Zahl","","","");
addClass("Kameragehäuse",x,y+180,"","","","");
addClass("Profigehäuse",x-300,y+180,"","","","");
addClass("Profiblitz",x-300,y+360,"","","","");
addClass("Amateurgehäuse",x+300,y+180,"","","","");
addClass("Amateurblitz",x+300,y+360,"","","","");
addClass("Objektiv",x,y+360,"Gewindedurchmesser:Zahl","","","");
addClass("Sonnenblende",x-300,y+540,"Gewindedurchmesser:Zahl","","","");
addClass("Festbrennweitenobjektiv",x+300,y+540,"Brennweite:Zahl","","","");
addClass("Zoomobjektiv",x,y+540,"Brennweite_maximal:Zahl","Brennweite_minimal:Zahl","","");
*/
// Namen aller Klassen
function getClasses(){
	var classes=[];
	for(i=0; i<graph.getCells().length; i++){
		if(graph.getCells()[i]._previousAttributes.name != undefined){
			classes.push(graph.getCells()[i]._previousAttributes.name);
		}
	}
	return classes;
	//console.log("getClasses: "+ classes);
}
// Alle ids aller Zellen
function getIds(){
	var ids=[];
	for(i=0; i<graph.getCells().length; i++){
	 ids.push(graph.getCells()[i].id);
	}
	return ids;
}
function getAttributes(id){
	var text ="";
	if(graph.getCell(id).attributes.name != undefined){
		var text=graph.getCell(id).attributes.name;
		for(i=0; i < graph.getCell(id).attributes.attributes.length; i++){
			if(graph.getCells()[i]._previousAttributes.name != undefined){
				text+="_"+graph.getCell(id).attributes.attributes[i];
			}
		}
						//console.log("getAttributes: "+text);
	}
	return text;
}
function getMethodes(id){
	var text=graph.getCell(id).attributes.name;
	for(i=0; i < graph.getCell(id).attributes.methods.length; i++){
		if(graph.getCells()[i]._previousAttributes.name != undefined){
			text+="_"+graph.getCell(id).attributes.methods[i];
		}
	}
	return text;
}
