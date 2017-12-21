import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { AuthService } from './auth.service'
import { Credentials } from './Credentials'
import { CreateAccountRequest } from './AccountQuery'
import { Observable, Subscription } from 'rxjs/Rx'
import { StatusComponent } from '../common/status.component'
import { Router, ActivatedRoute, Params } from '@angular/router'

@Component({
  selector: 'fn-login',
  templateUrl: './login.component.html',
  styles: [`
    #signin-form, #register-form{ margin-top:10%;max-width: 400px; margin: 0 auto; text-align:center;}
    #signin-form button, #register-form button {margin-top:20px;}
  `]
})
export class LoginComponent extends StatusComponent implements OnInit, OnDestroy {

  showSignIn = true;
  showRegister = false;

  loginForm: FormGroup
  registerForm: FormGroup

  registerSub: Subscription = null;
  loginSub: Subscription = null

  isAuthenticated = false;
  private authenticatedSub: Subscription = null;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, private activatedRoute:ActivatedRoute) { super(false) }
  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
        let bounced = <string>params['status'] == 'bounced';
        if(bounced){
          alert("Your session has expired. Please login again")
        }
      });

    this.isAuthenticated = this.authService.isAuthenticated();
    this.authenticatedSub = this.authService.getAuthenticatedObs().subscribe((bool) => {
      this.isAuthenticated = bool
      if (this.isAuthenticated) {
        this.router.navigate(['/dashboard/home'])
      }
    })
    this.loginForm = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', Validators.required]
    });
    this.registerForm = this.fb.group({
      email: ['', [Validators.required]],
      username: ['', [Validators.required]],
      passwords: this.fb.group({
        password: ['', Validators.required],
        passwordConfirm: ['', Validators.required]
      }, { validator: this.areEqual })
    });
  }

  login() {
    let email = this.loginForm.get('email').value;
    let password = this.loginForm.get('password').value;
    const creds = new Credentials(email, password)
    this.startLoading();
    this.loginSub = this.authService.login(creds).subscribe(
      (res) => { this.stopLoading() }, 
      (err) => {this.stopLoading(); alert("Login failed. Please try again")
    })
  }

  areEqual(group: FormGroup) {
    let valid = true;
    let previousVal = null

    for (let name in group.controls) {
      let val = group.controls[name].value

      if (previousVal != null && previousVal != val) {
        valid = false;
        break;
      }
      previousVal = val;
    }

    if (valid) {
      return null;
    }

    return {
      areEqual: true
    }
  }

  registerAccount() {
    const regEmail = this.registerForm.controls['email'].value
    const regPassword = this.registerForm.get('passwords.password').value
    const regUsername = this.registerForm.controls['username'].value
    const req = new CreateAccountRequest(regEmail, regUsername, regPassword);
    this.registerSub = this.authService.register(req).subscribe((res) => { })
  }

  showRegisterForm() {
    this.showRegister = true;
    this.showSignIn = false
  }

  showSignInForm() {
    this.showRegister = false;
    this.showSignIn = true
  }

  ngOnDestroy() {
    if (this.registerSub != null)
      this.registerSub.unsubscribe();
    if (this.loginSub != null)
      this.loginSub.unsubscribe();
    if (this.authenticatedSub != null)
      this.authenticatedSub.unsubscribe()
  }



}
