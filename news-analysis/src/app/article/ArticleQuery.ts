import {Article} from './Article'
import {ArticleRating} from './ArticleRating'
import {ArticleStatus} from './ArticleStatus'
import {ArticleWithStats} from './ArticleWithStats'

export class ArticleQueryResponse{
    constructor(public article:Article){}

}
export class ArticleQueryRequest{
    constructor(public userId:number,public url:string){}
}

export class ArticlesQueryRequest{
    constructor(public skipHtml:boolean){}
}

export class ArticlesQueryResponse{
    constructor(public articles:Article[]){}
}

export class ArticleUpdateStatusResponse{
    constructor(public success:boolean, message:string){}
}

export class ArticleRatingRequest{
    constructor(public articleId:number,public rating:ArticleRating){}

    toJson():any{
       return {"articleId": this.articleId, "rating": ArticleRating[this.rating] } 
    }
}

export class ArticleRatingResponse{
    constructor(public success:boolean, message:string){}
}

export class ArticleUpdateStatusRequest{
    constructor(public articleId:number,public status: ArticleStatus){}

    toJson():any{
       return {"articleId": this.articleId, "status": ArticleStatus[this.status] } 
    }
}

export class ArticlesWithStatsResponse{
    constructor(public articlesWithStats:ArticleWithStats[]){}
}