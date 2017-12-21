import { AccountInfo } from './AccountInfo'
import { Serializable } from '../common/Serializable'

export class AccountInfoResponse implements Serializable<AccountInfoResponse>{
    public account: AccountInfo
    public activeAuth: boolean
    public message: string

    constructor() { }

    deserialize(input: any) {
        this.activeAuth = input['activeAuth']
        if (this.activeAuth) {
            const readAccount = new AccountInfo();
            readAccount.deserialize(input['account'])
            this.account = readAccount;
        }

        this.message = input['message']
        return this;
    }
}

export class CreateAccountRequest {
    constructor(public email: string, public username: string, public password: string) { }
}

export class CreateAccountResponse {
    constructor(public success: boolean, message: string) { }
}