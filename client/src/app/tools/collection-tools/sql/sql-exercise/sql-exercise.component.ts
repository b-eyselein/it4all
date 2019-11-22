import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {SqlCreateQueryPart} from '../sql-tool';
import {DbSqlSolution, SqlExerciseContent, SqlResult} from '../sql-interfaces';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';

import 'codemirror/mode/sql/sql';
import {ExerciseComponentHelpers} from '../../_helpers/ExerciseComponentHelpers';
import {Exercise, ExerciseCollection} from '../../../../_interfaces/exercise';
import {ToolPart} from '../../../../_interfaces/tool';

@Component({
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent extends ExerciseComponentHelpers<SqlExerciseContent> implements OnInit {

  collection: ExerciseCollection;
  exercise: Exercise<SqlExerciseContent>;
  part: ToolPart = SqlCreateQueryPart;

  solution = '';
  result: SqlResult | undefined;

  readonly editorOptions = getDefaultEditorOptions('sql');

  showSampleSolutions = false;

  constructor(private route: ActivatedRoute, private  apiService: ApiService, private dexieService: DexieService) {
    super(route);
  }

  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
    const exId: number = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((coll) => this.collection = coll);

    this.apiService.getExercise<SqlExerciseContent>(this.tool.id, collId, exId)
      .subscribe((ex) => this.exercise = ex);

    this.dexieService.sqlSolutions.get([collId, exId])
      .then((solution: DbSqlSolution | undefined) => this.solution = solution ? solution.solution : '');
  }

  correct(): void {
    const partId = 'solve';

    this.dexieService.sqlSolutions.put({
      toolId: this.tool.id,
      collId: this.collection.id,
      exId: this.exercise.id,
      partId,
      solution: this.solution
    });

    this.apiService.correctSolution<string, any>(this.tool.id, this.collection.id, this.exercise.id, partId, this.solution)
      .subscribe((result) => {
        this.result = result;
        console.warn(JSON.stringify(this.result, null, 2));
      });
  }

  toggleSampleSolutions(): void {
    this.showSampleSolutions = !this.showSampleSolutions;
  }
}
