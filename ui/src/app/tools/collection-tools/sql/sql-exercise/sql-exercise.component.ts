import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {ToolPart} from '../../../../_interfaces/tool';
import {ExerciseSolveFieldsFragment, SqlExerciseContentSolveFieldsFragment} from '../../../../_services/apollo_services';
import {
  SqlAbstractResultFragment,
  SqlCorrectionGQL,
  SqlCorrectionMutation,
  SqlCorrectionMutationVariables,
  SqlInternalErrorResultFragment,
  SqlResultFragment
} from '../sql-apollo-mutations.service';
import {SqlExPart, SqlInternalErrorResult} from '../../../../_interfaces/graphql-types';

import 'codemirror/mode/sql/sql';
import {SqlCreateQueryPart} from '../sql-tool';
import {AuthenticationService} from '../../../../_services/authentication.service';


@Component({
  selector: 'it4all-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent
  extends ComponentWithExerciseDirective<string, string, SqlCorrectionMutation, SqlExPart, SqlCorrectionMutationVariables, SqlCorrectionGQL>
  implements OnInit {

  readonly editorOptions = getDefaultEditorOptions('sql');

  readonly oldPart: ToolPart = SqlCreateQueryPart;

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: SqlExerciseContentSolveFieldsFragment;

  solution = '';

  constructor(private authenticationService: AuthenticationService, sqlCorrectionGQL: SqlCorrectionGQL, dexieService: DexieService) {
    super(sqlCorrectionGQL, dexieService);
  }

  ngOnInit() {
    this.dexieService
      .getSolution(this.exerciseFragment, this.oldPart.id)
      .then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  protected getSolution(): string | undefined {
    return this.solution.length > 0 ? this.solution : undefined;
  }

  get sampleSolutions(): string[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

  protected getMutationQueryVariables(part: SqlExPart): SqlCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part,
      userJwt: this.authenticationService.currentUserValue.jwt
    };
  }

  // Result types

  get sqlAbstractResult(): SqlAbstractResultFragment & (SqlInternalErrorResultFragment | SqlResultFragment) | null {
    return this.resultQuery?.me.sqlExercise?.correct;
  }

  get sqlInternalErrorResult(): SqlInternalErrorResult | null {
    return this.sqlAbstractResult?.__typename === 'SqlInternalErrorResult' ? this.sqlAbstractResult : null;
  }

  get sqlResult(): SqlResultFragment | null {
    return this.sqlAbstractResult?.__typename === 'SqlResult' ? this.sqlAbstractResult : null;
  }

  // Correction

  correct(): void {
    this.correctAbstract(this.exerciseFragment, SqlExPart.SqlSingleExPart, this.oldPart.id);
  }


}
