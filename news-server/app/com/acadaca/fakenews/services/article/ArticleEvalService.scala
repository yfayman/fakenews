package com.acadaca.fakenews.services.article

trait ArticleEvalService {
  def generateModel(articleIds:List[Int]):GenerateModelResponse
  
  def evaluateArticleWithModel(articleId:Int, modelId:Int)
}

case class GenerateModelResponse()