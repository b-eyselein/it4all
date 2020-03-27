import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {ApiService} from '../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {IExercise} from '../../../../_interfaces/models';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {ISqlExerciseContent, ISqlQueryResult, ISqlResult} from '../sql-interfaces';
import {SqlCreateQueryPart} from "../sql-tool";

import 'codemirror/mode/sql/sql';


@Component({
  selector: 'it4all-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent extends ComponentWithExercise<string, ISqlResult> implements OnInit {

  readonly part = SqlCreateQueryPart;
  readonly editorOptions = getDefaultEditorOptions('sql');

  @Input() exercise: IExercise;

  dbContents: ISqlQueryResult[] = [];

  solution = '';

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit() {
    this.apiService.getSqlDbSchema(this.exercise.collectionId)
      .subscribe((dbContents) => this.dbContents = dbContents);

    this.dexieService.getSolution(this.exercise.id, this.exercise.collectionId, this.exercise.toolId, this.part.id)
      .then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  protected getSolution(): string {
    return this.solution;
  }

  get sampleSolutions(): string[] {
    const exContent = this.exercise.content as ISqlExerciseContent;

    return exContent.sampleSolutions.map((sample) => sample.sample);
  }

  correct(): void {
    this.correctAbstract(this.exercise.id, this.exercise.collectionId, this.exercise.toolId, this.part, true);
  }

}
