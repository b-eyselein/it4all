import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'it4all-uml-diag-drawing-correction',
  templateUrl: './uml-diag-drawing-correction.component.html'
})
export class UmlDiagDrawingCorrectionComponent implements OnInit {

  @Input() result: any;

  constructor() {
  }

  ngOnInit() {
  }

}
