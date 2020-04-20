import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {ToolPart} from '../../../../_interfaces/tool';
import {
  ExerciseSolveFieldsFragment,
  SqlExerciseContentSolveFieldsFragment,
  SqlSampleSolutionFragment
} from '../../../../_services/apollo_services';
import {
  DbContentsGQL,
  DbContentsQuery,
  SqlCorrectionGQL,
  SqlCorrectionMutation,
  SqlIllegalQueryResultFragment,
  SqlResultFragment,
  SqlWrongQueryTypeResultFragment
} from '../sql-apollo-mutations.service';
import {SqlExPart} from '../../../../_interfaces/graphql-types';

import 'codemirror/mode/sql/sql';


@Component({
  selector: 'it4all-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent
  extends ComponentWithExercise<string, string, SqlCorrectionMutation, SqlExPart, SqlCorrectionGQL, any>
  implements OnInit {

  readonly editorOptions = getDefaultEditorOptions('sql');

  @Input() oldPart: ToolPart;
  @Input() schemaName: string;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() exerciseContent: SqlExerciseContentSolveFieldsFragment;
  @Input() sampleSolutionFragments: SqlSampleSolutionFragment[];

  dbContentsQuery: DbContentsQuery;

  solution = '';

  constructor(sqlCorrectionGQL: SqlCorrectionGQL, dexieService: DexieService, private dbContentsGQL: DbContentsGQL) {
    super(sqlCorrectionGQL, dexieService);
  }

  ngOnInit() {
    this.dbContentsGQL
      .watch({schemaName: this.schemaName})
      .valueChanges
      .subscribe(({data}) => this.dbContentsQuery = data);

    this.dexieService.getSolution(
      this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.oldPart.id
    ).then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  protected getSolution(): string | undefined {
    return this.solution.length > 0 ? this.solution : undefined;
  }

  get sampleSolutions(): string[] {
    return this.sampleSolutionFragments.map((s) => s.sqlSampleSolution);
  }

  // Result types

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
    this.correctAbstract(this.exerciseFragment, SqlExPart.SqlSingleExPart, this.oldPart);
  }

}
