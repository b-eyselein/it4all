import * as $ from 'jquery';
import * as joint from 'jointjs';
import * as _ from 'underscore';

import './boolDrawingElements';

import Cell = joint.dia.Cell;
import CellView = joint.dia.CellView;
import Element = joint.dia.Element;
import Graph = joint.dia.Graph;
import Link = joint.dia.Link;
import LinkView = joint.dia.LinkView;
import {BooleanFormula} from '../../_model/bool-formula';
import {BooleanVariable} from '../../_model/bool-node';

export const graph: Graph = new Graph();

const elementWidth = 60;
const gridSize = elementWidth / 2;

export let paper: joint.dia.Paper;

export let dragX; // Postion within div : X
export let dragY;	// Postion within div : Y

export const SIGNALS: Map<string, boolean> = new Map([
  ['a', true],
  ['b', true],
  ['c', true],
  ['d', true]
]);

export function createElementInGraph(elementName: string, x: number, y: number): void {
  let element: joint.shapes.logic.Gate;
  let svg: string;
  const elementPos = {position: {x, y}};

  switch (elementName) {
    case 'elementAND':
      element = new joint.shapes.logic.And(elementPos);
      svg = 'https://upload.wikimedia.org/wikipedia/commons/0/0f/AND_IEC.svg';
      break;
    case 'elementNAND':
      element = new joint.shapes.logic.Nand(elementPos);
      svg = 'https://upload.wikimedia.org/wikipedia/commons/d/d8/NAND_IEC.svg';
      break;
    case 'elementOR':
      element = new joint.shapes.logic.Or(elementPos);
      svg = 'https://upload.wikimedia.org/wikipedia/commons/4/42/OR_IEC.svg';
      break;
    case 'elementNOR':
      element = new joint.shapes.logic.Nor(elementPos);
      svg = 'https://upload.wikimedia.org/wikipedia/commons/6/6d/NOR_IEC.svg';
      break;
    case 'elementXOR':
      element = new joint.shapes.logic.Xor(elementPos);
      svg = 'https://upload.wikimedia.org/wikipedia/commons/4/4e/XOR_IEC.svg';
      break;
    case 'elementXNOR':
      element = new joint.shapes.logic.Xnor(elementPos);
      svg = 'https://upload.wikimedia.org/wikipedia/commons/5/56/XNOR_IEC.svg';
      break;
    case 'elementNOT':
      element = new joint.shapes.logic.Not(elementPos);
      svg = 'https://upload.wikimedia.org/wikipedia/commons/e/ef/NOT_IEC.svg';
      break;
  }

  element.attr('image/xlink:href', svg);

  graph.addCell(element);
}

document.addEventListener('dragover', (e: DragEvent) => {
  const offset = $('#paper').offset();
  // console.warn(offset);
  dragX = e.pageX - offset.left;
  dragY = e.pageY - offset.top;
}, false);

function initGraph(inputsToAdd, outputsToAdd): void {
  for (const inputToAdd of inputsToAdd) {
    const newGate = new joint.shapes.logic.Input(inputToAdd);

    newGate.attr('logicSymbol', inputToAdd.symbol);
    newGate.attr('text', {text: inputToAdd.symbol});

    graph.addCell(newGate);
  }


  for (const outputToAdd of outputsToAdd) {
    const newGate = new joint.shapes.logic.Output(outputToAdd);

    newGate.attr('logicSymbol', outputToAdd.symbol);
    newGate.attr('text', {text: outputToAdd.symbol});

    graph.addCell(newGate);
  }

}

export function toggleLive(model: Cell, signal: boolean): void {
  // add 'live' class to the element if there is a positive signal
  joint.V(paper.findViewByModel(model).el).toggleClass('live', signal);
}

export function broadcastSignal(gate: joint.shapes.logic.Gate, signal: boolean): void {
  toggleLive(gate, signal);

  // broadcast signal to all output ports
  const outGoingWires = graph.getConnectedLinks(gate, {outbound: true}) as joint.shapes.logic.Wire[];
  for (const wire of outGoingWires) {
    wire.set('signal', signal);
  }
}

function initializeSignals(): void {
  // cancel all signals stores in wires
  for (const wire of graph.getLinks()) {
    wire.set('signal', false);
  }

  // remove all 'live' classes
  $('.live').each(() => {
    // TODO:        joint.V(this).removeClass('live');
  });

  for (const element of graph.getElements()) {
    const outGoingLinks: Link[] = graph.getConnectedLinks(element, {outbound: true});
    if (element instanceof joint.shapes.logic.Input && outGoingLinks.length > 0) {
      broadcastSignal(element, SIGNALS[element.attr('logicSymbol')]);
    }
  }
}

