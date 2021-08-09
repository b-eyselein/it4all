import Dexie, {PromiseExtended, Table} from 'dexie';

export interface DbSolution<T> {
  toolId: string;
  collectionId: number;
  exerciseId: number;
  partId: string;
  solution: T;
}

class DexieTable extends Dexie {

  // eslint-disable-next-line
  private solutions: Table<DbSolution<any>, [string, number, number, string]>;

  constructor() {
    super('it4all');

    this.version(1).stores({
      solutions: '[toolId+collectionId+exerciseId+partId]'
    });

    this.solutions = this.table('solutions');
  }

  getSolution<T>(toolId: string, collectionId: number, exerciseId: number, partId: string): PromiseExtended<DbSolution<T> | undefined> {
    return this.solutions.get([toolId, collectionId, exerciseId, partId]);
  }

  upsertSolution<T>(toolId: string, collectionId: number, exerciseId: number, partId: string, solution: T): PromiseExtended<[string, number, number, string]> {
    return this.solutions.put({toolId, collectionId, exerciseId, partId, solution});
  }

  deleteSolution(toolId: string, collectionId: number, exerciseId: number, partId: string): PromiseExtended<void> {
    return this.solutions.delete([toolId, collectionId, exerciseId, partId]);
  }

}

export const database = new DexieTable();
