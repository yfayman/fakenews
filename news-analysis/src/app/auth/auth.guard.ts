import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Rx';
import {AuthService} from './auth.service';

@Injectable()
export class AuthGuard implements CanActivate{
    
    constructor(private authService:AuthService, private router:Router){}

    canActivate(route:ActivatedRouteSnapshot, state:RouterStateSnapshot):Observable<boolean> | boolean{
        if(this.authService.isAuthenticated()){
            return true;
        }else{
            this.router.navigate(['/login','bounced'])
            return false;
        }
    }
}