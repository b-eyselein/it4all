import * as joint from 'jointjs';
import {GRID_SIZE} from './uml-consts';
import {MyJointClass, STD_CLASS_HEIGHT, STD_CLASS_WIDTH} from './joint-class-diag-elements';
import {UmlAttributeInput, UmlMethodInput} from "../../../../_services/apollo_services";

function findFreePositionForNextClass(paper: joint.dia.Paper): joint.dia.Point {

  const maxRows = Math.floor((paper.getArea().height - GRID_SIZE) / (STD_CLASS_HEIGHT + GRID_SIZE));
  const maxCols = Math.floor((paper.getArea().width - GRID_SIZE) / (STD_CLASS_WIDTH + GRID_SIZE));

  for (let row = 0; row < maxRows; row++) {
    for (let col = 0; col < maxCols; col++) {
      const x = GRID_SIZE + col * (STD_CLASS_WIDTH + GRID_SIZE);
      const y = GRID_SIZE + row * (STD_CLASS_HEIGHT + GRID_SIZE);

      const viewIsBlocked = paper.findViewsInArea({x, y, width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT}).length > 0;

      if (!viewIsBlocked) {
        return {x, y};
      }
    }
  }

  return {x: GRID_SIZE, y: GRID_SIZE};
}

export function addClassToGraph(
  name: string,
  paper: joint.dia.Paper,
  attributes: UmlAttributeInput[] = [],
  methods: UmlMethodInput[] = [],
  maybePosition?: joint.dia.Point,
): void {
  if (paper.model.getCells().find((c) => c instanceof MyJointClass && c.getClassName() === name)) {
    // graph already contains class with that name!
    return;
  }

  paper.model.addCell(
    new MyJointClass({
      className: name,
      size: {width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT},
      position: maybePosition || findFreePositionForNextClass(paper),
      attributes, methods
    })
  );
}

export function addImplementationToGraph(subClass: MyJointClass, superClass: MyJointClass, graph: joint.dia.Graph): void {
  graph.addCell(
    new joint.shapes.uml.Implementation({
      source: {id: subClass.id},
      target: {id: superClass.id}
    })
  );
}

export function addAssociationToGraph(
  firstEnd: MyJointClass, firstMult: string,
  secondEnd: MyJointClass, secondMult: string,
  graph: joint.dia.Graph
): void {
  const config = {
    source: {id: firstEnd.id},
    target: {id: secondEnd.id},
    labels: [
      {position: 25, attrs: {text: {text: firstMult}}},
      {position: -25, attrs: {text: {text: secondMult}}}
    ]
  };

  graph.addCell(new joint.shapes.uml.Association(config));
}
