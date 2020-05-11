package com.abaddon83.legal.ddMandates.adapters.ddMandateAdapters.akkaHttp.messages

import com.abaddon83.libs.akkaHttp.messages.ErrorMessage


object ErrorDDMandate{
  def build(exception: Throwable, apiResource: String): ErrorMessage ={
    ErrorMessage(apiResource,exception.getMessage,exception.getClass().getCanonicalName(),0)
  }

  def build(msg: String, apiResource: String): ErrorMessage ={
    ErrorMessage(apiResource,msg,"Missing",0)
  }
}
