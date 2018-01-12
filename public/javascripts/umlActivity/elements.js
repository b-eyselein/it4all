const externPortWidth = 250; // all excecpt if-then-else

function get_actionInput(xCoord,yCoord){
		action = new joint.shapes.html.Element({
						position: {x: xCoord, y: yCoord},
						size: {width: 250, height: 40},
						name: "actionInput",
						cleanname: "Aktionsknoten",
						template: [
							'<div class="action-element">',
							'<button class="delete">x</button>',
							'<textarea onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="varContent"></textarea>',
							'</div>'
						].join(''),
						varContent:"",
						ports: {
							groups: {
								'in': {
									position: 'absolute',
									attrs: {
										circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
									}
								},
								'out': {
									position: 'absolute',
									attrs: {
										circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
									}
								}
							},
							items: [{
								id: 'in',
								group: 'in',
								args: {x: 125, y: 0}
							},
							{
								id: 'out',
								group: 'out',
								args: {x: 125, y: 40}
							}]
						}
					});	
		return action;
}

function get_actionSelect(xCoord,yCoord){
		action = new joint.shapes.html.Element({
						position: {x: xCoord, y: yCoord},
						size: {width: 250, height: 40},
						name: "actionSelect",
						cleanname: "Aktionsknoten",
						template: [
							'<div class="action-element">',
							'<button class="delete">x</button>',
							'<select data-attribute="varContent"><option></option><option>getNutzlast()</option><option>getContent()</option><option>Example()</option></select>',	
							'</div>'
						].join(''),
						varContent:"",
						ports: {
							groups: {
								'in': {
									position: 'absolute',
									attrs: {
										circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
									}
								},
								'out': {
									position: 'absolute',
									attrs: {
										circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
									}
								}
							},
							items: [{
								id: 'in',
								group: 'in',
								args: {x: 125, y: 0}
							},
								{
									id: 'out',
									group: 'out',
									args: {x: 125, y: 40}
								}]
						}
					});	
		return action;
}

function get_actionDeclare(xCoord,yCoord){
		action = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 250, height: 40},
                template: [
                    '<div class="actionDeclare">',
                    '<button class="delete">x</button>',
					'<select data-attribute="varContent1"><option></option><option>String</option><option>Double</option><option>Boolean</option></select>',
					'<input placeholder="Var" class="smallInput" data-attribute="varContent2" type="text"/></input>',
					'<span> = </span>',
					'<input placeholder="Anweisung" class="normalInput" data-attribute="varContent3" type="text"/></input>',
		
                    '</div>'
                ].join(''),
                'varContent1': '',
                'varContent2': '',	
                'varContent3': '',				
                name: "actionDeclare",
                cleanname: "Aktionknoten",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            label: {
                                // label layout definition:
                                position: {
                                    name: 'manual', args: {
                                        y: 250,
                                        attrs: {
                                            '.': {'text-anchor': 'middle'},
                                            text: {fill: 'black', 'pointer-events': 'none'}
                                        }
                                    }
                                }
                            },
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 125, y: 0}
                    },
                        {
                            id: 'out',
                            group: 'out',
                            args: {x: 125, y: 40}
                        }]
                }
            });	
		return action;
}

function get_for(xCoord,yCoord){
	element_for = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 250, height: 100},
                template: [
                    '<div class="for_element">',
                    '<button class="delete">x</button>',
                    '<div class="dashed-bot">',
                    '<span> for </span>',
                    '<input placeholder="Element" data-attribute="efor" type="text"/></input>',
                    '<span> in </span>',
                    '<input placeholder="Collection" data-attribute="ein"  type="text"/></input>',
                    '</div>',
					'<textarea onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="area"></textarea>',
                    '</div>'
                ].join(''),
                'efor': '',
                'ein': '',
                area: '',
                name: "forin",
                cleanname: "For-In-Schleife",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            label: {
                                // label layout definition:
                                position: {
                                    name: 'manual', args: {
                                        y: 250,
                                        attrs: {
                                            '.': {'text-anchor': 'middle'},
                                            text: {fill: 'black', 'pointer-events': 'none'}
                                        }
                                    }
                                }
                            },
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
						'extern': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 125, y: 0}
                    },
					{
                        id: 'extern',
                        group: 'extern',
                        args: {x: externPortWidth, y: 55}
                    },
					{
						id: 'out',
						group: 'out',
						args: {x: 125, y: 100}
					}]
                }
            });
	return element_for;
}
	
