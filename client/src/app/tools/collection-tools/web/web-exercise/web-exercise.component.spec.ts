import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {WebExerciseComponent} from './web-exercise.component';
import {AppRoutingModule, routingComponents} from '../../../../app-routing.module';
import {TabsComponent} from '../../../../shared/tabs/tabs.component';
import {TabComponent} from '../../../../shared/tab/tab.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('WebExerciseComponent', () => {
  let component: WebExerciseComponent;
  let fixture: ComponentFixture<WebExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, AppRoutingModule],
      declarations: [WebExerciseComponent, TabsComponent, TabComponent, ...routingComponents],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WebExerciseComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
