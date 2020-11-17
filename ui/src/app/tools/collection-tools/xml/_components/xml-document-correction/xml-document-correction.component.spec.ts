import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { XmlDocumentCorrectionComponent } from './xml-document-correction.component';

describe('XmlDocumentCorrectionComponent', () => {
  let component: XmlDocumentCorrectionComponent;
  let fixture: ComponentFixture<XmlDocumentCorrectionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ XmlDocumentCorrectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(XmlDocumentCorrectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
