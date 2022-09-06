import Dexie, {PromiseExtended, Table} from 'dexie';

export interface DbSolutionWithoutParts<T> {
  toolId: string;
  collectionId: number;
  exerciseId: number;
  solution: T;
}

export interface DbSolutionWithParts<T> extends DbSolutionWithoutParts<T> {
  partId: string;
}

type SolWithoutPartsKey = [string, number, number];
type SolWithPartsKey = [string, number, number, string];

class DexieTable extends Dexie {

  // eslint-disable-next-line
  private solutionsWithoutParts: Table<DbSolutionWithoutParts<any>, SolWithoutPartsKey>;
  // eslint-disable-next-line
  private solutionsWithParts: Table<DbSolutionWithParts<any>, SolWithPartsKey>;

  constructor() {
    super('it4all');

    this.version(1).stores({
      solutionsWithoutParts: '[toolId+collectionId+exerciseId]',
      solutionsWithParts: '[toolId+collectionId+exerciseId+partId]'
    });

    this.solutionsWithoutParts = this.table('solutionsWithoutParts');
    this.solutionsWithParts = this.table('solutionsWithParts');
  }

  getSolutionWithoutParts<T>(key: SolWithoutPartsKey): PromiseExtended<DbSolutionWithoutParts<T> | undefined> {
    return this.solutionsWithoutParts.get(key);
  }

  getSolutionWithParts<T>(key: SolWithPartsKey): PromiseExtended<DbSolutionWithParts<T> | undefined> {
    return this.solutionsWithParts.get(key);
  }

  upsertSolutionWithoutParts<T>(toolId: string, collectionId: number, exerciseId: number, solution: T): PromiseExtended<SolWithoutPartsKey> {
    return this.solutionsWithoutParts.put({toolId, collectionId, exerciseId, solution});
  }

  upsertSolutionWithParts<T>(toolId: string, collectionId: number, exerciseId: number, partId: string, solution: T): PromiseExtended<SolWithPartsKey> {
    return this.solutionsWithParts.put({toolId, collectionId, exerciseId, partId, solution});
  }

  deleteSolutionWithoutParts(key: SolWithoutPartsKey): PromiseExtended<void> {
    return this.solutionsWithoutParts.delete(key);
  }

  deleteSolutionWithParts(key: SolWithPartsKey): PromiseExtended<void> {
    return this.solutionsWithParts.delete(key);
  }

}

export const database = new DexieTable();
