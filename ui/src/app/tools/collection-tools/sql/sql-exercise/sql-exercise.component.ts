import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {
  ExerciseSolveFieldsFragment,
  SqlCorrectionGQL,
  SqlCorrectionMutation,
  SqlCorrectionMutationVariables,
  SqlCorrectionResultFragment,
  SqlExerciseContentFragment,
  SqlExPart,
  SqlResultFragment
} from '../../../../_services/apollo_services';

import 'codemirror/mode/sql/sql';
import {HasSampleSolutions} from "../../_helpers/correction-helpers";
import {DexieService} from "../../../../_services/dexie.service";


function getIdForSqlExPart(sqlExPart: SqlExPart): string {
  switch (sqlExPart) {
    case SqlExPart.SqlSingleExPart:
      return 'solve';
  }
}

@Component({
  selector: 'it4all-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent
  extends ComponentWithExerciseDirective<string, SqlCorrectionMutation, SqlCorrectionMutationVariables>
  implements OnInit, HasSampleSolutions<string> {

  readonly editorOptions = getDefaultEditorOptions('sql');

  readonly partId = getIdForSqlExPart(SqlExPart.SqlSingleExPart);

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: SqlExerciseContentFragment;

  solution = '';

  constructor(sqlCorrectionGQL: SqlCorrectionGQL, dexieService: DexieService) {
    super(sqlCorrectionGQL, dexieService);
  }

  ngOnInit() {
    this.dexieService
      .getSolution(this.exerciseFragment, this.partId)
      .then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  // Result types

  get correctionResult(): SqlCorrectionResultFragment | null {
    return this.resultQuery?.me.sqlExercise?.correct;
  }

  get sqlResult(): SqlResultFragment | null {
    return this.correctionResult?.result;
  }

  // Correction

  protected getSolution(): string | undefined {
    return this.solution.length > 0 ? this.solution : undefined;
  }

  protected getMutationQueryVariables(): SqlCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part: SqlExPart.SqlSingleExPart,
    };
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.partId);
  }

  // Sample solutions

  displaySampleSolutions = false;

  toggleSampleSolutions() {
    this.displaySampleSolutions = !this.displaySampleSolutions;
  }

  get sampleSolutions(): string[] {
    return this.contentFragment.sqlSampleSolutions;
  }

}
