import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RegexExerciseComponent} from './regex-exercise.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppRoutingModule, routingComponents} from '../../../../app-routing.module';

describe('RegexExerciseComponent', () => {
  let component: RegexExerciseComponent;
  let fixture: ComponentFixture<RegexExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [RegexExerciseComponent, ...routingComponents]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegexExerciseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
