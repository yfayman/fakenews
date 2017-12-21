import {Serializable} from '../common/Serializable'

export class AccountInfo implements Serializable<AccountInfo> {

    public id:number
    public username:string;
    public email:string;
    public token:string;

    constructor(){}

    deserialize(input: any){
        this.id = input['userId']
        this.username = input['username']
        this.email = input['email']
        this.token = input['token']

        return this;
    }
}