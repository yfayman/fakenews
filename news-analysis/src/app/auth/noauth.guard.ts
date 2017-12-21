import {Injectable} from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Rx';
import {AuthService} from './auth.service';

@Injectable()
export class NoAuthGuard implements CanActivate{
    constructor(private authService:AuthService, private router:Router){}

    canActivate(route:ActivatedRouteSnapshot, state:RouterStateSnapshot):Observable<boolean> | boolean{
        if(this.authService.isAuthenticated()){
            this.router.navigate(['/dashboard/home'])
            return false;
        }else{
            return true;
        }
    }
}