import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {UmlExerciseComponent} from './uml-exercise.component';
import {UmlClassSelectionComponent} from '../uml-class-selection/uml-class-selection.component';
import {RouterTestingModule} from '@angular/router/testing';

describe('UmlExerciseComponent', () => {

  let component: UmlExerciseComponent;
  let fixture: ComponentFixture<UmlExerciseComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [UmlExerciseComponent, UmlClassSelectionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
