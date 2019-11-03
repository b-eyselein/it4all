import {Component, OnInit} from '@angular/core';
import {draw} from '../_model/boolDrawing';

@Component({
  // selector: 'it4all-bool-drawing',
  templateUrl: './bool-drawing.component.html',
})
export class BoolDrawingComponent implements OnInit {

  constructor() {
  }

  ngOnInit() {
    draw();
  }

}
