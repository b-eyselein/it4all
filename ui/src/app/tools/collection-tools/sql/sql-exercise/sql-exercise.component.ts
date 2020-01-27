import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {ApiService} from '../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {IExercise} from '../../../../_interfaces/models';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {ISqlExerciseContent, ISqlQueryResult, ISqlResult} from '../sql-interfaces';

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
export class SqlExerciseComponent extends ComponentWithExercise<string, ISqlResult> implements OnInit {

  readonly partId = 'solve';
  readonly editorOptions = getDefaultEditorOptions('sql');

  @Input() exercise: IExercise;
  exerciseContent: ISqlExerciseContent;

  dbContents: ActivatableSqlQueryResult[] = [];

  solution = '';

  showSampleSolutions = false;

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as ISqlExerciseContent;

    // FIXME: load db schema!
    this.apiService.getSqlDbSchema(this.exercise.collectionId)
      .subscribe((dbContents) => {
        // dbContents.forEach((dbc) => console.info(JSON.stringify(dbc, null, 2)));
        this.dbContents = dbContents;
      });

    this.dexieService.getSolution(this.exercise, this.partId)
      .then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  protected getSolution(): string {
    return this.solution;
  }

  correct(): void {
    this.correctAbstract(this.exercise, {id: this.partId, name: ''}, true);
  }

  toggleSampleSolutions(): void {
    this.showSampleSolutions = !this.showSampleSolutions;
  }

  activateModal(sqlQueryResult: ActivatableSqlQueryResult): void {
    this.dbContents.forEach((sqr) => sqr.active = false);

    sqlQueryResult.active = true;
  }

}