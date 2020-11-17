import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ExerciseLinkCardComponent } from './exercise-link-card.component';

describe('ExerciseLinkCardComponent', () => {
  let component: ExerciseLinkCardComponent;
  let fixture: ComponentFixture<ExerciseLinkCardComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ExerciseLinkCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExerciseLinkCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
