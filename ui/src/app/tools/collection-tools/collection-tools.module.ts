import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RegexMatchingResultComponent} from './regex/regex-matching-result/regex-matching-result.component';
import {RegexExtractionResultComponent} from './regex/regex-extraction-result/regex-extraction-result.component';
import {RegexExtractionMatchComponent} from './regex/regex-extraction-result/regex-extraction-match/regex-extraction-match.component';
import {RegexCheatsheetComponent} from './regex/regex-cheatsheet/regex-cheatsheet.component';
import {ProgrammingSimplifiedResultComponent} from './programming/_results/programming-simplified-result/programming-simplified-result.component';
import {ProgrammingUnittestResultComponent} from './programming/_results/programming-unittest-result/programming-unittest-result.component';
import {SqlResultComponent} from './sql/_results/sql-result/sql-result.component';
import {SqlMatchingResultComponent} from './sql/_results/sql-matching-result/sql-matching-result.component';
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
import {UmlClassSelectionComponent} from './uml/uml-class-selection/uml-class-selection.component';
import {AllExercisesOverviewComponent} from './all-exercises-overview/all-exercises-overview.component';
import {XmlExerciseComponent} from './xml/xml-exercise/xml-exercise.component';
import {SqlExecutionResultComponent} from './sql/_results/sql-execution-result/sql-execution-result.component';
import {UmlDiagramDrawingComponent} from './uml/uml-diagram-drawing/uml-diagram-drawing.component';
import {UmlClassEditComponent} from './uml/_components/uml-class-edit/uml-class-edit.component';
import {UmlAssocEditComponent} from './uml/_components/uml-assoc-edit/uml-assoc-edit.component';
import {ExerciseLinkCardComponent} from './_components/exercise-link-card/exercise-link-card.component';
import {UmlDiagDrawingCorrectionComponent} from './uml/_components/uml-diag-drawing-correction/uml-diag-drawing-correction.component';
import { UmlAssocMatchComponent } from './uml/_components/uml-assoc-match/uml-assoc-match.component';
import { UmlImplMatchComponent } from './uml/_components/uml-impl-match/uml-impl-match.component';
import { UmlMemberAllocationComponent } from './uml/uml-member-allocation/uml-member-allocation.component';
import { XmlElementLineMatchComponent } from './xml/_components/xml-element-line-match/xml-element-line-match.component';
import { XmlDocumentCorrectionComponent } from './xml/_components/xml-document-correction/xml-document-correction.component';


@NgModule({
  declarations: [
    ...collectionToolRoutingComponents,

    ExerciseFilesEditorComponent, ExerciseFileCardComponent,

    TagComponent,

    AllExercisesOverviewComponent,
    ExerciseOverviewComponent,
    ExerciseLinkCardComponent,

    ExerciseComponent,

    RegexExerciseComponent, RegexMatchingResultComponent, RegexExtractionResultComponent, RegexExtractionMatchComponent,
    RegexCheatsheetComponent,

    ProgrammingExerciseComponent, ProgrammingSimplifiedResultComponent, ProgrammingUnittestResultComponent,

    SqlExerciseComponent, SqlResultComponent, SqlMatchingResultComponent, SqlExecutionResultComponent,

    UmlDiagramDrawingComponent, UmlClassEditComponent, UmlAssocEditComponent, UmlExerciseComponent, UmlClassSelectionComponent,
    UmlDiagDrawingCorrectionComponent,

    WebExerciseComponent,

    XmlExerciseComponent,

    UmlAssocMatchComponent,

    UmlImplMatchComponent,

    UmlMemberAllocationComponent,

    XmlElementLineMatchComponent,

    XmlDocumentCorrectionComponent
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
