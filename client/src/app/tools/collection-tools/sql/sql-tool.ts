import {CollectionTool, ExerciseTag, Tool, ToolPart} from '../../../_interfaces/tool';
import {SqlExerciseContent} from './sql-interfaces';

export const SqlCreateQueryPart: ToolPart = {id: 'createQuery', name: 'Abfrage erstellen'};


const sqlExerciseTags: Map<string, ExerciseTag> = new Map<string, ExerciseTag>([
  ['SQL_JOIN', {label: 'J', title: 'Join'}],
  ['SQL_DOUBLE_JOIN', {label: '2J', title: 'Zweifacher Join'}],
  ['SQL_TRIPLE_JOIN', {label: '3J', title: 'Dreifacher Join'}],
  ['SQL_ORDER_BY', {label: 'O', title: 'Reihenfolge'}],
  ['SQL_GROUP_BY', {label: 'G', title: 'Gruppierung'}],
  ['SQL_FUNCTION', {label: 'F', title: 'Funktion'}],
  ['SQL_ALIAS', {label: 'A', title: 'Alias'}],
  ['SQL_LIMIT', {label: 'L', title: 'Limitierung'}],
  ['SQL_SUBSELECT', {label: 'S', title: 'Zweites Select innerhalb'}]
]);

export const SqlTool: CollectionTool<SqlExerciseContent> = new (
  class SqlToolClass extends CollectionTool<SqlExerciseContent> {
    constructor() {
      super('sql', 'SQL', [SqlCreateQueryPart], 'live');
    }

    processTagString(tag: string): ExerciseTag {
      return sqlExerciseTags.get(tag);
    }
  }
)();
