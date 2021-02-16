import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UmlAssocMatchResultComponent } from './uml-assoc-match-result.component';

describe('UmlAssocMatchResultComponent', () => {
  let component: UmlAssocMatchResultComponent;
  let fixture: ComponentFixture<UmlAssocMatchResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UmlAssocMatchResultComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlAssocMatchResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
