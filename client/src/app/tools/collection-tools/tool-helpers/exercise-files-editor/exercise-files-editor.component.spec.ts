import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseFilesEditorComponent } from './exercise-files-editor.component';

describe('ExerciseFilesEditorComponent', () => {
  let component: ExerciseFilesEditorComponent;
  let fixture: ComponentFixture<ExerciseFilesEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExerciseFilesEditorComponent ]
    })
    .compileComponents();
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
