import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UmlClassSelectionComponent } from './uml-class-selection.component';

describe('UmlClassSelectionComponent', () => {
  let component: UmlClassSelectionComponent;
  let fixture: ComponentFixture<UmlClassSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlClassSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlClassSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});