import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UmlExerciseComponent} from './uml-exercise.component';
import {UmlClassSelectionComponent} from '../uml-class-selection/uml-class-selection.component';
import {RouterTestingModule} from '@angular/router/testing';
import {UmlClassSelectionPart} from '../uml-tools';
import {IUmlExerciseContent} from '../uml-interfaces';

describe('UmlExerciseComponent', () => {

  let component: UmlExerciseComponent;
  let fixture: ComponentFixture<UmlExerciseComponent>;

  const exerciseContent: IUmlExerciseContent = {
    mappings: [],
    sampleSolutions: [],
    toIgnore: []
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [UmlExerciseComponent, UmlClassSelectionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlExerciseComponent);
    component = fixture.componentInstance;
    component.exercise = {
      id: 0, collectionId: 0, toolId: 'uml', semanticVersion: {major: 0, minor: 1, patch: 0}, title: '', authors: [], text: '',
      tags: [], content: exerciseContent
    };
    component.part = UmlClassSelectionPart;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
