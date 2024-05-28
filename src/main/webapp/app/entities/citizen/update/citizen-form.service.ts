import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICitizen, NewCitizen } from '../citizen.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICitizen for edit and NewCitizenFormGroupInput for create.
 */
type CitizenFormGroupInput = ICitizen | PartialWithRequiredKeyOf<NewCitizen>;

type CitizenFormDefaults = Pick<NewCitizen, 'id'>;

type CitizenFormGroupContent = {
  id: FormControl<ICitizen['id'] | NewCitizen['id']>;
  nationalId: FormControl<ICitizen['nationalId']>;
  firstName: FormControl<ICitizen['firstName']>;
  lastName: FormControl<ICitizen['lastName']>;
  phoneNumber: FormControl<ICitizen['phoneNumber']>;
};

export type CitizenFormGroup = FormGroup<CitizenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CitizenFormService {
  createCitizenFormGroup(citizen: CitizenFormGroupInput = { id: null }): CitizenFormGroup {
    const citizenRawValue = {
      ...this.getFormDefaults(),
      ...citizen,
    };
    return new FormGroup<CitizenFormGroupContent>({
      id: new FormControl(
        { value: citizenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nationalId: new FormControl(citizenRawValue.nationalId, {
        validators: [Validators.required, Validators.minLength(14), Validators.maxLength(14), Validators.pattern('^[0-9]{14}$')],
      }),
      firstName: new FormControl(citizenRawValue.firstName, {
        validators: [Validators.required],
      }),
      lastName: new FormControl(citizenRawValue.lastName, {
        validators: [Validators.required],
      }),
      phoneNumber: new FormControl(citizenRawValue.phoneNumber, {
        validators: [Validators.required, Validators.minLength(11), Validators.maxLength(11), Validators.pattern('^[0-9]{11}$')],
      }),
    });
  }

  getCitizen(form: CitizenFormGroup): ICitizen | NewCitizen {
    return form.getRawValue() as ICitizen | NewCitizen;
  }

  resetForm(form: CitizenFormGroup, citizen: CitizenFormGroupInput): void {
    const citizenRawValue = { ...this.getFormDefaults(), ...citizen };
    form.reset(
      {
        ...citizenRawValue,
        id: { value: citizenRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CitizenFormDefaults {
    return {
      id: null,
    };
  }
}
