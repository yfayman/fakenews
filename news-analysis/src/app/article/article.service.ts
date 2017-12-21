import { Injectable, OnInit, EventEmitter } from '@angular/core';
import { HttpService } from '../common/http.service'
import {
    ArticleQueryResponse, ArticleQueryRequest, ArticleUpdateStatusResponse,
    ArticlesQueryRequest, ArticlesQueryResponse, ArticleRatingRequest, ArticleRatingResponse,
    ArticleUpdateStatusRequest
} from './ArticleQuery'
import { Observable } from 'rxjs/Rx';
import { Response } from '@angular/http';
import { Article } from './Article'
import { ArticleStatus } from './ArticleStatus'
import { ArticleRating } from './ArticleRating'

@Injectable()
export class ArticleService {

    private articleCache = new Map<number, Article>();

    constructor(private httpService: HttpService) {
    }



    // Returns HTML
    submitUrl(req: ArticleQueryRequest): Observable<ArticleQueryResponse> {
        return this.httpService.post("/app/article/", req).map((response: Response) => {
            return new ArticleQueryResponse(
                this.extractArticleDataFromResponse(response)
            );
        });
    }

    updateArticleStatus(req: ArticleUpdateStatusRequest) {
        const statusString = ArticleStatus[req.status];
        return this.httpService.patch("/app/articleUpdateStatus/" + req.articleId, req.toJson()).map((response: Response) =>
            new ArticleUpdateStatusResponse(response.ok, "")
        );
    }

    rateArticle(req: ArticleRatingRequest) {
        return this.httpService.post("/app/article/rating/", req.toJson()).map((response: Response) =>
            new ArticleRatingResponse(response.ok, '')
        );
    }

    articleById(id: number): Observable<ArticleQueryResponse>{
        const articleFromCache = this.articleCache.get(id)
        if (articleFromCache != null)
            return Observable.of(new ArticleQueryResponse(articleFromCache));
        return this.httpService.get("/app/article/" + id, []).map((response: Response) =>
            new ArticleQueryResponse(this.extractArticleDataFromResponse(response))
        ).do( (aqr) => this.articleCache.set(aqr.article.id,aqr.article) );
    }

    articleByUrl(url: string) {
        var encodedUrl = encodeURIComponent(url);
        return this.httpService.get("/app/articleByUrl/" + encodedUrl, []).map((response: Response) =>
            new ArticleQueryResponse(this.extractArticleDataFromResponse(response))
        );

    }

    articles(queryRequest: ArticlesQueryRequest) {
        const params = new Array<[string, string]>();
        params.push(['skipHtml', String(queryRequest.skipHtml)])
        const requestUrl = "/app/article/"
        return this.httpService.get(requestUrl, params).map((response: Response) => {
            const articlesJson = response.json()
            const articlesArray = new Array<Article>();
            for (var i = 0; i < articlesJson.length; i++) {
                const json = articlesJson[i];
                const article = new Article();
                articlesArray.push(article.deserialize(json));
            }
            return new ArticlesQueryResponse(articlesArray);
        }).do( (aqr) =>{
            for(var i = 0; i < aqr.articles.length; i++){
                let article = aqr.articles[i];
                this.articleCache.set(article.id, article);
            }
        });
    }


    private extractArticleDataFromResponse(response: Response): Article {
        let json = response.json();
        const article = new Article();
        return article.deserialize(json);
    }
   
}