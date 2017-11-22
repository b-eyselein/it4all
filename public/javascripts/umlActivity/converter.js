var list_path;
var code;
var list_nameOfCorrectChangingElements = ["manual_ifend", "manual_ifstart", "manual_loopstart", "manual_loopendct", "manual_loopendcf"];
var graphcopy;  //all Elements (no links)
var jsonMerge = {};  //merged elements 
var jsongraph; // saving and loading graph
var list_successors // startnode successors
var lang; //current used Language
const newLine = /\n/; //for getDataFromElement split(const)

//python contents
class Python{

	constructor(deep) {
		this.deep = parseInt(deep);
	}
	
	get_core(startnode_inputtype,startnode_input,endnode_outputtype,endnode_output,methodname,content){
		return "def "+methodname+"("+startnode_input+"):\n"+content+" return "+endnode_output;
	}

	get_if(econdition,ethen,eelse,deep){
		return " ".repeat(deep)+"if "+econdition+":\n"+" ".repeat(deep)+ethen+"\n"+" ".repeat(deep)+"else:\n"+" ".repeat(deep)+eelse+"\n";
	}

	get_loop(econdition,path,content,deep){
		return path+"\n"+" ".repeat(deep)+"while"+econdition+":\n"+content;
	}

	get_efor(eelement,collection,content,deep){
		return " ".repeat(deep)+"for "+eelement+" in "+collection+":\n"+content+"\n";
	}

	get_edw(econdition,content,deep){
		return " ".repeat(deep)+"while True:\n"+" ".repeat(deep)+content+"\n"+" ".repeat(deep)+"if "+econdition+":\n"+" ".repeat(deep)+"break\n";
	}

	get_ewd(econdition,content,deep){
		return " ".repeat(deep)+"while "+econdition+":\n"+content+"\n";
	}

	get_manualIf(econdition,left,right,deep){
		return " ".repeat(deep-1)+"if "+econdition+":\n"+" ".repeat(deep-1)+left+"\n"+" ".repeat(deep-1)+"else:\n"+" ".repeat(deep-1)+right+"\n";
	}

	get_manualLoop(econdition,path2,comb,deep){
		return path2+"\n"+" ".repeat(deep-1)+"while"+econdition+":\n"+comb;
	}
		
	set_increaseDeep(value){
		this.deep = this.deep + parseInt(value);
	}
	
	
}

// java contents
class Java{
	
	constructor(deep) {
		this.deep = parseInt(deep);
	}
	
	get_core(startnode_inputtype,startnode_input,endnode_outputtype,endnode_output,methodname,content){
		return "public "+endnode_outputtype+" "+methodname+"("+startnode_inputtype+" "+startnode_input+")\n{\n"+content+"return "+endnode_output+";\n}";
	}

	get_if(econdition,ethen,eelse,deep){
		return " ".repeat(deep)+"if("+econdition+"){\n"+ethen+"\n"+" ".repeat(deep)+"}else{\n"+eelse+"\n"+" ".repeat(deep)+"}\n";
	}

	get_loop(econdition,path,content,deep){
		return path+" ".repeat(deep)+"while(!("+econdition+")){\n"+content+"}";
	}

	get_efor(eelement,collection,content,deep){
		return " ".repeat(deep)+"for("+eelement+":"+collection+"){\n"+content+"\n"+" ".repeat(deep)+"}\n";
	}

	get_edw(econdition,content,deep){
		return " ".repeat(deep)+"do{\n"+content+"\n"+" ".repeat(deep)+"}\n"+" ".repeat(deep)+"while("+econdition+");\n";
	}

	get_ewd(econdition,content,deep){
		return " ".repeat(deep)+"while("+econdition+"){\n"+content+"\n"+" ".repeat(deep)+"}\n";
	}

	get_manualIf(econdition,left,right,deep){
		return " ".repeat(deep-1)+"if("+econdition+"){\n"+left+"\n"+" ".repeat(deep-1)+"}else{\n"+right+"\n"+" ".repeat(deep-1)+"}\n";
	}

	get_manualLoop(econdition,path2,comb,deep){
		return path2+" ".repeat(deep-1)+"while(!("+econdition+")){\n"+comb+"}\n";
	}
	
	set_increaseDeep(value){
		this.deep = this.deep + parseInt(value);
	}
	
}
// TESTING STUFF
var log = []; // hints and feedback from testcases
var list_successors = []; // using only for testing scenario

