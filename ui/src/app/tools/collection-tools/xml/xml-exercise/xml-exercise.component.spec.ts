import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { XmlExerciseComponent } from './xml-exercise.component';

describe('XmlExerciseComponent', () => {
  let component: XmlExerciseComponent;
  let fixture: ComponentFixture<XmlExerciseComponent>;

  beforeEach(waitForAsync(() => {
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
