import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UmlImplResultComponent } from './uml-impl-result.component';

describe('UmlImplResultComponent', () => {
  let component: UmlImplResultComponent;
  let fixture: ComponentFixture<UmlImplResultComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UmlImplResultComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlImplResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
