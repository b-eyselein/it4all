import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {ToolPart} from '../../../../_interfaces/tool';
import {
  ExerciseSolveFieldsFragment,
  SqlExerciseContentSolveFieldsFragment
} from '../../../../_services/apollo_services';
import {
  SqlCorrectionGQL,
  SqlCorrectionMutation,
  SqlCorrectionMutationVariables,
  SqlIllegalQueryResultFragment,
  SqlResultFragment,
  SqlWrongQueryTypeResultFragment
} from '../sql-apollo-mutations.service';
import {SqlExPart, SqlInternalErrorResult} from '../../../../_interfaces/graphql-types';

import 'codemirror/mode/sql/sql';
import {SqlCreateQueryPart} from "../sql-tool";
import {AuthenticationService} from "../../../../_services/authentication.service";


@Component({
  selector: 'it4all-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent
  extends ComponentWithExercise<string, string, SqlCorrectionMutation, SqlExPart, SqlCorrectionMutationVariables, SqlCorrectionGQL>
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

  get sqlInternalErrorResult(): SqlInternalErrorResult | undefined {
    return this.resultQuery?.correctSql.__typename === 'SqlInternalErrorResult' ? this.resultQuery.correctSql : undefined;
  }

  get sqlIllegalQueryResult(): SqlIllegalQueryResultFragment | undefined {
    return this.resultQuery?.correctSql.__typename === 'SqlIllegalQueryResult' ? this.resultQuery.correctSql : undefined;
  }

  get sqlWrongQueryTypeResult(): SqlWrongQueryTypeResultFragment | undefined {
    return this.resultQuery?.correctSql.__typename === 'SqlWrongQueryTypeResult' ? this.resultQuery.correctSql : undefined;
  }

  get sqlResult(): SqlResultFragment | undefined {
    return this.resultQuery?.correctSql.__typename === 'SqlResult' ? this.resultQuery.correctSql : undefined;
  }

  // Correction

  correct(): void {
    this.correctAbstract(this.exerciseFragment, SqlExPart.SqlSingleExPart, this.oldPart.id);
  }


}
