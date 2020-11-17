import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {UmlClassSelectionComponent} from './uml-class-selection.component';

describe('UmlClassSelectionComponent', () => {
  let component: UmlClassSelectionComponent;
  let fixture: ComponentFixture<UmlClassSelectionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [UmlClassSelectionComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlClassSelectionComponent);
    component = fixture.componentInstance;
    component.exerciseFragment = {exerciseId: 0, collectionId: 0, toolId: 'uml', text: '', title: ''};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
