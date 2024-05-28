import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'postOfficeMoneyTransferApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'citizen',
    data: { pageTitle: 'postOfficeMoneyTransferApp.citizen.home.title' },
    loadChildren: () => import('./citizen/citizen.routes'),
  },
  {
    path: 'post-office',
    data: { pageTitle: 'postOfficeMoneyTransferApp.postOffice.home.title' },
    loadChildren: () => import('./post-office/post-office.routes'),
  },
  {
    path: 'transaction',
    data: { pageTitle: 'postOfficeMoneyTransferApp.transaction.home.title' },
    loadChildren: () => import('./transaction/transaction.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
