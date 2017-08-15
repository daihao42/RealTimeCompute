package com.sibat.gongan.util

import scala.util.parsing.json.JSON

/********
  construct by string of json
  and getMap(str) to get Map(str -> Any)
  and getList(int) to get List(int)
  and u can mark ur cursor by mark()
  then use mark to query continue
  !! only use query u can get the result(String)
********/
class EZJSONParser(jsonfull:Option[Any]){

  def this(json:String) = {
    this(JSON.parseFull(json))
  }

  private var cur : Option[Any] = jsonfull

  def getMap(str:String) = {
    cur = cur.get.asInstanceOf[Map[String,Any]].get(str)
    this
  }

  /**
  **/
  def getList(index:Int) = {
    try{
      cur = Option(cur.get.asInstanceOf[List[Any]](index))
    }
    catch{
      case ex : Exception => cur = Option(Map[String,Any]())
    }
    this
  }

  def mark() = {
    new EZJSONParser(cur)
  }

  def query() = {
    val res = cur
    cur = jsonfull
    res.get.toString
  }

}
