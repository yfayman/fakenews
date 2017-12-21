import { Injectable } from '@angular/core';
import { Observable, Subject, BehaviorSubject } from 'rxjs/Rx';
import { Cookie } from 'ng2-cookies/ng2-cookies'
import { Credentials } from './Credentials'
import { LoginResponse } from './LoginResponse'
import { AccountInfo } from './AccountInfo'
import { Http, Response, Headers } from '@angular/http'
import { AccountInfoResponse, CreateAccountRequest, CreateAccountResponse } from './AccountQuery'

export const AUTH_COOKIE_NAME: string = 'auth';

@Injectable()
export class AuthService {


  private authToken: string = null;
  // This is initialized based on whether a cookie exists
  private authTokenValidSubject: BehaviorSubject<boolean> = null
  private accountInfoSubject = new BehaviorSubject<AccountInfo>(null)



  private headers = new Headers();

  constructor(private http: Http) {
    this.headers.append('Accept', 'application/json');
    this.headers.append('Content-Type', 'application/json')
    let cookieVal = Cookie.get(AUTH_COOKIE_NAME)
    this.authToken = cookieVal
    this.authTokenValidSubject = new BehaviorSubject<boolean>(this.authToken != null)
    if (this.authToken != null)
      this.getAccountInfo();
  }

  login(creds: Credentials) {
    const credentialJson = JSON.stringify(creds)
    return this.http.post('/app/auth/login/', credentialJson, { headers: this.headers }).do( (response) => {
      let json = response.json();
        if (response.ok) {
          const loginResponse = new LoginResponse();
          loginResponse.accInfo.deserialize(json['accountInfo']);
          this.authToken = loginResponse.accInfo.token;
          Cookie.set(AUTH_COOKIE_NAME, this.authToken, 1)
          this.authTokenValidSubject.next(true)
          this.accountInfoSubject.next(loginResponse.accInfo)
        } else {
          // add a login failed event
        }

    })
     
  }

  register(req: CreateAccountRequest) {
    const regJson = JSON.stringify(req);
    return this.http.post('/app/auth/create/', regJson, { headers: this.headers }).map((response: Response) => {
      let json = response.json();
      return new CreateAccountResponse(response.ok, json['status']);
    })

  }

  /**
   * Should change this later to not expose authToken in URL
   */
  private getAccountInfo() {
    this.http.get('/app/auth/getAcc/' + this.authToken, { headers: this.headers }).
      subscribe((response: Response) => {
        const json = response.json();
        const accInfoResponse = new AccountInfoResponse();
        accInfoResponse.deserialize(json);
        this.accountInfoSubject.next(accInfoResponse.account)
        this.authTokenValidSubject.next(accInfoResponse.activeAuth)
      })
  }

  getAccount() {
    return this.accountInfoSubject.value
  }

  getAccountInfoObs() {
    return this.accountInfoSubject.asObservable();
  }

  isAuthenticated() {
    return this.authTokenValidSubject.value
  }

  getAuthenticatedObs(){
    return this.authTokenValidSubject.asObservable();
  }





  logout() {
    Cookie.delete(AUTH_COOKIE_NAME)
    this.authTokenValidSubject.next(false)
    this.accountInfoSubject.next(null)
  }

}
