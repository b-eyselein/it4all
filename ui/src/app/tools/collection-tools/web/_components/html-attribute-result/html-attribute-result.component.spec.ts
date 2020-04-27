import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HtmlAttributeResultComponent } from './html-attribute-result.component';

describe('HtmlAttributeResultComponent', () => {
  let component: HtmlAttributeResultComponent;
  let fixture: ComponentFixture<HtmlAttributeResultComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HtmlAttributeResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HtmlAttributeResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
