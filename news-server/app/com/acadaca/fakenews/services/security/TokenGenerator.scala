package com.acadaca.fakenews.services.security

trait TokenGenerator {
  
    /**
     * Generates a random token
     */
    def generateToken():String;
}