//MAIN Function
function generateCode() {
	log = [];
	//Testcases
		test_isEndnodeAccessible();
		test_forbidOutboundConForEnd();
		test_noUnknownElements(list_successors);
		test_alternativeEnds();
		test_disconnectedElements();
		test_conditionOfMergeelements();
		test_CnrStartnodeEqualsEndnode();
		//test_atLeastOneElementinMerge();
	//Generating Code, if no errors occured
	if(log.length === 0){
		try{
			document.getElementById("preCode").removeChild(document.getElementById("list_error"));
		}catch(e){}
		//Define: complete graph with Merged elements in a row
		var json_completeGraph = {};
		//Define: use mergecontent instead of regular element content
		var list_openingsMerge=["manual_ifstart", "manual_loopstart"];
		//Build: Mergeelements
		graphcopy=graph.getElements();
		mergeSplittingElements(graphcopy);
		console.log(graphcopy);
		var node = graph.getSuccessors(graph.getCell("startid"))["0"];
		var cnr = 0;
		while(node.attributes.name !== "end"){
			if(list_openingsMerge.includes(node.attributes.name)){
				// ADD: Data
				json_completeGraph[cnr] =jsonMerge[node.attributes.id];
				// DEFINE: next element
				node = graph.getCell(json_completeGraph[cnr].targetId);
			}else{
				if(node.attributes.name == "manual_loopendcf" || node.attributes.name == "manual_loopendct" || node.attributes.name == "manual_ifend"){
					var startIds_loop = graph.getConnectedLinks(graph.getCell(node.attributes.id), {outbound: true});
					var startId_loop;
					/*
						two elements --> loop with condition false or true
						one element --> manual if --> next element is on top or bot side
					*/
					if(!(startIds_loop.length>1)){
						startId_loop = startIds_loop["0"].attributes.target.id;
					}else{
						if(startIds_loop["0"].attributes.source.port == "bot"){
							startId_loop = startIds_loop["0"].attributes.target.id;
						}else{
							startId_loop = startIds_loop["1"].attributes.target.id;
						}
					}
					
					//console.log(startIds_loop);

					try{
						node = graph.getCell(graph.getCell(startId_loop));	
					}catch(e){
						node = graph.getCell(graph.getCell(startId_loop).attributes.id);	
					}
					cnr--;	
				}else{
					json_completeGraph[cnr] = getDataFromElement(graph.getCell(node.attributes.id).attributes);	
					node = graph.getCell(graph.getSuccessors(node)["0"]);
				}
			}
			//console.log("node name:");
			//console.log(node.attributes.name);
			cnr++;
		}
		console.log("completeGraph:");
		console.log(json_completeGraph);
		//JSON.stringify(json_completeGraph);
		var selected_language = $("#sel_lang").val();;
		convert_JsonToProgrammCode(json_completeGraph,selected_language);		
	}else{
		document.getElementById("preCode").innerHTML = "";
		log = preparelog(log);
		try{
			document.getElementById("preCode").removeChild(document.getElementById("list_error"));
		}catch(e){}
		document.getElementById("preCode").appendChild(log);
	}
}


//TEST: if a merge element is open --> must be close with end tag
function test_CnrStartnodeEqualsEndnode(){
var eif = 0;
var eloop = 0;
for( var i = 0; i<graphcopy.length; i++){
	switch (graphcopy[i].attributes.name){
		case "manual_ifstart":
			eif++;
			break;
		case "manual_ifend":
			eif--;
			break;
		case "manual_loopstart":
			eloop++;
			break;
		case "manual_loopendct":
			eloop--;
			break;
		case "manual_loopendcf":
			eloop--;
			break;
	}
}
if(eloop > 0){
	log.push("Das Verzweigungselement Schleife wurde nicht korrekt abgeschlossen");
}else if(eloop < 0){
	log.push("Das Verzweigungselement Schleife wurde nicht korrekt begonnen");
}
if(eif > 0){
	log.push("Das Verzweigungselement Bedingung wurde nicht korrekt abgeschlossen");
}else if(eif < 0){
	log.push("Das Verzweigungselement Bedingung wurde nicht korrekt begonnen");
}
}

//TEST: path to end possible   (beginnning via startpoint)
function test_isEndnodeAccessible(){
	list_successors = graph.getSuccessors(graph.getCell("startid"));
	if(typeof list_successors[list_successors.length-1] !== 'undefined'){
		for(var i = list_successors.length-1; i>-1;i--){
			if(list_successors[i].attributes.name === "end"){
				return;
			}
		}
	}
	log.push("Der Endknoten kann nicht erreicht werden.");
}

