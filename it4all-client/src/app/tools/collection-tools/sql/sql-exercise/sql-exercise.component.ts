import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {SqlCreateQueryPart, SqlTool} from '../sql-tool';
import {SqlResult} from '../sql-interfaces';
import {ApiService} from '../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {ToolPart} from '../../../../_interfaces/tool';
import {IExercise, ISqlExerciseContent} from '../../../../_interfaces/models';
import {DbSolution} from '../../../../_interfaces/exercise';

import 'codemirror/mode/sql/sql';


@Component({
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent implements OnInit {

  tool = SqlTool;
  part: ToolPart = SqlCreateQueryPart;

  @Input() exercise: IExercise;
  exerciseContent: ISqlExerciseContent;

  solution = '';
  result: SqlResult | undefined;

  readonly editorOptions = getDefaultEditorOptions('sql');

  showSampleSolutions = false;

  constructor(private  apiService: ApiService, private dexieService: DexieService) {
  }

  ngOnInit() {
    this.exerciseContent = this.exercise.content as ISqlExerciseContent;

    this.dexieService.solutions.get([this.tool.id, this.exercise.collectionId, this.exercise.id, this.part.id])
      .then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  correct(): void {
    const partId = 'solve';

    // noinspection JSIgnoredPromiseFromCall
    this.dexieService.solutions.put({
      toolId: this.tool.id,
      collId: this.exercise.collectionId,
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
