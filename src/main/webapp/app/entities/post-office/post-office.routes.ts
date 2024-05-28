import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PostOfficeComponent } from './list/post-office.component';
import { PostOfficeDetailComponent } from './detail/post-office-detail.component';
import { PostOfficeUpdateComponent } from './update/post-office-update.component';
import PostOfficeResolve from './route/post-office-routing-resolve.service';

const postOfficeRoute: Routes = [
  {
    path: '',
    component: PostOfficeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PostOfficeDetailComponent,
    resolve: {
      postOffice: PostOfficeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PostOfficeUpdateComponent,
    resolve: {
      postOffice: PostOfficeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PostOfficeUpdateComponent,
    resolve: {
      postOffice: PostOfficeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default postOfficeRoute;