//TEST: test if manual_loopstart and manual_ifstart have at least one element
function test_atLeastOneElementinMerge(){
	var list_namesOfIncorrectElements = ["manual_ifstart","manual_loopstart"];
	graphcopy = graph.getCells();
	for(var i = 0; i< graphcopy.length; i++){
		if(graphcopy[i].attributes.name === "manual_ifstart"){
			var startids = graph.getConnectedLinks(graph.getCell(graphcopy[i].attributes.id), {outbound: true});				
			var stopwords = ["manual_ifend"];
			var cnr = 0;
			for(var i = 0; i < startids.length; i++){
				var node = graph.getCell(startids[i].attributes.target.id);
				if(stopwords.includes(node.attributes.name)){
					cnr++;
				}
			}
			if(cnr == 2){
				log.push("Ein Verzweigungselement enth\u00e4lt keine Elemente au\u00DFer das Ende des Verzweigungselements");
			}
		}
		if(graphcopy[i].attributes.name === "manual_loopstart"){
			var cnr2 = 0;
			stopwords = ["manual_loopendcf","manual_loopendct"];
			//Define: only 1 outgoing link on botside
			var startId_path = graph.getConnectedLinks(graph.getCell(graphcopy[i].attributes.id), {outbound: true})["0"].attributes.target.id;
			var node = graph.getCell(startId_path);
			if(stopwords.includes(node.attributes.name)){
				cnr2++;
			}
			while(!stopwords.includes(node.attributes.name)){
				if(list_namesOfIncorrectElements.includes(node.attributes.name)){
					if(!jsonMerge.hasOwnProperty(node.attributes.id)){
						return false;
					}else{
						node = graph.getCell(jsonMerge[node.attributes.id].targetId);
					}	
				}else{
					node = graph.getSuccessors(graph.getCell(node.attributes.id))["0"];
				}
			}
			var loopid = node.attributes.id; 
			var startIds_loop = graph.getConnectedLinks(graph.getCell(loopid), {outbound: true});
			//Define: Port Right or Left
			var startId_loop;
			if(startIds_loop["0"].attributes.source.port == "bot"  || startIds_loop["0"].attributes.source.port == "top" ){
				startId_loop = startIds_loop["1"].attributes.target.id;
			}else{
				startId_loop = startIds_loop["0"].attributes.target.id;	
			}
			node = graph.getCell(startId_loop);
			stopwords = ["manual_loopstart"];
			if(stopwords.includes(node.attributes.name)){
				cnr2++;
			}
			if(cnr2 == 2){
				log.push("Ein Verzweigungselement enth\u00e4lt keine Elemente au\u00DFer das Ende des Verzweigungselements");
			}
		}		
	}	
}

//Test: outbound connections from endnode are restricted
function test_forbidOutboundConForEnd(){
	if(graph.getConnectedLinks(graph.getCell("endid"), {outbound: true}).length > 0){
		log.push("Der Endknoten darf keine ausgehenden Verbindungen enthalten.");
	} 
}

//TEST: graph doesnt contains unknown elements
function test_noUnknownElements(list_successors){
	for (i = 0; i < list_successors.length; i++) {
		if (list_successors[i].attributes.name === "unknown") {
			log.push("Der Graph enth\u00e4lt Verzweigungselemente, welche nicht korrekt verbunden sind.");
			return;
		}
	}
}
	
//TEST: alternative Endpoints through not connected elements
function test_alternativeEnds() {
    var sinks = graph.getSinks();
	var string=[];
    if (sinks.length > 1) {
		for(var i = 0; i<sinks.length;i++){
		if(sinks[i].attributes.name == "end"){
				delete sinks[i];
			}else{
				string.push(sinks[i].attributes.cleanname);
			}
		}

		if(string.length>1){
			string = "Die Elemente "+string.toString()+" stellen Endpunkte bzw. Senke dar. (Endknoten exklusiv)";
		}else{
			string = "Das Element "+string.toString()+" stellt einen Endpunkt bzw. eine Senke dar. (Endknoten exklusiv)";
		}
        
        log.push(string);
    }
}

// Anzahl der unverbundenen Knoten vom Start aus ( -1 um den Startknoten zu ignorieren)
function test_disconnectedElements() {
    try {
        if(graph.getElements().length - 1 - graph.getSuccessors(getNodeById("startid")).length > 0){
			log.push("Es existieren unverbundene Elemente auf der Zeichenfl\u00e4che.");
		}
    } catch (e) {
    }
}

