import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {ExerciseSolveFieldsFragment, SqlExerciseContentFragment} from '../../../../_services/apollo_services';
import {
  SqlAbstractResultFragment,
  SqlCorrectionGQL,
  SqlCorrectionMutation,
  SqlCorrectionMutationVariables,
  SqlCorrectionResultFragment,
  SqlInternalErrorResultFragment,
  SqlResultFragment
} from '../sql-apollo-mutations.service';
import {SqlExPart, SqlInternalErrorResult} from '../../../../_interfaces/graphql-types';
import {AuthenticationService} from '../../../../_services/authentication.service';

import 'codemirror/mode/sql/sql';
import {HasSampleSolutions} from "../../_helpers/correction-helpers";


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
  extends ComponentWithExerciseDirective<string, SqlCorrectionMutation, SqlExPart, SqlCorrectionMutationVariables, SqlCorrectionGQL>
  implements OnInit, HasSampleSolutions<string> {

  readonly editorOptions = getDefaultEditorOptions('sql');

  readonly partId = getIdForSqlExPart(SqlExPart.SqlSingleExPart);

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: SqlExerciseContentFragment;

  solution = '';

  constructor(private authenticationService: AuthenticationService, sqlCorrectionGQL: SqlCorrectionGQL, dexieService: DexieService) {
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

  get abstractResult(): SqlAbstractResultFragment & (SqlInternalErrorResultFragment | SqlResultFragment) | null {
    return this.correctionResult?.result;
  }

  get sqlInternalErrorResult(): SqlInternalErrorResult | null {
    return this.abstractResult?.__typename === 'SqlInternalErrorResult' ? this.abstractResult : null;
  }

  get sqlResult(): SqlResultFragment | null {
    return this.abstractResult?.__typename === 'SqlResult' ? this.abstractResult : null;
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
      userJwt: this.authenticationService.currentUserValue.jwt
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
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

}
