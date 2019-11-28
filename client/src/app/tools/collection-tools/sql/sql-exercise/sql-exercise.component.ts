import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {SqlCreateQueryPart} from '../sql-tool';
import {DbSqlSolution, SqlResult} from '../sql-interfaces';
import {ActivatedRoute} from '@angular/router';
import {ApiService} from '../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {ExerciseComponentHelpers} from '../../_helpers/ExerciseComponentHelpers';
import {ToolPart} from '../../../../_interfaces/tool';
import {IExercise, IExerciseCollection, ISqlExerciseContent} from '../../../../_interfaces/models';

import 'codemirror/mode/sql/sql';

@Component({
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent extends ExerciseComponentHelpers implements OnInit {

  collection: IExerciseCollection;
  exercise: IExercise;
  content: ISqlExerciseContent;
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

    this.apiService.getExercise(this.tool.id, collId, exId)
      .subscribe((ex) => {
        this.exercise = ex;
        this.content = ex.content as ISqlExerciseContent;
      });

    this.dexieService.sqlSolutions.get([collId, exId])
      .then((solution: DbSqlSolution | undefined) => this.solution = solution ? solution.solution : '');
  }

  correct(): void {
    const partId = 'solve';

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.sqlSolutions.put({
      toolId: this.tool.id,
      collId: this.collection.id,
      exId: this.exercise.id,
      partId,
      solution: this.solution
    });

    this.apiService.correctSolution<string, any>(this.exercise, partId, this.solution)
      .subscribe((result) => {
        this.result = result;
        console.warn(JSON.stringify(this.result, null, 2));
      });
  }

  toggleSampleSolutions(): void {
    this.showSampleSolutions = !this.showSampleSolutions;
  }
}
