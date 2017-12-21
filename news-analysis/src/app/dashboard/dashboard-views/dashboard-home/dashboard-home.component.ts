import { Component, OnInit, OnDestroy } from '@angular/core';
import { Article } from '../../../article/Article'
import { ArticleService } from '../../../article/article.service'
import { ArticlesQueryRequest } from '../../../article/ArticleQuery'
import { Observable } from 'rxjs/Rx';
import { RouterModule } from '@angular/router';
import { ArticleStatus } from '../../../article/ArticleStatus'
import { StatusComponent } from '../../../common/status.component'
import { Subscription } from 'rxjs/Rx'

@Component({
  selector: 'fn-dashboard-home',
  templateUrl: './dashboard-home.component.html',
  styleUrls: ['./dashboard-home.component.css']
})
export class DashboardHomeComponent extends StatusComponent implements OnInit, OnDestroy {

  constructor(private articleService: ArticleService) { super(true) }

  ArticleStatus = ArticleStatus;

  observableArticles: Observable<Article[]>;


  ngOnInit() {
    this.startLoading()
    this.observableArticles = this.articleService.articles(new ArticlesQueryRequest(true))
      .map((req) => req.articles).do( (arts)=> {this.stopLoading()} )
  }

  ngOnDestroy() {
    
  }

}
