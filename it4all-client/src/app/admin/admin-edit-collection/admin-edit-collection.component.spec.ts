import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminEditCollectionComponent} from './admin-edit-collection.component';
import {RouterTestingModule} from '@angular/router/testing';
import {ApiService} from '../../tools/collection-tools/_services/api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';

describe('AdminEditCollectionComponent', () => {
  let component: AdminEditCollectionComponent;
  let fixture: ComponentFixture<AdminEditCollectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [AdminEditCollectionComponent],
      providers: [
        ApiService,
        {
          provide: ActivatedRoute,
          useValue: {snapshot: {paramMap: new Map<string, string>([['toolId', 'web']])}}
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminEditCollectionComponent);
    component = fixture.componentInstance;
    component.collection = {
      id: 1, toolId: '', title: '', authors: [], text: '', shortName: '', exercises: []
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