function get_if(xCoord,yCoord){
	ife = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 250, height: 180},
                template: [
                    '<div class="if_element">',
                    '<button class="delete">x</button>',
                    '<div class="dashed-bot">',
                    '<span> if </span>',
                    '<input placeholder="Bedingung" data-attribute="eif" type="text"/></input>',
                    '</div>',
                    '<div class="dashed-bot">',
                    '<span>then</span>',
                    '<textarea onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen" data-attribute="ethen"></textarea>',
                    '</div>',
                    '<div>',
                    '<span>else</span>',
                    '<textarea onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen" data-attribute="eelse"></textarea>',
                    '</div>',
                    '</div>'

                ].join(''),
                'eif': '',
                'ethen': '',
                'eelse': '',
                name: "if",
                cleanname: "Bedingungsknoten",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
						'extern-ethen': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
						'extern-eelse': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 125, y: 0}
                    },
					{
						id: 'out',
						group: 'out',
						args: {x: 125, y: 180}
					},
					{
                        id: 'extern-ethen',
                        group: 'extern-ethen',
                        args: {x: externPortWidth, y: 75}
                    },
					{
                        id: 'extern-eelse',
                        group: 'extern-eelse',
                        args: {x: externPortWidth, y: 125}
                    }]
                }
            });
	return ife;
}
	
function get_dw(xCoord,yCoord){
	dw =  new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 250, height: 120},
                template: [
                    '<div class="wd_element">',
                    '<button class="delete">x</button>',
                    '<span>do</span>',
                    '<textarea onkeyup="textAreaAdjust(this)"  placeholder="Anweisungen"  data-attribute="edo"></textarea>',
                    '<div class="dashed-top">',
                    '<span> while </span>',
                    '<input placeholder="Bedingung" data-attribute="ewhile" type="text"/></input>',
                    '</div>',
                    '</div>'
                ].join(''),
                'ewhile': '',
                'edo': '',
                name: "dw",
                cleanname: "Do-While-Knoten",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
						'extern': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 125, y: 0}
                    },
					{
						id: 'out',
						group: 'out',
						args: {x: 125, y: 120}
					},
					{
						id: 'extern',
						group: 'extern',
						args: {x: externPortWidth, y: 60}
					}]
                }
            });
	return dw;
}	
	
function get_wd(xCoord,yCoord){
	wd = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 250, height: 120},
                template: [
                    '<div class="wd_element">',
                    '<button class="delete">x</button>',
                    '<div class="dashed-bot">',
                    '<span> while </span>',
                    '<input placeholder="Bedingung" data-attribute="ewhile" type="text"/></input>',
                    '</div>',
                    '<span>do</span>',
                    '</br>',
                    '<textarea onkeyup="textAreaAdjust(this)" placeholder="Anweisungen" data-attribute="edo"></textarea>',
                    '</div>'
                ].join(''),
                'ewhile': '',
                'edo': '',
                name: "wd",
                cleanname: "While-Do-Knoten",
                ports: {
                    groups: {
                        'in': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'out': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
						'extern': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'black', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'in',
                        group: 'in',
                        args: {x: 125, y: 0}
                    },
					{
						id: 'out',
						group: 'out',
						args: {x: 125, y: 120}
					},
					{
						id: 'extern',
						group: 'extern',
						args: {x: externPortWidth, y: 60}
					}
					]
                }
            });
	return wd;
}	
	
