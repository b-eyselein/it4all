import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { XmlExerciseComponent } from './xml-exercise.component';

describe('XmlExerciseComponent', () => {
  let component: XmlExerciseComponent;
  let fixture: ComponentFixture<XmlExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ XmlExerciseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(XmlExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
