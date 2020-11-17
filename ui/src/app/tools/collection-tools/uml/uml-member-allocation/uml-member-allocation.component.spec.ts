import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { UmlMemberAllocationComponent } from './uml-member-allocation.component';

describe('UmlMemberAllocationComponent', () => {
  let component: UmlMemberAllocationComponent;
  let fixture: ComponentFixture<UmlMemberAllocationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ UmlMemberAllocationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UmlMemberAllocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