//manuell loop start darf keine Bedingung enthalten!
function test_conditionOfMergeelements() {
	graphcopy = graph.getCells();
	for(var i = 0; i< graphcopy.length; i++){
		if(graphcopy[i].attributes.name === "manual_ifstart" || graphcopy[i].attributes.name === "manual_loopendcf" || graphcopy[i].attributes.name === "manual_loopendct" ){
			if(graphcopy[i].attributes.einput === ""){
				var desc = "";
				if(graphcopy[i].attributes.name === "manual_ifstart" || graphcopy[i].attributes.name === "manual_loopendcf" || graphcopy[i].attributes.name === "manual_loopendct" ){
					desc = "If-Verzweigung";
				}else{
					desc = "Schleifen-Verzweigung";
				}
				log.push("Ein "+graphcopy[i].attributes.cleanname+" ent\u00e4lt keine Bedingung, obwohl es als "+desc+" erkannt wurde.");
			}
		}else{
			if(graphcopy[i].attributes.name === "manual_ifend" || graphcopy[i].attributes.name === "manual_loopstart"){
				if(graphcopy[i].attributes.einput !== ""){
				var desc = "";
				if(graphcopy[i].attributes.name === "manual_ifend"){
					desc = "Ende einer If-Verzweigung";
				}else{
					desc = "Anfang einer Schleifen-Verzweigung";
				}
				log.push("Ein "+graphcopy[i].attributes.cleanname+" ent\u00e4lt enhält eine Bedingung, obwohl es als "+desc+" erkannt wurde.");
			}
			}
		}		
	}
}

//Return: Array of all connected Links
function getLinksById(id) {
    return graph.getConnectedLinks(graph.getCell(id));
}

//return jSON with pseudocode   node = starting node,  type = Bezeichnung des mergeknotens
function connectElements(node_input, type) {
    /*
Define Stop ELEMENT:
manual_loopstart --> manual_loopendcf  || manual_loopendct
manual_ifstart --> manual_ifend
*/
	var stopwords =[];
    switch (type) {
        case "manual_loopstart":
			stopwords =["manual_loopendcf","manual_loopendct"];
            break;
        case "manual_ifstart":
			stopwords =["manual_ifend"];
            break;
        case "manual_loopendcf":
			stopwords =["manual_loopstart"];
            break;			
        case "manual_loopendct":
			stopwords =["manual_loopstart"];
            break;								
        default:
            stopwords =["end"];
    }	
	var jsonCode = {};
	var i = 0;
	var node = node_input;
	while(!stopwords.includes(node.attributes.name)){
		var target;	
		 if(jsonMerge.hasOwnProperty(node.attributes.id)){
			jsonCode[i] = jsonMerge[node.attributes.id];
			target = jsonCode[i].targetId;
			if(node.attributes.name === "manual_loopstart"){
				var startIds_loop = graph.getConnectedLinks(graph.getCell(jsonMerge[node.attributes.id].targetId), {outbound: true});
				if(startIds_loop["0"].attributes.source.port == "bot"){
					startId_loop = startIds_loop["0"].attributes.target.id;	
				}else{
					startId_loop = startIds_loop["1"].attributes.target.id;
				}
				node = graph.getCell(startId_loop);
			}else{
			node = graph.getSuccessors(graph.getCell(target))["0"];	
			}
		}else{
			//node is 
		    jsonCode[i] = getDataFromElement(node.attributes);
			target = node.attributes.id;
			node = graph.getSuccessors(graph.getCell(target))["0"];
		}
		i++;
	}
    //console.log(jsonCode);
    return jsonCode;

}
//Return: amount of mergedElements (startelements)
function getNumberOfMergeElements(graphcopy){
	var cnr=0;
	for (var i = 0; i < graphcopy.length; i++) {
        if (graphcopy[i].attributes.name == "manual_ifstart" || graphcopy[i].attributes.name == "manual_loopstart" ) {
			cnr++;
		}
	}
	return cnr;
}

