package com.acadaca.fakenews.validation

import org.apache.commons.validator.routines.UrlValidator
/**
 * For custom validation
 */
object ValidationHelper {
  
 
  lazy val urlValidator = new UrlValidator()
  
  def isUrl(s:String):Boolean = {
    urlValidator.isValid(s)
  }
  
}