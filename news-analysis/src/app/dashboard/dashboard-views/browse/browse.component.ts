import { Component, OnInit, OnDestroy } from '@angular/core';
import {FormGroup, FormBuilder, FormControl, Validators} from '@angular/forms';
import {AuthService} from '../../../auth/auth.service';
import {ArticleService} from '../../../article/article.service'
import {ArticleQueryRequest, ArticleUpdateStatusRequest} from '../../../article/ArticleQuery'
import {Article} from '../../../article/Article'
import { ArticleStatus } from '../../../article/ArticleStatus'
import {Observable,Subscription} from 'rxjs/Rx'
import {Router} from '@angular/router'
import {StatusComponent} from '../../../common/status.component'

@Component({
  selector: 'fn-browse',
  templateUrl: './browse.component.html',
  styleUrls: ['./browse.component.css']
})
export class BrowseComponent extends StatusComponent implements OnInit, OnDestroy {
  
  submittedArticle:Article = null
  browserForm:FormGroup;
  sub:Subscription = null;

  constructor(private fb:FormBuilder, private authService: AuthService, private articleService:ArticleService, private router: Router) { 
     super(false);
  }

  ngOnInit() {
    this.initForm();
  }

  ngOnDestroy(){
    if(this.sub != null)
      this.sub.unsubscribe();
  }

  initForm(){
    this.browserForm = this.fb.group({
      enteredUrl: ['', [Validators.pattern(/^https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)/), Validators.required]]
    })
  }

  submitUrl(){
   let url = this.browserForm.controls['enteredUrl'].value;
    const request = new ArticleQueryRequest(null, url);
    this.startLoading();
    this.sub = this.articleService.submitUrl(request).map( (aqr)=> aqr.article).subscribe( (art)=> {this.submittedArticle = art; this.stopLoading()} );
  }

  approveArticle(){
    const req = new ArticleUpdateStatusRequest(this.submittedArticle.id, ArticleStatus.ACTIVE)
    this.articleService.updateArticleStatus(req).subscribe( (asur) => {
      if(asur.success){
        this.router.navigate(['/dashboard/rate',this.submittedArticle.id])
      }else{

      }
    });
  }

  rejectArticle(){
    const req = new ArticleUpdateStatusRequest(this.submittedArticle.id, ArticleStatus.DELETED)
    this.articleService.updateArticleStatus(req).subscribe( (asur)=> {
      if(asur.success){
        // Redirect
      }else{

      }
    });
  }

}
