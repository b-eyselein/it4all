import {BrowserModule, Title} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {ToolOverviewComponent} from './tool-overview/tool-overview.component';
import {AppRoutingModule} from './app-routing.module';
import {BoolFillOutComponent} from './tools/random-tools/bool/bool-fill-out/bool-fill-out.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {ToolIndexComponent} from './tools/collection-tools/tool-index/tool-index.component';
import {CollectionIndexComponent} from './tools/collection-tools/collection-index/collection-index.component';
import {RegexExerciseComponent} from './tools/collection-tools/regex/regex-exercise/regex-exercise.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoginFormComponent} from './user_management/login-form/login-form.component';
import {JwtInterceptor} from './_helpers/jwt.interceptor';
import {ErrorInterceptor} from './_helpers/error.interceptor';
import {RandomOverviewComponent} from './tools/random-tools/random-overview/random-overview.component';
import {NaryAdditionComponent} from './tools/random-tools/nary/nary-addition/nary-addition.component';
import {NaryConversionComponent} from './tools/random-tools/nary/nary-conversion/nary-conversion.component';
import {NaryNumberReadOnlyInputComponent} from './tools/random-tools/nary/_components/nary-number-read-only-input/nary-number-read-only-input.component';
import {NaryTwoConversionComponent} from './tools/random-tools/nary/nary-two-conversion/nary-two-conversion.component';
import {BoolCreateComponent} from './tools/random-tools/bool/bool-create/bool-create.component';
import {SolutionSavedComponent} from './tools/collection-tools/tool-helpers/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from './tools/collection-tools/tool-helpers/points-notification/points-notification.component';
import {RegexMatchingResultComponent} from './tools/collection-tools/regex/regex-matching-result/regex-matching-result.component';
import {RegexExtractionResultComponent} from './tools/collection-tools/regex/regex-extraction-result/regex-extraction-result.component';
import {WebExerciseComponent} from './tools/collection-tools/web/web-exercise/web-exercise.component';
import {ProgrammingExerciseComponent} from './tools/collection-tools/programming/programming-exercise/programming-exercise.component';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {ExerciseFilesEditorComponent} from './tools/collection-tools/tool-helpers/exercise-files-editor/exercise-files-editor.component';
import {TabComponent} from './_component_helpers/tab/tab.component';
import {TabsComponent} from './_component_helpers/tabs/tabs.component';
import {SqlExerciseComponent} from './tools/collection-tools/sql/sql-exercise/sql-exercise.component';
import {BoolCreateInstructionsComponent} from './tools/random-tools/bool/bool-create/bool-create-instructions/bool-create-instructions.component';
import {ExerciseFileCardComponent} from './tools/collection-tools/tool-helpers/exercise-file-card/exercise-file-card.component';
import {RandomSolveButtonsComponent} from './tools/random-tools/_components/random-solve-buttons/random-solve-buttons.component';
import {LtiComponent} from './lti/lti.component';
import { AdminIndexComponent } from './admin/admin-index/admin-index.component';
import { CollToolAdminComponent } from './admin/coll-tool-admin/coll-tool-admin.component';
import { AdminReadCollectionsComponent } from './admin/admin-read-collections/admin-read-collections.component';


@NgModule({
  declarations: [
    AppComponent,
    ToolOverviewComponent,
    BoolFillOutComponent,
    ToolIndexComponent,
    CollectionIndexComponent,
    RegexExerciseComponent,
    LoginFormComponent,
    RandomOverviewComponent,
    NaryAdditionComponent,
    NaryConversionComponent,
    NaryNumberReadOnlyInputComponent,
    NaryTwoConversionComponent,
    BoolCreateComponent,
    SolutionSavedComponent,
    ProgrammingExerciseComponent,
    PointsNotificationComponent,
    RegexMatchingResultComponent,
    RegexExtractionResultComponent,
    WebExerciseComponent,
    ExerciseFilesEditorComponent,
    TabComponent,
    TabsComponent,
    SqlExerciseComponent,
    BoolCreateInstructionsComponent,
    ExerciseFileCardComponent,
    RandomSolveButtonsComponent,
    LtiComponent,
    AdminIndexComponent,
    CollToolAdminComponent,
    AdminReadCollectionsComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    CodemirrorModule
  ],
  providers: [
    Title,
    {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
