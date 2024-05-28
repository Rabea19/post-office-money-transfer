import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../post-office.test-samples';

import { PostOfficeFormService } from './post-office-form.service';

describe('PostOffice Form Service', () => {
  let service: PostOfficeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PostOfficeFormService);
  });

  describe('Service methods', () => {
    describe('createPostOfficeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPostOfficeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            streetAddress: expect.any(Object),
            city: expect.any(Object),
            state: expect.any(Object),
            postalCode: expect.any(Object),
          }),
        );
      });

      it('passing IPostOffice should create a new form with FormGroup', () => {
        const formGroup = service.createPostOfficeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            streetAddress: expect.any(Object),
            city: expect.any(Object),
            state: expect.any(Object),
            postalCode: expect.any(Object),
          }),
        );
      });
    });

    describe('getPostOffice', () => {
      it('should return NewPostOffice for default PostOffice initial value', () => {
        const formGroup = service.createPostOfficeFormGroup(sampleWithNewData);

        const postOffice = service.getPostOffice(formGroup) as any;

        expect(postOffice).toMatchObject(sampleWithNewData);
      });

      it('should return NewPostOffice for empty PostOffice initial value', () => {
        const formGroup = service.createPostOfficeFormGroup();

        const postOffice = service.getPostOffice(formGroup) as any;

        expect(postOffice).toMatchObject({});
      });

      it('should return IPostOffice', () => {
        const formGroup = service.createPostOfficeFormGroup(sampleWithRequiredData);

        const postOffice = service.getPostOffice(formGroup) as any;

        expect(postOffice).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPostOffice should not enable id FormControl', () => {
        const formGroup = service.createPostOfficeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPostOffice should disable id FormControl', () => {
        const formGroup = service.createPostOfficeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
