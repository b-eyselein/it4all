import { ComponentFixture, TestBed } from '@angular/core/testing';

import { XmlElementLineMatchResultComponent } from './xml-element-line-match-result.component';

describe('XmlElementLineMatchResultComponent', () => {
  let component: XmlElementLineMatchResultComponent;
  let fixture: ComponentFixture<XmlElementLineMatchResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ XmlElementLineMatchResultComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(XmlElementLineMatchResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
