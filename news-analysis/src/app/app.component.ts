import { Component, Injectable} from '@angular/core';
import {HttpService} from './common/http.service'

@Component({
  selector: 'fn-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
 @Injectable()
export class AppComponent {
 

  constructor(private httpService:HttpService){}

}
