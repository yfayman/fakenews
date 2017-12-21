import {ArticleStatus} from './ArticleStatus'
import {ArticleStats} from './ArticleStats'
import {Serializable} from '../common/Serializable'

export class Article implements Serializable<Article> {
    constructor(){}

    public id:number
    public url:string
    public title:string
    public html:string
    public status:ArticleStatus
    public shortDescription:string
  

    deserialize(input: any){
        this.id = input['articleId']
        this.url = input['url']
        this.title = input['title']
        this.html = input['html']
        this.shortDescription = input['shortDescription']
        this.status = this.getArticleStatus(input['status'])
        return this;
    }

    private getArticleStatus(status:string): ArticleStatus  {
        switch(status){
                case 'PENDING': return ArticleStatus.PENDING
                case 'ACTIVE': return ArticleStatus.ACTIVE
                case 'DELETED': return ArticleStatus.DELETED
        }
    }
}