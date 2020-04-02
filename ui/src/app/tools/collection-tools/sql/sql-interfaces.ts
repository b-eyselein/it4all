
export interface ISqlCell {
  colName: string;
  content: string;
}


export interface ISqlRow {
  cells: { [ key: string ]: ISqlCell };
}


export interface ISqlQueryResult {
  columnNames: string[];
  rows: ISqlRow[];
  tableName: string;
}
