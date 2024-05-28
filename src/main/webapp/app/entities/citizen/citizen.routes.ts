import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CitizenComponent } from './list/citizen.component';
import { CitizenDetailComponent } from './detail/citizen-detail.component';
import { CitizenUpdateComponent } from './update/citizen-update.component';
import CitizenResolve from './route/citizen-routing-resolve.service';

const citizenRoute: Routes = [
  {
    path: '',
    component: CitizenComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CitizenDetailComponent,
    resolve: {
      citizen: CitizenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CitizenUpdateComponent,
    resolve: {
      citizen: CitizenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CitizenUpdateComponent,
    resolve: {
      citizen: CitizenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default citizenRoute;
