import * as joint from 'jointjs';
import {BooleanNode, BooleanNot, BooleanVariable, instantiateOperator} from '../../_model/bool-node';
import {paper} from './boolDrawing';

function getSubOutputFormula(wire: joint.dia.Link, graph: joint.dia.Graph): BooleanNode | undefined {
  const inputGate: joint.shapes.logic.Gate = graph.getCell(wire.prop('source').id) as joint.shapes.logic.Gate;
  return getOutputFormula(inputGate, graph);
}

function getOutputFormulaFromSingleInputGate(
  gate: joint.shapes.logic.Gate11,
  ingoingWires: joint.dia.Link[],
  graph: joint.dia.Graph
): BooleanNode | undefined {
  if (gate instanceof joint.shapes.logic.Not) {
    if (ingoingWires.length !== 1) {
      // Not cannot have more than 1 ingoing wire...
      gate.findView(paper).highlight();
      return undefined;
    }

    const sourceInput: BooleanNode | undefined = getSubOutputFormula(ingoingWires[0], graph);
    return sourceInput ? new BooleanNot(sourceInput) : undefined;

  } else if (gate instanceof joint.shapes.logic.Repeater) {
    // TODO: Repeater!
    return undefined;
  } else {
    return undefined;
  }
}

function getOutputFormulaFromDoubleInputGate(
  gate: joint.shapes.logic.Gate21,
  ingoingWires: joint.dia.Link[],
  graph: joint.dia.Graph
): BooleanNode | undefined {
  if (ingoingWires.length !== 2) {
    paper.findViewByModel(gate.id).highlight();
    return undefined;
  }

  const firstInput: BooleanNode | undefined = getSubOutputFormula(ingoingWires[0], graph);
  const secondInput: BooleanNode | undefined = getSubOutputFormula(ingoingWires[1], graph);

  if (firstInput && secondInput) {
    const operationString: string = gate.attributes.type.split('\.')[1].toLocaleLowerCase();
    return instantiateOperator(firstInput, operationString, secondInput);
  } else {
    return undefined;
  }
}

function getOutputFormula(gate: joint.shapes.logic.Gate, graph: joint.dia.Graph): BooleanNode | undefined {
  const ingoingWires: joint.dia.Link[] = graph.getConnectedLinks(gate, {inbound: true});

  if (gate instanceof joint.shapes.logic.Gate11) {

    return getOutputFormulaFromSingleInputGate(gate, ingoingWires, graph);

  } else if (gate instanceof joint.shapes.logic.Gate21) {

    // gate is and, nand, or, nor, xor, xnor, equiv or impl
    return getOutputFormulaFromDoubleInputGate(gate, ingoingWires, graph);

  } else if (gate instanceof joint.shapes.logic.IO) {

    if (gate instanceof joint.shapes.logic.Input) {
      return new BooleanVariable(gate.attr('logicSymbol').toString().charAt(0));
    } else if (gate instanceof joint.shapes.logic.Output) {

      if (ingoingWires.length !== 1) {
        gate.findView(paper).highlight();
        return undefined;
      }

      return getSubOutputFormula(ingoingWires[0], graph);
    }
  }
}

export function extractFormulaFromGraph(graph: joint.dia.Graph): [BooleanVariable, (BooleanNode | undefined)][] {
  const formulas: [BooleanVariable, (BooleanNode | undefined)][] = [];

  for (const element of graph.getElements()) {
    paper.findViewByModel(element).unhighlight();

    if (element instanceof joint.shapes.logic.Output) {
      const variable = new BooleanVariable(element.attr('logicSymbol').toString().charAt(0));

      formulas.push([variable, getOutputFormula(element, graph)]);
    }
  }

  return formulas;
}
