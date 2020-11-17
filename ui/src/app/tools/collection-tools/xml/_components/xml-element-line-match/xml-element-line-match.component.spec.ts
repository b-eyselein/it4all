import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { XmlElementLineMatchComponent } from './xml-element-line-match.component';

describe('XmlElementLineMatchComponent', () => {
  let component: XmlElementLineMatchComponent;
  let fixture: ComponentFixture<XmlElementLineMatchComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ XmlElementLineMatchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(XmlElementLineMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
