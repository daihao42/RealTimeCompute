package com.sibat.gongan.imp

import org.apache.hadoop.hbase.client.Put

//execute Core
abstract class Core{

  /*********
    query from ES to make sure it's a safe recoder
    define a alarm info and send it to kafka( or a cache
    cause that a nearly alarm happended in the short time
    and will send lots of same info to web)
  *********/
  def Alarm(recorder:String):Map[String,String]

  /**********
    make the recorder to PUT to save it to hbase soon
  **********/
  def FormatPut(alarm:Map[String,String]):Put


  // def ESDecoder(context:String):Map[String,Any]

}
