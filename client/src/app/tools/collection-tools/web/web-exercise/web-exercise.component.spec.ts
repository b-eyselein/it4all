import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {WebExerciseComponent} from './web-exercise.component';
import {AppRoutingModule} from '../../../../app-routing.module';
import {TabsComponent} from '../../../../_component_helpers/tabs/tabs.component';
import {TabComponent} from '../../../../_component_helpers/tab/tab.component';

describe('WebExerciseComponent', () => {
  let component: WebExerciseComponent;
  let fixture: ComponentFixture<WebExerciseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [WebExerciseComponent, TabsComponent, TabComponent],
      imports: [AppRoutingModule]
    })
      .compileComponents();
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
