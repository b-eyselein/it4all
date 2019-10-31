import {Component, OnInit} from '@angular/core';
import * as $ from 'jquery';
import * as joint from 'jointjs';

@Component({
  selector: 'it4all-uml-test',
  templateUrl: './uml-test.component.html',
})
export class UmlTestComponent implements OnInit {

  constructor() {
  }

  ngOnInit() {
    const graph: joint.dia.Graph = new joint.dia.Graph();

    const paper: joint.dia.Paper = new joint.dia.Paper({
      el: $('#myPaper'),
      width: 1000, height: 500,
      model: graph,
      gridSize: 1
    });

    const rect = new joint.shapes.basic.Rect({
      position: {x: 100, y: 100},
      size: {width: 100, height: 100},
      attrs: {rect: {fill: 'blue'}, text: {text: 'testTxt!', fill: 'white'}}
    });

    graph.addCell(rect);
  }

}
