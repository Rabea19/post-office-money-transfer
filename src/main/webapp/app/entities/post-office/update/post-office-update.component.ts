import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPostOffice } from '../post-office.model';
import { PostOfficeService } from '../service/post-office.service';
import { PostOfficeFormService, PostOfficeFormGroup } from './post-office-form.service';

@Component({
  standalone: true,
  selector: 'jhi-post-office-update',
  templateUrl: './post-office-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PostOfficeUpdateComponent implements OnInit {
  isSaving = false;
  postOffice: IPostOffice | null = null;

  protected postOfficeService = inject(PostOfficeService);
  protected postOfficeFormService = inject(PostOfficeFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PostOfficeFormGroup = this.postOfficeFormService.createPostOfficeFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ postOffice }) => {
      this.postOffice = postOffice;
      if (postOffice) {
        this.updateForm(postOffice);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const postOffice = this.postOfficeFormService.getPostOffice(this.editForm);
    if (postOffice.id !== null) {
      this.subscribeToSaveResponse(this.postOfficeService.update(postOffice));
    } else {
      this.subscribeToSaveResponse(this.postOfficeService.create(postOffice));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPostOffice>>): void {
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

  protected updateForm(postOffice: IPostOffice): void {
    this.postOffice = postOffice;
    this.postOfficeFormService.resetForm(this.editForm, postOffice);
  }
}
