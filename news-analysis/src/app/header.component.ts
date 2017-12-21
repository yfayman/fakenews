import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'fn-header',
  templateUrl: './header.component.html',
  styles: [`
    #navbar ul a.active{color:#dcdcdc;}
  `]
})
export class HeaderComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
