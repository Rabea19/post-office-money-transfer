import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IPostOffice } from '../post-office.model';

@Component({
  standalone: true,
  selector: 'jhi-post-office-detail',
  templateUrl: './post-office-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PostOfficeDetailComponent {
  postOffice = input<IPostOffice | null>(null);

  previousState(): void {
    window.history.back();
  }
}
