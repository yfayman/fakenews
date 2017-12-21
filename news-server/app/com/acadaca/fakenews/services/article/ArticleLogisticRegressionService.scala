package com.acadaca.fakenews.services.article



class ArticleLogisticRegressionService(rticleService:ArticleService) extends ArticleEvalService{
  
  def generateModel(articleIds:List[Int]):GenerateModelResponse = ???
  
  def evaluateArticleWithModel(articleId:Int, modelId:Int) = ???
  
  
}