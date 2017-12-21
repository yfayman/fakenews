import {Serializable} from '../common/Serializable'


export class ArticleStats implements Serializable<ArticleStats>{

    deserialize(input:any){
        return this;
    }

}