import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TransactionComponent } from './list/transaction.component';
import { TransactionDetailComponent } from './detail/transaction-detail.component';
import { TransactionUpdateComponent } from './update/transaction-update.component';
import TransactionResolve from './route/transaction-routing-resolve.service';

const transactionRoute: Routes = [
  {
    path: '',
    component: TransactionComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TransactionDetailComponent,
    resolve: {
      transaction: TransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TransactionUpdateComponent,
    resolve: {
      transaction: TransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TransactionUpdateComponent,
    resolve: {
      transaction: TransactionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default transactionRoute;
