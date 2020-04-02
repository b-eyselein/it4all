import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {ApiService} from '../../_services/api.service';
import {getDefaultEditorOptions} from '../../collection-tool-helpers';
import {DexieService} from '../../../../_services/dexie.service';
import {DbSolution} from '../../../../_interfaces/exercise';
import {ComponentWithExercise} from '../../_helpers/component-with-exercise';
import {ISqlQueryResult} from '../sql-interfaces';
import {ToolPart} from '../../../../_interfaces/tool';
import {
  ExerciseSolveFieldsFragment,
  SqlExerciseContentSolveFieldsFragment
} from '../../../../_services/apollo_services';
import {SqlExPart} from '../../../../_services/apollo-mutation.service';
import {
  SqlCorrectionGQL,
  SqlCorrectionMutation,
  SqlIllegalQueryResultFragment,
  SqlResultFragment,
  SqlWrongQueryTypeResultFragment
} from '../sql-apollo-service';


import 'codemirror/mode/sql/sql';


@Component({
  selector: 'it4all-sql-exercise',
  templateUrl: './sql-exercise.component.html',
  styleUrls: ['./sql-exercise.component.sass'],
  encapsulation: ViewEncapsulation.None // style editor also
})
export class SqlExerciseComponent
  extends ComponentWithExercise<string, SqlCorrectionMutation, SqlExPart, SqlCorrectionGQL, any>
  implements OnInit {

  readonly editorOptions = getDefaultEditorOptions('sql');

  @Input() oldPart: ToolPart;
  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() sqlExerciseContent: SqlExerciseContentSolveFieldsFragment;

  dbContents: ISqlQueryResult[] = [];

  solution = '';

  constructor(sqlCorrectionGQL: SqlCorrectionGQL, apiService: ApiService, dexieService: DexieService) {
    super(sqlCorrectionGQL, apiService, dexieService);
  }

  ngOnInit() {
    this.apiService.getSqlDbSchema(this.exerciseFragment.collectionId)
      .subscribe((dbContents) => this.dbContents = dbContents);

    this.dexieService.getSolution(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, this.oldPart.id)
      .then((solution: DbSolution<string> | undefined) => this.solution = solution ? solution.solution : '');
  }

  protected getSolution(): string | undefined {
    return this.solution.length > 0 ? this.solution : undefined;
  }

  get sampleSolutions(): string[] {
    return this.sqlExerciseContent.sqlSampleSolutions.map((s) => s.sample);
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
    this.correctAbstract(this.exerciseFragment.id, this.exerciseFragment.collectionId, this.exerciseFragment.toolId, SqlExPart.SqlSingleExPart, this.oldPart);
  }

}
