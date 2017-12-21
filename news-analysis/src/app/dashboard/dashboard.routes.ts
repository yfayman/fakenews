import {Routes} from '@angular/router';
import {BrowseComponent} from './dashboard-views/browse/browse.component';
import {DashboardHomeComponent} from './dashboard-views/dashboard-home/dashboard-home.component';
import {RateArticleComponent} from './dashboard-views/rate-article/rate-article.component'
import {AuthGuard} from '../auth/auth.guard'

export const DASHBOARD_ROUTES:Routes = [
    {path: '', redirectTo: '/dashboard/home', pathMatch: 'full'},
    {path: 'home', component: DashboardHomeComponent, canActivate: [AuthGuard]  },
    {path: 'browse', component: BrowseComponent },
    {path: 'rate/:id', component: RateArticleComponent, canActivate:[AuthGuard]}

];