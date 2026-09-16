import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdministrativeManagers } from './administrative-managers';

describe('AdministrativeManagers', () => {
  let component: AdministrativeManagers;
  let fixture: ComponentFixture<AdministrativeManagers>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdministrativeManagers],
    }).compileComponents();

    fixture = TestBed.createComponent(AdministrativeManagers);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
