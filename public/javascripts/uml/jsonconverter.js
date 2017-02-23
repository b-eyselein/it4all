function extractParameters(){
	var json = "\{\"classes\":[";
	var ids =[];
	for(i=0; i<graph.getCells().length; i++){
		if(graph.getCells()[i].attributes.type == "uml.Class"){
				 ids.push(graph.getCells()[i]);
				 json+="{\"name\":\""+ids[i].attributes.name+"\",\"methods\":[";
				 var methods=ids[i].attributes.methods;
				 for(j=0; j<methods.length;j++){
					 json+="\""+ids[i].attributes.methods[j]+"\",";
				 }
				 if(json.charAt(json.length-1)==","){
					 json=json.substr(0,json.length-1);
				 }
				json+="],\"attributes\":[";
				 var attributes=ids[i].attributes.attributes;
				 for(j=0; j<attributes.length;j++){
					 json+="\""+ids[i].attributes.attributes[j]+"\",";
				 }
				 if(json.charAt(json.length-1)==","){
					 json=json.substr(0,json.length-1);
				 }
				 json+="]},";
		}
		
	}
	if(json.charAt(json.length-1)==","){
					 json=json.substr(0,json.length-1);
				 }
	json+="],";

				 // Zugriff auf Werte
				// console.log(ids[i].attributes);
				// console.log(ids[i].attributes.name);
				// console.log(ids[i].attributes.methods);
				// console.log(ids[i].attributes.attributes);

// Auflistung der Connections
	var connections=[]; 
	for(i=0; i<graph.getLinks().length; i++){
		//  typ_source-id-name_destin-id-name_source-mult_destin-mult
		 var text = graph.getLinks()[i].attributes.type +"_"+
					graph.getCell(graph.getLinks()[i].attributes.source.id).attr('.uml-class-name-text/text') +"_"+
					graph.getCell(graph.getLinks()[i].attributes.target.id).attr('.uml-class-name-text/text') +"_"+
					graph.getLinks()[i].attributes.labels["0"].attrs.text.text +"_"+
					graph.getLinks()[i].attributes.labels[1].attrs.text.text;
		connections.push(text);
		text="";
	}
	connections.sort();
	var jsonConnections="\"connections\":\{\{";
	jsonConnections=jsonConnections.substr(0,jsonConnections.length-1);
	var agg = 0;
	var com = 0;
	var imp = 0;
	var gen = 0;
	for(i=0; i<connections.length; i++){
		switch(connections[i].substr(4,3)){
			case "Agg":
				agg++;
				break;
			case "Com":
				com++;	
				break;
			case "Imp":
				imp++;
				break;
			case "Gen":
				gen++;	
				break;
		}
	}
	if(agg == 0){
		jsonConnections+="\"aggregation\":[],";
	}else{
		jsonConnections+="\"aggregation\":[";
		for(i=0; i<agg;i++){
		var split =connections[i].split("_");
			jsonConnections+=	"\{\"start\":\""
						+split[1]+
					"\",\"target\":\""
						+split[2]+
					"\",\"mulstart\":\""
						+split[3]+
					"\",\"multarget\":\""
						+split[4]+
					"\"\},";
		}
		jsonConnections=jsonConnections.substr(0,jsonConnections.length-1)+"\],";
	}
	if(com == 0){
		jsonConnections+="\"composition\":[],";
	}else{
		jsonConnections+="\"composition\":[";
		for(i=0; i<com;i++){
		var split =connections[i].split("_");
			jsonConnections+=	"\{\"start\":\""
						+split[1]+
					"\",\"target\":\""
						+split[2]+
					"\",\"mulstart\":\""
						+split[3]+
					"\",\"multarget\":\""
						+split[4]+
					"\"\},";
		}
		jsonConnections=jsonConnections.substr(0,jsonConnections.length-1)+"\],";
	}
		if(imp == 0){
		jsonConnections+="\"implementation\":[],";
	}else{
		jsonConnections+="\"implementation\":[";
		for(i=0; i<imp;i++){
		var split =connections[i].split("_");
			jsonConnections+=	"\{\"start\":\""
						+split[1]+
					"\",\"target\":\""
						+split[2]+
					"\",\"mulstart\":\""
						+split[3]+
					"\",\"multarget\":\""
						+split[4]+
					"\"\},";
		}
		jsonConnections=jsonConnections.substr(0,jsonConnections.length-1)+"\],";
	}
	if(gen == 0){
		jsonConnections+="\"generalization\":[]\{\{\}";
		jsonConnections=jsonConnections.substr(0,jsonConnections.length-3)+"\}\}";
	}else{
		jsonConnections+="\"generalization\":[";
		for(i=0; i<gen;i++){
		var split =connections[i].split("_");
			jsonConnections+=	"\{\"start\":\""
						+split[1]+
					"\",\"target\":\""
						+split[2]+
					"\",\"mulstart\":\""
						+split[3]+
					"\",\"multarget\":\""
						+split[4]+
					"\"\},";
		}
		jsonConnections=jsonConnections.substr(0,jsonConnections.length-1)+"\]\}\}";
	}
	
	json+=jsonConnections;
	console.log(json);
    return "fname=" + json;
}
