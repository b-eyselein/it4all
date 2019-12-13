import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProgrammingExerciseComponent} from './programming-exercise.component';
import {ExerciseFilesEditorComponent} from '../../_components/exercise-files-editor/exercise-files-editor.component';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {ProgrammingSimplifiedResultComponent} from '../_results/programming-simplified-result/programming-simplified-result.component';
import {ProgrammingUnittestResultComponent} from '../_results/programming-unittest-result/programming-unittest-result.component';
import {ExerciseFileCardComponent} from '../../_components/exercise-file-card/exercise-file-card.component';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {FormsModule} from '@angular/forms';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {ApiService} from '../../_services/api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {IProgExerciseContent} from '../../../../_interfaces/models';


describe('ProgrammingExerciseComponent', () => {

  let component: ProgrammingExerciseComponent;
  let fixture: ComponentFixture<ProgrammingExerciseComponent>;

  const exerciseContent: IProgExerciseContent = {
    inputTypes: [],
    maybeClassDiagramPart: undefined,
    outputType: {},
    sampleSolutions: [],
    sampleTestData: [],
    unitTestPart: {
      sampleSolFileNames: [],
      simplifiedTestMainFile: undefined,
      testFileName: '',
      unitTestFiles: [],
      unitTestsDescription: '',
      unitTestTestConfigs: [],
      unitTestType: 'Normal'
    },
    baseData: [],
    filename: '',
    foldername: '',
    functionName: '',
    implementationPart: {
      base: '',
      files: [],
      implFileName: '',
      sampleSolFileNames: []
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, CodemirrorModule, HttpClientTestingModule],
      declarations: [
        ProgrammingExerciseComponent, ExerciseFilesEditorComponent,
        TabComponent, TabsComponent,
        SolutionSavedComponent, ProgrammingSimplifiedResultComponent, ProgrammingUnittestResultComponent,
        ExerciseFileCardComponent, ExerciseFilesEditorComponent
      ],
      providers: [ApiService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingExerciseComponent);
    component = fixture.componentInstance;
    component.exerciseFiles = [];
    component.exercise = {
      id: 0, collectionId: 0, toolId: 'programming', semanticVersion: {major: 0, minor: 0, patch: 0},
      title: '', authors: [], text: '', tags: [], content: exerciseContent
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
