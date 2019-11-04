import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {broadcastSignal, createElement, dragX, dragY, draw, graph, paper, SIGNALS, toggleLive} from '../_model/boolDrawing';
import * as joint from 'jointjs';

@Component({
  templateUrl: './bool-drawing.component.html',
  styles: [`.live {
      background: green;
  }`],
  encapsulation: ViewEncapsulation.None
})
export class BoolDrawingComponent implements OnInit {

  readonly gateTypes: string[] = ['and', 'nand', 'or', 'nor', 'xor', 'xnor', 'equiv', 'impl', 'not'];

  elementToCreate: string | undefined = undefined;

  // constructor() {
  // }

  ngOnInit() {
    draw();

    this.initPaperEvents();
  }

  toggleGateButton(event: MouseEvent, gateType: string): void {
    if (this.elementToCreate === gateType) {
      this.elementToCreate = undefined;
    } else {
      this.elementToCreate = gateType;
    }
  }

  gateButtonOnDragstart(event: DragEvent, item: string): void {
    event.dataTransfer.setData('text', item);
  }

  paperOnDragover(event: DragEvent): void {
    event.preventDefault();
  }

  paperOnDrop(event: DragEvent): void {
    event.preventDefault();

    const elementToCreate: string = event.dataTransfer.getData('text');

    console.error(elementToCreate);

    // scale: Coordinates offset by graph scale, correct with factor
    const scale = joint.V(paper.viewport).scale();

    createElement('element' + elementToCreate.toUpperCase(), dragX / scale.sx, dragY / scale.sy);
  }

  private initPaperEvents(): void {
    paper.on('cell:contextmenu', (evt, x, y) => {
      if (!(evt.model.attributes.type === 'logic.Input' || evt.model.attributes.type === 'logic.Output')) {
        graph.getCell(evt.model.id).remove();
      }
    });

    paper.on('cell:pointerclick', (cellView, evt, x, y) => {
      const inputCell = cellView.model;
      if (inputCell instanceof joint.shapes.logic.Input) {
        const logicSymbol: string = inputCell.attr('logicSymbol');

        const newSignal: boolean = !SIGNALS.get(logicSymbol);
        SIGNALS.set(logicSymbol, newSignal);

        toggleLive(inputCell, newSignal);

        broadcastSignal(inputCell, newSignal);
      }
    });

    paper.on('blank:pointerclick', (evt, x, y) => {
      if (this.elementToCreate !== '') {
        createElement('element' + this.elementToCreate.toUpperCase(), x, y);
      }
    });
  }

}
