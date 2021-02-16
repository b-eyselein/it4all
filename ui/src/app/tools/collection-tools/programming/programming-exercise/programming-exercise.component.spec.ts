import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {ProgrammingExerciseComponent} from './programming-exercise.component';
import {ExerciseFilesEditorComponent} from '../../_components/exercise-files-editor/exercise-files-editor.component';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {ProgrammingSimplifiedResultComponent} from '../_results/programming-simplified-result/programming-simplified-result.component';
import {ExerciseFileCardComponent} from '../../_components/exercise-file-card/exercise-file-card.component';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {FormsModule} from '@angular/forms';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ProgExPart} from "../../../../_services/apollo_services";


describe('ProgrammingExerciseComponent', () => {

  let component: ProgrammingExerciseComponent;
  let fixture: ComponentFixture<ProgrammingExerciseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, CodemirrorModule, HttpClientTestingModule],
      declarations: [
        ProgrammingExerciseComponent, ExerciseFilesEditorComponent,
        TabComponent, TabsComponent,
        SolutionSavedComponent, ProgrammingSimplifiedResultComponent,
        ExerciseFileCardComponent, ExerciseFilesEditorComponent
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingExerciseComponent);
    component = fixture.componentInstance;
    component.exerciseFiles = [];
    component.exerciseFragment = {
      exerciseId: 0,
      collectionId: 0,
      toolId: 'programming',
      title: '',
      text: '',
      content: {
        __typename: 'ProgrammingExerciseContent',
        implementationPart: {files: []},
        programmingPart: ProgExPart.Implementation,
        programmingSampleSolutions: [],
        unitTestPart: {
          __typename: "NormalUnitTestPart",
          unitTestFiles: [],
        }
      }
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
