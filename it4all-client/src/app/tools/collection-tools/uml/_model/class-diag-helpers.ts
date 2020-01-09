import * as joint from 'jointjs';
import {GRID_SIZE} from './uml-consts';
import {STD_CLASS_HEIGHT, STD_CLASS_WIDTH} from './joint-class-diag-elements';


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
