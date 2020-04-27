import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HtmlTaskResultComponent } from './html-task-result.component';

describe('HtmlTaskResultComponent', () => {
  let component: HtmlTaskResultComponent;
  let fixture: ComponentFixture<HtmlTaskResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HtmlTaskResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HtmlTaskResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