function get_edit(xCoord,yCoord){
	edit = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 170, height: 120},
                template: [
                    '<div class="edit_element">',
                    '<button class="delete">x</button>',
                    '</div>'
                ].join(''),
                name: "edit",
                cleanname: "Externer Knoten",
                ports: {
                    groups: {
                        'extern': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'extern',
                        group: 'extern',
                        args: {x: -10, y: 60}
                    }]
}
            });
		var start = get_start("Externer Startknoten",edit.id,xCoord,yCoord,"Start");
		var end = get_end("Externer Endknoten",edit.id,xCoord+118,yCoord+68,"Ende");	
		graph.addCell(edit);
		edit.embed(start);
		start.toFront({ deep: true});
		end.toFront({ deep: true});	
		graph.addCell(start);
		edit.embed(end);
		parentChildNodes.push({"parentId":edit.id,"startId":start.id,"endId":end.id,"endName":end.name});		
		//edit.attr('rect/magnet', true).attr('text/pointer-events', 'none');
	return end;
}		
	
	
function get_basic(xCoord,yCoord){
	basic = element_basic = new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 100, height: 100},
                template: [
                    '<div class="basic">',
                    '<button class="delete">x</button>',

                    '<div class="myDiv3">',
                    '<input placeholder="Bedingung" data-attribute="einput" class="myDiv4" type="text">',
                    '</div>',
                    '<div class="left">',
                    '<span></span>',
                    '</div>',
                    '<div class="right"></div>',
                    '<div class="bot">',
                    '<span></span>',
                    '</div>',
                    '<div class="top"></div>',
                    '</div>'
                ].join(''),
                einput: '',
                name: "unknown",
                cleanname: "Verzweigungsknoten",
                ports: {
                    groups: {
                        'top': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'left': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'green', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'right': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'red', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        },
                        'bot': {
                            position: 'absolute',
                            attrs: {
                                circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 10, magnet: true}
                            }
                        }
                    },
                    items: [{
                        id: 'top',
                        group: 'top',
                        args: {x: 53, y: -3}
                    },
                        {
                            id: 'bot',
                            group: 'bot',
                            args: {x: 53, y: 104}
                        },
                        {
                            id: 'left',
                            group: 'left',
                            args: {x: 0, y: 50}
                        },
                        {
                            id: 'right',
                            group: 'right',
                            args: {x: 108, y: 50}
                        }]
                }
            });
	return basic;
}

function get_end(e_name,e_id,e_x,e_y,labeltext){
		end = new joint.shapes.html.Element({
						position: {x: e_x, y: e_y},
						size: {width: 50, height: 50},
						id: "Endknoten-"+e_id,
						name:  e_name,
						cleanname: "Endknoten",
						template: [
							'<div class="circle1">',
								'<div class="circle2">',
									'<div class="circle3">',
									'<label class="endlabelpos" data-attribute="label"></label>',
									'</div>',
								'</div>',
							'</div>'
						].join(''),
						label:labeltext,
						ports: {
							groups: {
								'in': {
									position: 'absolute',
									attrs: {
										circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 22, magnet: true}
									}
								},
							},
							items: [{
								id: 'in',
								group: 'in',
								args: {x: 25, y: 25}
							}]
						}
					});	
		return end;
}

function get_start(s_name,s_id,s_x,s_y,labeltext){
		var start = new joint.shapes.html.Element({
						position: {x: s_x, y: s_y},
						size: {width: 50, height: 50},
						id: "Startknoten-"+s_id,
						name: s_name,
						cleanname: "Startknoten",
						template: [
							'<div class="circle1">',
							'<label class="startlabelpos" data-attribute="label"></label>',
							'</div>'
						].join(''),
						label:labeltext,
						ports: {
							groups: {
								'in': {
									position: 'absolute',
									attrs: {
										circle: {fill: 'transparent', stroke: 'blue', 'stroke-width': 1, r: 22, magnet: true}
									}
								},
							},
							items: [{
								id: 'in',
								group: 'in',
								args: {x: 25, y: 25}
							}]
						}
					});	
		return start;
}