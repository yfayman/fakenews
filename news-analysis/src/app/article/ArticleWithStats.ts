import {Article} from './Article'
import {ArticleStats} from './ArticleStats'

export class ArticleWithStats{
    constructor(public article:Article, stats:ArticleStats){}
}