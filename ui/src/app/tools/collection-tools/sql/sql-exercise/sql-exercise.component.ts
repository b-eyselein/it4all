import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {ApiService} from '../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {ISqlQueryResult, ISqlResult} from '../sql-interfaces';

import 'codemirror/mode/sql/sql';
import {ToolPart} from "../../../../_interfaces/tool";
import {
  ExerciseSolveFieldsFragment,
  SqlExerciseContentSolveFieldsFragment
} from "../../../../_services/apollo_services";


@Component({
  selector: 'it4all-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent extends ComponentWithExercise<string, ISqlResult> implements OnInit {

  readonly editorOptions = getDefaultEditorOptions('sql');

  @Input() part: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() sqlExerciseContent: SqlExerciseContentSolveFieldsFragment;

  dbContents: ISqlQueryResult[] = [];

  solution = '';

  constructor(apiService: ApiService, dexieService: DexieService) {
    super(apiService, dexieService);
  }

  ngOnInit() {
    this.apiService.getSqlDbSchema(this.exerciseFragment.collectionId)
      .subscribe((dbContents) => this.dbContents = dbContents);

    this.dexieService.getSolution(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part.id)
      .then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  protected getSolution(): string {
    return this.solution;
  }

  get sampleSolutions(): string[] {
    return this.sqlExerciseContent.sqlSampleSolutions.map((s) => s.sample);
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.part, true);
  }

}
