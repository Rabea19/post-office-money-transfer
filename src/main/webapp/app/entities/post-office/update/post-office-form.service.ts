import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPostOffice, NewPostOffice } from '../post-office.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPostOffice for edit and NewPostOfficeFormGroupInput for create.
 */
type PostOfficeFormGroupInput = IPostOffice | PartialWithRequiredKeyOf<NewPostOffice>;

type PostOfficeFormDefaults = Pick<NewPostOffice, 'id'>;

type PostOfficeFormGroupContent = {
  id: FormControl<IPostOffice['id'] | NewPostOffice['id']>;
  name: FormControl<IPostOffice['name']>;
  streetAddress: FormControl<IPostOffice['streetAddress']>;
  city: FormControl<IPostOffice['city']>;
  state: FormControl<IPostOffice['state']>;
  postalCode: FormControl<IPostOffice['postalCode']>;
};

export type PostOfficeFormGroup = FormGroup<PostOfficeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PostOfficeFormService {
  createPostOfficeFormGroup(postOffice: PostOfficeFormGroupInput = { id: null }): PostOfficeFormGroup {
    const postOfficeRawValue = {
      ...this.getFormDefaults(),
      ...postOffice,
    };
    return new FormGroup<PostOfficeFormGroupContent>({
      id: new FormControl(
        { value: postOfficeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(postOfficeRawValue.name, {
        validators: [Validators.required],
      }),
      streetAddress: new FormControl(postOfficeRawValue.streetAddress, {
        validators: [Validators.required],
      }),
      city: new FormControl(postOfficeRawValue.city, {
        validators: [Validators.required],
      }),
      state: new FormControl(postOfficeRawValue.state, {
        validators: [Validators.required],
      }),
      postalCode: new FormControl(postOfficeRawValue.postalCode, {
        validators: [Validators.required, Validators.pattern('^[0-9]{5}(-[0-9]{4})?$')],
      }),
    });
  }

  getPostOffice(form: PostOfficeFormGroup): IPostOffice | NewPostOffice {
    return form.getRawValue() as IPostOffice | NewPostOffice;
  }

  resetForm(form: PostOfficeFormGroup, postOffice: PostOfficeFormGroupInput): void {
    const postOfficeRawValue = { ...this.getFormDefaults(), ...postOffice };
    form.reset(
      {
        ...postOfficeRawValue,
        id: { value: postOfficeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PostOfficeFormDefaults {
    return {
      id: null,
    };
  }
}