/*
Returns Boolean 
true: no other mergestart elements or already merged
*/
function isPathclean(id){
	var list_namesOfIncorrectElements = ["manual_ifstart","manual_loopstart"];
	var stopwords=[];
	switch(graph.getCell(id).attributes.name){
		//Note: loopstart only needs bot port ( one outgoing port )
		case "manual_loopstart":
			stopwords = ["manual_loopendcf","manual_loopendct"];
			//Define: only 1 outgoing link on botside
			var startId_path = graph.getConnectedLinks(graph.getCell(id), {outbound: true})["0"].attributes.target.id;
			var node = graph.getCell(startId_path);
			while(!stopwords.includes(node.attributes.name)){
				if(list_namesOfIncorrectElements.includes(node.attributes.name)){
					if(!jsonMerge.hasOwnProperty(node.attributes.id)){
						return false;
					}else{
						node = graph.getCell(jsonMerge[node.attributes.id].targetId);
					}	
				}else{
					node = graph.getSuccessors(graph.getCell(node.attributes.id))["0"];
				}
			}
			var loopid = node.attributes.id; 
			var startIds_loop = graph.getConnectedLinks(graph.getCell(loopid), {outbound: true});
			//Define: Port Right or Left
			var startId_loop;
			if(startIds_loop["0"].attributes.source.port == "bot"  || startIds_loop["0"].attributes.source.port == "top" ){
				startId_loop = startIds_loop["1"].attributes.target.id;
			}else{
				startId_loop = startIds_loop["0"].attributes.target.id;	
			}
			node = graph.getCell(startId_loop);
			stopwords = ["manual_loopstart"];
			while(!stopwords.includes(node.attributes.name)){
				if(list_namesOfIncorrectElements.includes(node.attributes.name)){
					if(!jsonMerge.hasOwnProperty(node.attributes.id)){
						return false;
					}else{
						if(node.attributes.name == "manual_loopstart"){
							var startIds_loop = graph.getConnectedLinks(graph.getCell(jsonMerge[node.attributes.id].targetId), {outbound: true});
							if(startIds_loop["0"].attributes.source.port == "bot" || startIds_loop["0"].attributes.source.port == "top"){
								startId_loop = startIds_loop["0"].attributes.target.id;
							}else{
								startId_loop = startIds_loop["1"].attributes.target.id;
							}
							node = graph.getSuccessors(graph.getCell(startId_loop))["0"];
						}else{						
						node = graph.getCell(jsonMerge[node.attributes.id].targetId);
						}
					}	
				}else{
					node = graph.getSuccessors(graph.getCell(node.attributes.id))["0"];
				}
			}
			return true;

		case "manual_ifstart":
			//Define: outgoing ports
			var startids = graph.getConnectedLinks(graph.getCell(id), {outbound: true});				
			var stopwords = ["manual_ifend"];
			for(var i = 0; i < startids.length; i++){
				var node = graph.getCell(startids[i].attributes.target.id);
				while(!stopwords.includes(node.attributes.name)){
					if(list_namesOfIncorrectElements.includes(node.attributes.name)){
						if(!jsonMerge.hasOwnProperty(node.attributes.id)){
							return false;
						}else{
							if(node.attributes.name == "manual_loopstart"){
								
								var startIds_loop = graph.getConnectedLinks(graph.getCell(jsonMerge[node.attributes.id].targetId), {outbound: true});
								if(startIds_loop["0"].attributes.source.port == "bot" || startIds_loop["0"].attributes.source.port == "top"){
									startId_loop = startIds_loop["0"].attributes.target.id;
								}else{
									startId_loop = startIds_loop["1"].attributes.target.id;
								}
								node = graph.getCell(startId_loop);
							}else{
								node = graph.getSuccessors(graph.getCell(jsonMerge[node.attributes.id].targetId))["0"];
							}
						}	
					}else{
						node = graph.getSuccessors(graph.getCell(node.attributes.id))["0"];
					}
				}
			}
			return true;
		default:
			//console.log("pathclean nicht möglich");
			//console.log(graph.getCell(id).attributes.name);
	}
}

