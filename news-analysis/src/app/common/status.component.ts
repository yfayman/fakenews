import { OnInit } from '@angular/core';

export class StatusComponent implements OnInit {

  public isLoading: boolean;
  public isProcessing:boolean

  //Does the component start off in loading mode?
  constructor(isLoadingOnInit: boolean) {
    this.isLoading = isLoadingOnInit;
    this.isProcessing = false;
  }

  ngOnInit() {
  }
  /**
   * Invoke this when you want the loading graphic be active
   */
  startLoading(){
    this.isLoading = true;
  }
  /**
   * Invoke this to hide loading graphic
   */
  stopLoading(){
    this.isLoading = false;
  }

  startProcessing(){
    this.isProcessing = true;
  }

  stopProcessing(){
    this.isProcessing = false;
  }

}
