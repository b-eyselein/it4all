import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {SqlResult} from '../sql-interfaces';
import {ApiService} from '../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {IExercise, ISqlExerciseContent, ISqlQueryResult} from '../../../../_interfaces/models';
import {DbSolution} from '../../../../_interfaces/exercise';

import 'codemirror/mode/sql/sql';

interface ActivatableSqlQueryResult extends ISqlQueryResult {
  active?: boolean;
}

@Component({
  selector: 'it4all-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent implements OnInit {

  readonly partId = 'solve';

  @Input() exercise: IExercise;
  exerciseContent: ISqlExerciseContent;

  dbContents: ActivatableSqlQueryResult[] = [];

  solution = '';
  result: SqlResult | undefined;

  readonly editorOptions = getDefaultEditorOptions('sql');

  showSampleSolutions = false;

  constructor(private  apiService: ApiService, private dexieService: DexieService) {
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as ISqlExerciseContent;

    // FIXME: load db schema!
    this.apiService.getSqlDbSchema(this.exercise.collectionId)
      .subscribe((dbContents) => {
        dbContents.forEach((dbc) => console.info(JSON.stringify(dbc, null, 2)));
        this.dbContents = dbContents;
      });

    this.dexieService.getSolution(this.exercise, this.partId)
      .then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  correct(): void {
    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.upsertSolution(this.exercise, this.partId, this.solution);

    this.apiService.correctSolution<string, any>(this.exercise, this.partId, this.solution)
      .subscribe((result) => this.result = result);
  }

  toggleSampleSolutions(): void {
    this.showSampleSolutions = !this.showSampleSolutions;
  }

  activateModal(sqlQueryResult: ActivatableSqlQueryResult): void {
    this.dbContents.forEach((sqr) => sqr.active = false);

    sqlQueryResult.active = true;
  }

}