//Returns JsonFile with all merged elements and contents
function mergeSplittingElements(graphcopy) {	
	/*
jsonMerge
{
sourceId:[]
}


json_element
{
"name": "",				Elementname of sourcepoint
 "sourceId":"",			ElementID of sourcepoint
"targetId":"",			ElementID of targetpoint
 "content": []			Content of specific mergeelement 
}

*/
var list_namesOfIncorrectElements = ["manual_ifstart","manual_loopstart"];
    //Define: Array of all Merging Elements
    jsonMerge = {};
	//Define: Counter for Mergeelements
	var cnr_mergeElements=getNumberOfMergeElements(graphcopy);
    //console.log(graphcopy);
	//While: any merged element isnt converted in json
	while(cnr_mergeElements > 0){
		//For: each element of graph
		for (var i = 0; i < graphcopy.length; i++) {
			var startelement = graphcopy[i].attributes.id;
			//If: elementtype belongs to mergetype
			if (list_nameOfCorrectChangingElements.includes(graphcopy[i].attributes.name)) {
				if(jsonMerge.hasOwnProperty(graphcopy[i].attributes.id)){
					continue;	
				}
				if(isPathclean(graphcopy[i].attributes.id)){
					//Define: all links of mergeelement
					var list_links = getLinksById(startelement);
					//Define: structure of an entry 
					var json_element={
						"name": "",
						"sourceId":"",
						"targetId":"",
						"content": [
						]
					}
					//Define: sourceId for jsonMerge
					var startelement = graphcopy[i].attributes.id;			
					//Define: startpoint bye elementype for search
					switch (graphcopy[i].attributes.name) {
						//Note: loopstart only needs bot port ( one outgoing port )
						case "manual_loopstart":
							//Define: only 1 outgoing link on botside
							var startId_path = graph.getConnectedLinks(graph.getCell(startelement), {outbound: true})["0"].attributes.target.id;
							//Define: json content Path from mergesource to condition
							
							//Define: Mergestart --> name and sourceId
							json_element.name="manual_loopstart";
							json_element.sourceId=startelement;
							json_element["content"].path=connectElements(graph.getCell(startId_path), "manual_loopstart");
							//Test: No elements on path
							if(Object.entries(json_element.content.path).length != 0){
								if(list_namesOfIncorrectElements.includes(json_element.content.path[Object.keys(json_element.content.path).length-1].name)){
									var node = graph.getCell(json_element.content.path[Object.keys(json_element.content.path).length-1].targetId)
									json_element.targetId = graph.getSuccessors(node)["0"].attributes.id;
								}else{
									json_element.targetId=graph.getSuccessors(graph.getCell(json_element.content.path[Object.keys(json_element.content.path).length-1].id))["0"].attributes.id;
								}
							}
							if(json_element.targetId == ""){
								json_element.targetId=graph.getSuccessors(graph.getCell(json_element.sourceId))["0"].attributes.id;
							}						
							/*
							
							if(json_element.content.path != null){
								//Take last element from path
								if(json_element.content.path[Object.keys(json_element.content.path).length-1].hasOwnProperty("sourceId")){
									var lastelement = json_element.content.path[Object.keys(json_element.content.path).length-1];
									json_element.targetId=graph.getSuccessors(graph.getCell(lastelement.targetId))["0"].attributes.id;
								}else{
									json_element.targetId=graph.getSuccessors(json_element.content.path[Object.keys(json_element.content.path).length-1])["0"].attributes.id;
								}
							}else{
								json_element.targetId=graph.getSuccessors(graph.getCell(json_element.sourceId))["0"].attributes.id;
							}
							*/
							json_element["content"].condition=graph.getCell(json_element.targetId).attributes.einput;
							//Define: StartId of endelement
							var startIds_loop = graph.getConnectedLinks(graph.getCell(json_element.targetId), {outbound: true});
							//Define: Port Right or Left
							var startId_loop;
							console.log(startIds_loop);
							if(startIds_loop["0"].attributes.source.port == "bot" || startIds_loop["0"].attributes.source.port == "top"){
								startId_loop = startIds_loop["1"].attributes.target.id;
								json_element.targetId = startIds_loop["0"].attributes.source.id;
							}else{
								startId_loop = startIds_loop["0"].attributes.target.id;
								json_element.targetId = startIds_loop["1"].attributes.source.id; 								
							}
							//ignoring side of port! condition true or false
							json_element["content"].loop=connectElements(graph.getCell(startId_loop), "manual_loopendcf");
							jsonMerge[json_element.sourceId]=json_element;
							cnr_mergeElements--;
							break;
				
						case "manual_ifstart":

							//Define: outgoing ports
							var startids = graph.getConnectedLinks(graph.getCell(startelement), {outbound: true});
							//Define: branches of true and false
							var branch_true;
							var branch_false;		
							if(startids["0"].attributes.source.port == "left"){
								branch_true = startids["0"].attributes.target.id;
								branch_false = startids[1].attributes.target.id;
							}else{
								branch_true = startids[1].attributes.target.id;
								branch_false = startids["0"].attributes.target.id;
							}
							//tripleIF
							var nodeTrue = graph.getCell(branch_true);
							var nodeFalse = graph.getCell(branch_false);
							//nodeTrue = checkIfBranchesMergeObjects(nodeTrue);
							//nodeFalse = checkIfBranchesMergeObjects(nodeFalse);	
							//Define: Content with JSON element, if there are no Followers undefined
							json_element["content"].true=connectElements(nodeTrue, "manual_ifstart");
							json_element["content"].false=connectElements(nodeFalse, "manual_ifstart");
							json_element["content"].condition=graphcopy[i].attributes.einput;
							//Define: Mergestart --> name and sourceId
							json_element.name="manual_ifstart";
							json_element.sourceId=startelement;
							/*
								Define: TargetId
								Branches can be empty --> undefined
								Test: if true and false have no entry, just add following element of sourceId
							*/
							if(Object.entries(json_element.content.false).length != 0){
								if(list_namesOfIncorrectElements.includes(json_element.content.false[Object.keys(json_element.content.false).length-1].name)){
									json_element.targetId = json_element.content.false[Object.keys(json_element.content.false).length-1].targetId;
								}else{
									json_element.targetId=graph.getSuccessors(graph.getCell(json_element.content.false[Object.keys(json_element.content.false).length-1].id))["0"].attributes.id;
								}
							}
							if(Object.entries(json_element.content.true).length != 0){
								if(list_namesOfIncorrectElements.includes(json_element.content.true[Object.keys(json_element.content.true).length-1].name)){
									json_element.targetId = json_element.content.true[Object.keys(json_element.content.true).length-1].targetId;
								}else{
									json_element.targetId=graph.getSuccessors(graph.getCell(json_element.content.true[Object.keys(json_element.content.true).length-1].id))["0"].attributes.id;								
								}
							}
							if(json_element.targetId == ""){
								json_element.targetId=graph.getSuccessors(graph.getCell(json_element.sourceId))["0"].attributes.id;
							}
							//ADD: JSONmerge
							jsonMerge[json_element.sourceId]=json_element;
							cnr_mergeElements--;
							break;					
					}
				}
			}
		}	
		}
	console.log("JsonMerge:");
	console.log(jsonMerge);
    return jsonMerge;
}

