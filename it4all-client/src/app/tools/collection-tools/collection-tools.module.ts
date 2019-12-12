import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RegexMatchingResultComponent} from './regex/regex-matching-result/regex-matching-result.component';
import {RegexExtractionResultComponent} from './regex/regex-extraction-result/regex-extraction-result.component';
import {RegexExtractionMatchComponent} from './regex/regex-extraction-result/regex-extraction-match/regex-extraction-match.component';
import {RegexCheatsheetComponent} from './regex/regex-cheatsheet/regex-cheatsheet.component';
import {ProgrammingSimplifiedResultComponent} from './programming/_results/programming-simplified-result/programming-simplified-result.component';
import {ProgrammingUnittestResultComponent} from './programming/_results/programming-unittest-result/programming-unittest-result.component';
import {SqlResultComponent} from './sql/sql-result/sql-result.component';
import {SqlMatchingResultComponent} from './sql/sql-matching-result/sql-matching-result.component';
import {collectionToolRoutingComponents, CollectionToolRoutingModule} from './collection-tools.routing';
import {FormsModule} from '@angular/forms';
import {ExerciseFilesEditorComponent} from './_components/exercise-files-editor/exercise-files-editor.component';
import {ExerciseFileCardComponent} from './_components/exercise-file-card/exercise-file-card.component';
import {SharedModule} from '../../shared/shared.module';
import {TagComponent} from './_components/tag/tag.component';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {ApiService} from './_services/api.service';
import {ExerciseOverviewComponent} from './exercise-overview/exercise-overview.component';
import {ExerciseComponent} from './exercise/exercise.component';
import {RegexExerciseComponent} from './regex/regex-exercise/regex-exercise.component';
import {ProgrammingExerciseComponent} from './programming/programming-exercise/programming-exercise.component';
import {SqlExerciseComponent} from './sql/sql-exercise/sql-exercise.component';
import {UmlExerciseComponent} from './uml/uml-exercise/uml-exercise.component';
import {WebExerciseComponent} from './web/web-exercise/web-exercise.component';
import { UmlClassSelectionComponent } from './uml/uml-class-selection/uml-class-selection.component';
import { AllExercisesOverviewComponent } from './all-exercises-overview/all-exercises-overview.component';


@NgModule({
  declarations: [
    ExerciseFilesEditorComponent,
    ExerciseFileCardComponent,
    TagComponent,

    RegexExerciseComponent, RegexMatchingResultComponent, RegexExtractionResultComponent,
    RegexExtractionMatchComponent, RegexCheatsheetComponent,

    ProgrammingExerciseComponent, ProgrammingSimplifiedResultComponent, ProgrammingUnittestResultComponent,

    SqlExerciseComponent, SqlResultComponent, SqlMatchingResultComponent,

    UmlExerciseComponent,

    WebExerciseComponent,

    ...collectionToolRoutingComponents,

    ExerciseOverviewComponent,

    ExerciseComponent,

    UmlClassSelectionComponent,

    AllExercisesOverviewComponent
  ],
  providers: [ApiService],
  imports: [
    CommonModule,
    FormsModule,
    CodemirrorModule,
    SharedModule,
    CollectionToolRoutingModule
  ]
})
export class CollectionToolsModule {
}
