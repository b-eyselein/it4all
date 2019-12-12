import {CollectionTool} from '../../../_interfaces/tool';

export const RoseTool: CollectionTool = new class RoseToolClass extends CollectionTool {
  constructor() {
    super('rose', 'ROSE', [], 'alpha');
  }
}();
