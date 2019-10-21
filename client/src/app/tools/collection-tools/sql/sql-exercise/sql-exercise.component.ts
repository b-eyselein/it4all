import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {SqlCreateQueryPart, SqlTool} from '../../collection-tools-list';
import {ExerciseCollection, Tool, ToolPart} from '../../../../_interfaces/tool';
import {DbSqlSolution, SqlExercise, SqlResult} from '../sql-exercise';
import {ActivatedRoute, Router} from '@angular/router';
import {ApiService} from '../../../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';

import 'codemirror/mode/sql/sql';

@Component({
  // selector: 'app-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent implements OnInit {

   readonly tool: Tool = SqlTool;
   collection: ExerciseCollection;
   exercise: SqlExercise;
   part: ToolPart = SqlCreateQueryPart;

   solution = '';
   result: SqlResult;

   editorOptions = getDefaultEditorOptions('sql');

  constructor(private route: ActivatedRoute, private  apiService: ApiService, private dexieService: DexieService, private router: Router) {
  }

  ngOnInit() {
    const collId: number = parseInt(this.route.snapshot.paramMap.get('collId'), 10);
    const exId: number = parseInt(this.route.snapshot.paramMap.get('exId'), 10);

    this.apiService.getCollection(this.tool.id, collId)
      .subscribe((coll) => this.collection = coll);

    this.apiService.getExercise<SqlExercise>(this.tool.id, collId, exId)
      .subscribe((ex) => this.exercise = ex);

    this.dexieService.sqlSolutions.get([collId, exId])
      .then((solution: DbSqlSolution | undefined) => this.solution = solution ? solution.solution : '');
  }

  correct(): void {
    this.dexieService.sqlSolutions.put({collId: this.collection.id, exId: this.exercise.id, solution: this.solution});

    const partId = 'solve';

    this.apiService.correctSolution<string, any>(this.tool.id, this.collection.id, this.exercise.id, partId, this.solution)
      .subscribe((result) => {
        this.result = result;
        console.warn(JSON.stringify(this.result, null, 2));
      });
  }

  showSampleSolutions(): void {

  }
}
