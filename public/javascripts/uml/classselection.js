var main =JSON.parse('{"correct":[{"classes":["c1","c2","c3","c4"],"methods":["m1","m2"],"attributes":["a1","a2"]}],"false":[{"classes":["fc1","fc2","Durchmesser des Filtergewindes","maximale Brennweite"],"methods":["fm1"],"attributes":["fa1"]}],"missing":[{"classes":["mc1","Firma"],"methods":["mm1","Fotosystems"],"attributes":["ma1","Webseite"]}]}');
var corrcl = main.correct["0"].classes;
var falscl = main.false["0"].classes;
var misscl = main.missing["0"].classes;
var corrme = main.correct["0"].methods;
var falsme = main.false["0"].methods;
var missme = main.missing["0"].methods;
var corrat = main.correct["0"].attributes;
var falsat = main.false["0"].attributes;
var missat = main.missing["0"].attributes;
createList(corrcl,"cl_corr");
createList(falscl,"cl_fals");
createList(misscl,"cl_miss");
createList(corrme,"me_corr");
createList(falsme,"me_fals");
createList(missme,"me_miss");
createList(corrat,"at_corr");
createList(falsat,"at_fals");
createList(missat,"at_miss");

function createList(array,id){
	console.log("array: "+array);
	console.log("id: "+id);
	var ul = document.createElement("ul");
	for( x in array){
		var il = document.createElement("il");
		il.appendChild(document.createTextNode(array[x]));
		ul.appendChild(il);
		ul.appendChild(document.createElement("br"));
	}
	console.log("ul: "+ul);

	console.log("myNavbar "+myNavbar);
	console.log("getelement: "+document.getElementById("cl_corr"));
	document.getElementById(id).appendChild(ul);
}
