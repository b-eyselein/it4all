import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {ExerciseFilesEditorComponent} from './exercise-files-editor.component';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {FormsModule} from '@angular/forms';

describe('ExerciseFilesEditorComponent', () => {
  let component: ExerciseFilesEditorComponent;
  let fixture: ComponentFixture<ExerciseFilesEditorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, CodemirrorModule],
      declarations: [ExerciseFilesEditorComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExerciseFilesEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
