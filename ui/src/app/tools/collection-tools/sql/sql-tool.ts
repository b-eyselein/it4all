import {CollectionTool, ToolPart} from '../../../_interfaces/tool';

export const SqlCreateQueryPart: ToolPart = {id: 'solve', name: 'Abfrage erstellen'};

export const SqlTool: CollectionTool = new class SqlToolClass extends CollectionTool {
  constructor() {
    super('sql', 'SQL', [SqlCreateQueryPart], 'live');
  }
}();
