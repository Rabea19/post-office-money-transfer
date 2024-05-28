import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICitizen } from '../citizen.model';
import { CitizenService } from '../service/citizen.service';
import { CitizenFormService, CitizenFormGroup } from './citizen-form.service';

@Component({
  standalone: true,
  selector: 'jhi-citizen-update',
  templateUrl: './citizen-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CitizenUpdateComponent implements OnInit {
  isSaving = false;
  citizen: ICitizen | null = null;

  protected citizenService = inject(CitizenService);
  protected citizenFormService = inject(CitizenFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CitizenFormGroup = this.citizenFormService.createCitizenFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ citizen }) => {
      this.citizen = citizen;
      if (citizen) {
        this.updateForm(citizen);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const citizen = this.citizenFormService.getCitizen(this.editForm);
    if (citizen.id !== null) {
      this.subscribeToSaveResponse(this.citizenService.update(citizen));
    } else {
      this.subscribeToSaveResponse(this.citizenService.create(citizen));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICitizen>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(citizen: ICitizen): void {
    this.citizen = citizen;
    this.citizenFormService.resetForm(this.editForm, citizen);
  }
}
