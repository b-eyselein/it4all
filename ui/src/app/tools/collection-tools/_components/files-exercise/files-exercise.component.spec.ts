import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FilesExerciseComponent} from './files-exercise.component';

describe('FilesExerciseComponent', () => {
  let component: FilesExerciseComponent;
  let fixture: ComponentFixture<FilesExerciseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FilesExerciseComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FilesExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
