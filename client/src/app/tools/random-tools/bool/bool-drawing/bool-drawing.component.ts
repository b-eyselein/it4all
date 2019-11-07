import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {broadcastSignal, createElement, dragX, dragY, draw, graph, paper, SIGNALS, toggleLive} from './_bool-drawing-model/boolDrawing';
import {extractFormulaFromGraph} from './_bool-drawing-model/formulaExtractor';
import {BooleanNode, BooleanVariable} from '../_model/bool-node';
import {Event} from 'jquery';
import * as joint from 'jointjs';

@Component({
  templateUrl: './bool-drawing.component.html',
  styles: [`.live {
      background: green;
  }`],
  encapsulation: ViewEncapsulation.None
})
export class BoolDrawingComponent implements OnInit {

  readonly binaryGateTypes: string[] = ['and', 'nand', 'or', 'nor', 'xor', 'xnor'/*, 'equiv', 'impl'*/];

  elementToCreate: string | undefined = undefined;

  constructor() {
  }

  private initPaperEvents(): void {
    paper.on('blank:pointerclick', (evt: Event, x: number, y: number) => {
      if (this.elementToCreate) {
        createElement('element' + this.elementToCreate.toUpperCase(), x, y);
      }
    });

    paper.on('cell:pointerclick', (cellView: joint.dia.CellView) => {
      // Left click on cell
      const inputCell: joint.dia.Cell = cellView.model;

      if (inputCell instanceof joint.shapes.logic.Input) {
        const logicSymbol: string = inputCell.attr('logicSymbol');

        const newSignal: boolean = !SIGNALS.get(logicSymbol);
        SIGNALS.set(logicSymbol, newSignal);

        toggleLive(inputCell, newSignal);

        broadcastSignal(inputCell, newSignal);
      }
    });

    paper.on('cell:contextmenu', (evt: joint.dia.CellView) => {
      // Right click on cell
      if (evt.model instanceof joint.shapes.logic.IO) {
        alert('You cannot delete in Input or an Output!');
      } else {
        graph.getCell(evt.model.id).remove();
      }
    });

  }

  ngOnInit() {
    draw();

    this.initPaperEvents();
  }

  toggleGateButton(event: MouseEvent, gateType: string): void {
    this.elementToCreate = this.elementToCreate === gateType ? undefined : gateType;
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

  updateFormula(): void {
    const formulas: [BooleanVariable, (BooleanNode | undefined)][] = extractFormulaFromGraph(graph);

    formulas.forEach((outputFormula: [BooleanVariable, (BooleanNode | undefined)]) => {
      if (outputFormula[1]) {
        console.info(outputFormula[0].variable + ' = ' + outputFormula[1].asString());
      } else {
        console.info(outputFormula[0].variable + ' = undefined!');
      }
    });
  }

}