//RETURN last point
function checkIfBranchesMergeObjects(node){
	if(jsonMerge.hasOwnProperty(node.attributes.id)){
		node=graph.getCell(jsonMerge[node.attributes.id].targetId);
		return node;				
	}else{
		return node;
	}
}


// Ausgabe der Datenfelder als Liste mit ID des ELEMENTS 
function getDataFromElement(el) {
    data = {"name": el.name,"id":el.id, "content": []};
    switch (el.name) {
        case "action":
            data["content"].data = el.area.split(newLine);
            break;
        case "forin":
            data["content"].for = el.efor.split(newLine);
            data["content"].in = el.ein.split(newLine);
            data["content"].area = el.area.split(newLine);
            break;
        case "dw":
            data["content"].ewhile = el.ewhile.split(newLine);
            data["content"].edo = el.edo.split(newLine);
            break;
        case "wd":
            data["content"].ewhile = el.ewhile.split(newLine);
            data["content"].edo = el.edo.split(newLine);
            break;
        case "if":
            data["content"].eif = el.eif.split(newLine);
            data["content"].ethen = el.ethen.split(newLine);
            data["content"].eelse = el.eelse.split(newLine);
            break;
        case "manual_loopendcf":
            data["content"].einput = el.einput.split(newLine);
            break;
        case "manual_loopendct":
            data["content"].einput = el.einput.split(newLine);
            break;
		 case "manual_loopstart":
			if(jsonMerge.hasOwnProperty(el.id)){
				data = jsonMerge[el.id];
				break;
			}
			break
        case "manual_ifstart":
			if(jsonMerge.hasOwnProperty(el.id)){
				data = jsonMerge[el.id];
				break;
			}
            data["content"].einput = el.einput.split(newLine);
            break;			
		case "end":
            break;	
		case "start":
            break;				
        default:
            console.log(el);
            console.log("Daten konnten vom Element nicht ausgelesen werden");
    }
    return data;
}

function getNodeById(id) {
    for (i = 0; i < graphcopy.length; i++) {
        if (graphcopy[i].id == id) {
            return graphcopy[i];
        }
    }
    //console.log("Knoten mittels id nicht gefunden");
}

function preparelog(log){
	var list = document.createElement('ul');
    for(var i = 0; i < log.length; i++) {
        var item = document.createElement('li');
        item.appendChild(document.createTextNode(log[i]));
        list.appendChild(item);
    }
	list.id="list_error";
    return list;
}

