import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { ArticleService } from '../../../article/article.service'
import { ArticleQueryResponse, ArticleRatingRequest } from '../../../article/ArticleQuery'
import { Article } from '../../../article/Article'
import { Subscription } from 'rxjs/Rx'
import { ArticleRating } from '../../../article/ArticleRating'

@Component({
  selector: 'fn-rate-article',
  templateUrl: './rate-article.component.html',
  styleUrls: ['./rate-article.component.css']
})
export class RateArticleComponent implements OnInit, OnDestroy {

  article: Article = null;
  private articleRatingSubscription: Subscription
  private articleSubscription: Subscription

  constructor(private route: ActivatedRoute, private router: Router, private articleService: ArticleService) { }


  articleReal() {
    let articleRatingReq = new ArticleRatingRequest(this.article.id, ArticleRating.REAL)
    this.articleRatingSubscription = this.articleService.rateArticle(articleRatingReq).subscribe((arr) => { console.log(arr) });
  }

  articleFake() {
    let articleRatingReq = new ArticleRatingRequest(this.article.id, ArticleRating.FAKE)
    this.articleRatingSubscription = this.articleService.rateArticle(articleRatingReq).subscribe((arr) => { console.log(arr) });
  }

  ngOnInit() {
    this.articleSubscription = this.route.params.switchMap((params: Params) => this.articleService.articleById(+params['id']))
      .subscribe((aqr: ArticleQueryResponse) => this.article = aqr.article);
  }

  ngOnDestroy() {
    this.articleSubscription.unsubscribe();
    this.articleRatingSubscription.unsubscribe();
  }

}
