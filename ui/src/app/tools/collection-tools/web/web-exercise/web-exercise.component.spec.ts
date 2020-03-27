import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {WebExerciseComponent} from './web-exercise.component';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ExerciseFilesEditorComponent} from '../../_components/exercise-files-editor/exercise-files-editor.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SolutionSavedComponent} from '../../../../shared/solution-saved/solution-saved.component';
import {PointsNotificationComponent} from '../../../../shared/points-notification/points-notification.component';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {ApiService} from '../../_services/api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HtmlPart} from '../web-tool';

describe('WebExerciseComponent', () => {

  let component: WebExerciseComponent;
  let fixture: ComponentFixture<WebExerciseComponent>;


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, RouterTestingModule, CodemirrorModule, HttpClientTestingModule],
      declarations: [
        WebExerciseComponent,
        TabsComponent, TabComponent,
        ExerciseFilesEditorComponent,
        SolutionSavedComponent, PointsNotificationComponent
      ],
      providers: [ApiService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WebExerciseComponent);
    component = fixture.componentInstance;
    component.part = HtmlPart;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
