import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICitizen } from '../citizen.model';
import { CitizenService } from '../service/citizen.service';

@Component({
  standalone: true,
  templateUrl: './citizen-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CitizenDeleteDialogComponent {
  citizen?: ICitizen;

  protected citizenService = inject(CitizenService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.citizenService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
