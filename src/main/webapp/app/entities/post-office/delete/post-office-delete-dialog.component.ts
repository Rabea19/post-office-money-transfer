import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPostOffice } from '../post-office.model';
import { PostOfficeService } from '../service/post-office.service';

@Component({
  standalone: true,
  templateUrl: './post-office-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PostOfficeDeleteDialogComponent {
  postOffice?: IPostOffice;

  protected postOfficeService = inject(PostOfficeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.postOfficeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
