import * as joint from 'jointjs';
import {GRID_SIZE} from './uml-consts';
import {isAssociation, isImplementation, MyJointClass, STD_CLASS_HEIGHT, STD_CLASS_WIDTH} from './joint-class-diag-elements';
import {UmlAttributeInput, UmlMethodInput, UmlMultiplicity} from '../../../../graphql';

export function findFreePositionForNextClass(paper: joint.dia.Paper): joint.dia.Point {

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
  graph: joint.dia.Graph,
  name: string,
  attributes: UmlAttributeInput[] = [],
  methods: UmlMethodInput[] = [],
  maybePosition: joint.dia.Point,
): void {
  if (graph.getCells().find((c) => c instanceof MyJointClass && c.getClassName() === name)) {
    // graph already contains class with that name!
    console.error(`Class with name ${name} already exists!`);
    return;
  }

  graph.addCell(
    new MyJointClass({
      className: name,
      size: {width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT},
      position: maybePosition,
      attributes, methods
    })
  );
}

export function addAssociationToGraph(graph: joint.dia.Graph, firstEnd: MyJointClass, firstMult: UmlMultiplicity, secondEnd: MyJointClass, secondMult: UmlMultiplicity): void {

  console.info(`Adding assoc to graph from ${firstEnd.getClassName()} to ${secondEnd.getClassName()}`);

  const prior = graph.getLinks().filter(isAssociation).length;

  graph.addCell(
    new joint.shapes.uml.Association({
      source: {id: firstEnd.id},
      target: {id: secondEnd.id},
      labels: [
        {position: 25, attrs: {text: {text: firstMult === 'UNBOUND' ? '*' : '1'}}},
        {position: -25, attrs: {text: {text: secondMult === 'UNBOUND' ? '*' : '1'}}}
      ]
    })
  );

  console.info(prior + ' -> ' + graph.getLinks().filter(isAssociation).length);
}

export function addImplementationToGraph(graph: joint.dia.Graph, subClass: MyJointClass, superClass: MyJointClass): void {
  const subClassName = subClass.getClassName();
  const superClassName = superClass.getClassName();

  const alreadyExistingImplementations = graph.getLinks()
    .filter(isImplementation)
    .filter((impl) => {
      const source: joint.dia.Element | null = impl.getSourceElement();
      const sourceEqual = source && ((source as MyJointClass).getClassName() === subClassName);

      const target = impl.getTargetElement();
      const targetEqual = target && ((target as MyJointClass).getClassName() === superClassName);

      return sourceEqual && targetEqual;
    });

  if (alreadyExistingImplementations.length > 0) {
    console.error(`Tried to create an already existing implementation from ${subClassName} to ${superClassName}!`);
    return;
  }

  graph.addCell(
    new joint.shapes.uml.Implementation({
      source: {id: subClass.id},
      target: {id: superClass.id}
    })
  );
}
