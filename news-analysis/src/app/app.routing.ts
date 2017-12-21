import {Routes,RouterModule} from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';
import {DASHBOARD_ROUTES} from './dashboard/dashboard.routes';
import {NoAuthGuard} from './auth/noauth.guard'
import {LoginComponent} from './auth/login.component'

const APP_ROUTES:Routes = [
    {path:'', redirectTo:'/login', pathMatch: 'full'},
    {path:'login', component:LoginComponent,canActivate:[NoAuthGuard]},
    {path:'login/:status', component:LoginComponent,canActivate:[NoAuthGuard]},
    {path:'dashboard', component:DashboardComponent, children: DASHBOARD_ROUTES}
];

export const routing = RouterModule.forRoot(APP_ROUTES);