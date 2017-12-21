import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { DashboardSidebarComponent } from './dashboard/dashboard-sidebar.component';
import { DashboardHomeComponent } from './dashboard/dashboard-views/dashboard-home/dashboard-home.component';
import {routing} from './app.routing';
import { BrowseComponent } from './dashboard/dashboard-views/browse/browse.component';
import {SafePipe} from './safe.pipe';
import {AuthService} from './auth/auth.service';
import {AuthGuard} from './auth/auth.guard'
import {NoAuthGuard} from './auth/noauth.guard'
import {HttpService} from './common/http.service';
import { LoginComponent } from './auth/login.component'
import {ArticleService} from './article/article.service';
import { RateArticleComponent } from './dashboard/dashboard-views/rate-article/rate-article.component';
import { StatusComponent } from './common/status.component'

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    DashboardComponent,
    DashboardSidebarComponent,
    DashboardHomeComponent,
    BrowseComponent,
    SafePipe,
    LoginComponent,
    RateArticleComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    routing
  ],
  providers: [AuthService, AuthGuard, NoAuthGuard, HttpService,ArticleService],
  bootstrap: [AppComponent]
})
export class AppModule { }
