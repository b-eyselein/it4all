import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UmlClassSelectionComponent} from './uml-class-selection.component';

describe('UmlClassSelectionComponent', () => {
  let component: UmlClassSelectionComponent;
  let fixture: ComponentFixture<UmlClassSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UmlClassSelectionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlClassSelectionComponent);
    component = fixture.componentInstance;
    component.exercise = {
      id: 0, collectionId: 0, toolId: 'uml', text: '', authors: [], title: '',
      semanticVersion: {major: 0, minor: 1, patch: 0},
      tags: [],
      content: {}
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
