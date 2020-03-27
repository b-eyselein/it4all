import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UmlExerciseComponent} from './uml-exercise.component';
import {UmlClassSelectionComponent} from '../uml-class-selection/uml-class-selection.component';
import {RouterTestingModule} from '@angular/router/testing';
import {UmlClassSelectionPart} from '../uml-tools';

describe('UmlExerciseComponent', () => {

  let component: UmlExerciseComponent;
  let fixture: ComponentFixture<UmlExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [UmlExerciseComponent, UmlClassSelectionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlExerciseComponent);
    component = fixture.componentInstance;
    component.part = UmlClassSelectionPart;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