function initGraphEvents(): void {

  graph.on('change:source change:target', (model, end) => {

    const e: string = 'target' in model.changed ? 'target' : 'source';

    if ((model.previous(e).id && !model.get(e).id) || (!model.previous(e).id && model.get(e).id)) {
      // if source/target has been connected to a port or disconnected from a port reinitialize signals
      initializeSignals();
    }
  });

  graph.on('change:signal', (wire: joint.shapes.logic.Wire, signal: boolean) => {

    toggleLive(wire, signal);

    const gate: joint.shapes.logic.Gate = graph.getCell(wire.get('target').id) as joint.shapes.logic.Gate;

    if (gate && false) {
      // FIXME: implement!
      if (gate instanceof joint.shapes.logic.Gate11) {
        gate.onSignal(signal, () => {
          const maybeInput = graph.getConnectedLinks(gate, {inbound: true});
          const input: boolean = (maybeInput.length === 1) ? maybeInput[0].get('signal') : false;

          // calculate the output signal
          throw Error('TODO');
          // const output: boolean = gate.operation(input);
          // broadcastSignal(gate, output);
        });
      } else if (gate instanceof joint.shapes.logic.Gate21) {
        const maybeInput = graph.getConnectedLinks(gate, {inbound: true});

        const input1: boolean = (maybeInput.length >= 1) ? maybeInput[0].get('signal') : false;
        const input2: boolean = (maybeInput.length === 2) ? maybeInput[1].get('signal') : false;

        // calculate the output signal
        throw Error('TODO');
        // const output: boolean = gate.operation(input1, input2);
        // broadcastSignal(gate, output);
      } else {
        // TODO!
      }
    }
  });

  graph.on('change:position', (cell, newPosition, opt) => {

    if (opt.skipParentHandler) {
      return;
    }

    if (cell.get('embeds') && cell.get('embeds').length) {
      // If we're manipulating a parent element, let's store it's original position to a special property so that
      // we can shrink the parent element back while manipulating its children.
      cell.set('originalPosition', cell.get('position'));
    }

    const parentId = cell.get('parent');
    if (!parentId) {
      return;
    }

    const parent = graph.getCell(parentId);
    if (!parent.get('originalPosition')) {
      parent.set('originalPosition', parent.get('position'));
    }
    if (!parent.get('originalSize')) {
      parent.set('originalSize', parent.get('size'));
    }

    const originalPosition = parent.get('originalPosition');
    const originalSize = parent.get('originalSize');

    let newX = originalPosition.x;
    let newY = originalPosition.y;
    let newCornerX = originalPosition.x + originalSize.width;
    let newCornerY = originalPosition.y + originalSize.height;

    _.each(parent.getEmbeddedCells(), (child: Element) => {

      const childBbox: joint.g.Rect = child.getBBox();

      if (childBbox.x < newX) {
        newX = childBbox.x;
      }
      if (childBbox.y < newY) {
        newY = childBbox.y;
      }
      if (childBbox.corner().x > newCornerX) {
        newCornerX = childBbox.corner().x;
      }
      if (childBbox.corner().y > newCornerY) {
        newCornerY = childBbox.corner().y;
      }
    });

    // Note that we also pass a flag so that we know we shouldn't adjust the `originalPosition` and `originalSize` in our handlers as a
    // reaction on the following `set()` call.
    parent.set({
      position: {x: newX, y: newY},
      size: {width: newCornerX - newX, height: newCornerY - newY}
    }/*, {skipParentHandler: true}*/);
  });

  graph.on('change:size', (cell, newPosition, opt) => {
    if (opt.skipParentHandler) {
      return;
    }

    if (cell.get('embeds') && cell.get('embeds').length) {
      // If we're manipulating a parent element, let's store it's original size to a special property so that
      // we can shrink the parent element back while manipulating its children.
      cell.set('originalSize', cell.get('size'));
    }
  });
}

export function draw(formula: BooleanFormula): void {
  const paperSelector: JQuery<HTMLElement> = $('#paper');

  paper = new joint.dia.Paper({
    el: paperSelector,
    model: graph,

    width: paperSelector.width(), height: 600,

    gridSize, drawGrid: {color: 'grey', thickness: 1, name: 'mesh'},

    snapLinks: true, linkPinning: false,
    defaultLink: (cellView: CellView, magnet: SVGElement) => new joint.shapes.logic.Wire(cellView, magnet),

    validateConnection(
      cellViewSource: CellView, magnetSource: SVGElement,
      cellViewTarget: CellView, magnetTarget: SVGElement,
      end: string, linkView: LinkView
    ) {
      if (end === 'target') {
        //        target requires an input port to connect
        if (!magnetTarget || !magnetTarget.getAttribute('class') || magnetTarget.getAttribute('class').indexOf('input') < 0) {
          return false;
        }

        //  check whether the port is being already used
        const portUsed = _.find(this.model.getLinks(), (link: joint.dia.Link) => {

          return (link.id !== linkView.model.id &&
            link.get('target').id === cellViewTarget.model.id &&
            link.get('target').port === magnetTarget.getAttribute('port'));
        });

        return !portUsed;

      } else { // e === 'source'
        //     source requires an output port to connect
        return magnetSource && magnetSource.getAttribute('class') && magnetSource.getAttribute('class').indexOf('output') >= 0;
      }
    }
  });


  // Graph events
  initGraphEvents();

  const outputVariables: string[] = ['z'];

  const inputsToAdd = formula.getVariables().map((variable: BooleanVariable, index: number) => {
    return {symbol: variable.variable, position: {x: gridSize, y: gridSize + index * (2 * elementWidth + gridSize)}};
  });

  const xMaxPos: number = paper.getArea().width - 60 - gridSize;
  // const yPos: number = paper.getArea().height;

  const outputsToAdd = outputVariables.map((variable: string, index: number) => {
    // TODO: calculate if more than one input!
    return {symbol: variable, position: {x: xMaxPos, y: 3 * elementWidth}};
  });

  initGraph(inputsToAdd, outputsToAdd);
}