/*Converting JSON Code in Programming Code
1.Define programming language as ojbect



*/
//Return: String,  Input: graph as json , programminglanguage as String
function convert_JsonToProgrammCode(json_graph,language){
	var sel_langClass;
	switch(language){
		case "java":
			sel_langClass = new Java(0);
			break;
		case "python":
			sel_langClass = new Python(1);
			break;	
		default:
			log.push("Body für Sprache konnte nicht ermittelt werden.");
	}
	var result = buildProgramm(json_graph,sel_langClass);
	var wrap;
	wrap = sel_langClass.get_core(startnode_inputtype,startnode_input,endnode_outputtype,endnode_output,methodname,result);
	try{
		document.getElementById("preCode").removeChild(document.getElementById("list_error"));
	}catch(e){}
	document.getElementById("preCode").innerHTML = wrap;
	//document.getElementById("code").innerHTML = "Es wurden keine Fehler bei der Konvertierung von der Zeichnung zum Programmcode erkannt. Sollten Sie der Meinung sein, dass Ihr Code nicht korrekt ermittelt wurde, senden Sie bitte den gespeicherten Graphen per Email an asdf@asdf.de";
}

function buildProgramm(json,sel_langClass){
	var content="";
	for(var i = 0; i < Object.keys(json).length;i++){
		switch(json[i].name){
			case "manual_ifstart":
				sel_langClass.set_increaseDeep(1);
					console.log(sel_langClass);
				var left = buildProgramm(json[i].content.true,sel_langClass);
				var right = buildProgramm(json[i].content.false,sel_langClass);
				var condition = json[i].content.condition;
				content += sel_langClass.get_manualIf(condition,left,right,sel_langClass.deep);
				sel_langClass.set_increaseDeep(-1);
					console.log(sel_langClass);
				break;
			case "manual_loopstart":
				sel_langClass.set_increaseDeep(1);
				var loop = " ".repeat(sel_langClass.deep-1)+buildProgramm(json[i].content.loop,sel_langClass);
				var path = buildProgramm(json[i].content.path,sel_langClass);
				var condition = json[i].content.condition;
				var comb = loop+" ".repeat(sel_langClass.deep-1)+path;
				var path2 = buildProgramm(json[i].content.path,sel_langClass.deep); //level of while
				content += sel_langClass.get_manualLoop(condition,path2,comb,sel_langClass.deep);
				sel_langClass.set_increaseDeep(-1);
				break;			
			default:
				content += readDataFromLanguage(json[i],sel_langClass);
		}
	}			
	return content;
}

//RETURN: code from a element combind with language , Input: jsonelement,language, deep
function readDataFromLanguage(graphelement,sel_langClass){
	var str="";
	var deep = sel_langClass.deep;
			switch(graphelement.name){
				case "action":
					var content = graphelement.content.data;
					if(content.length > 1){
						str = createMoreLinesText(content,deep)+"\n";
					}else{
						str = " ".repeat(deep)+graphelement.content.data["0"]+"\n";				
					}
					break;
				case "if":
					var eelse = createMoreLinesText(graphelement.content.eelse,deep+1);
					var eif = createMoreLinesText(graphelement.content.eif,0);
					var ethen = createMoreLinesText(graphelement.content.ethen,deep+1);					
					str = sel_langClass.get_if(eif,ethen,eelse,deep);
					break;			
				case "forin":
					var efor = createMoreLinesText(graphelement.content.for,0);
					var ein = createMoreLinesText(graphelement.content.in,0);
					var earea = createMoreLinesText(graphelement.content.area,deep+1);					
					str = sel_langClass.get_efor(efor,ein,earea,deep);
					break;			
				case "dw":
					var edo = createMoreLinesText(graphelement.content.edo,deep+1);
					var ewhile = createMoreLinesText(graphelement.content.ewhile,0);	
					str = sel_langClass.get_edw(ewhile,edo,deep);
					break;			
				case "wd":
					var edo = createMoreLinesText(graphelement.content.edo,deep+1);
					var ewhile = createMoreLinesText(graphelement.content.ewhile,0);
					str = sel_langClass.get_ewd(ewhile,edo,deep);		
					break;			
				default:
					console.log("Es konnte Daten aus dem Graphelement nicht lesen: "+graphelement.name);
			}	
	return str;
}

//RETURN: STRING     INPUT: jsonnode of textlines
// Summs up more lines in contentfields for pre field
function createMoreLinesText(input,deep){
	var text ="";
	if(Array.isArray(input)){
		for(var i = 0; i < input.length; i++){
			if(input[i].length>0){
				text += " ".repeat(deep)+input[i]+"\n";
			}	
		}
		return text.substr(0, text.length-1);
	}else{
		return " ".repeat(deep)+text;
	}
}

//SET: Saveing current Graph
function saveGraph(){
	jsongraph = graph.toJSON();
	console.log(JSON.stringify(jsongraph));
}
