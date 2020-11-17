import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProficiencyCardComponent } from './proficiency-card.component';

describe('ProficiencyCardComponent', () => {
  let component: ProficiencyCardComponent;
  let fixture: ComponentFixture<ProficiencyCardComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ProficiencyCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProficiencyCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
