import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../citizen.test-samples';

import { CitizenFormService } from './citizen-form.service';

describe('Citizen Form Service', () => {
  let service: CitizenFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CitizenFormService);
  });

  describe('Service methods', () => {
    describe('createCitizenFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCitizenFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nationalId: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            phoneNumber: expect.any(Object),
          }),
        );
      });

      it('passing ICitizen should create a new form with FormGroup', () => {
        const formGroup = service.createCitizenFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nationalId: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            phoneNumber: expect.any(Object),
          }),
        );
      });
    });

    describe('getCitizen', () => {
      it('should return NewCitizen for default Citizen initial value', () => {
        const formGroup = service.createCitizenFormGroup(sampleWithNewData);

        const citizen = service.getCitizen(formGroup) as any;

        expect(citizen).toMatchObject(sampleWithNewData);
      });

      it('should return NewCitizen for empty Citizen initial value', () => {
        const formGroup = service.createCitizenFormGroup();

        const citizen = service.getCitizen(formGroup) as any;

        expect(citizen).toMatchObject({});
      });

      it('should return ICitizen', () => {
        const formGroup = service.createCitizenFormGroup(sampleWithRequiredData);

        const citizen = service.getCitizen(formGroup) as any;

        expect(citizen).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICitizen should not enable id FormControl', () => {
        const formGroup = service.createCitizenFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCitizen should disable id FormControl', () => {
        const formGroup = service.createCitizenFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
