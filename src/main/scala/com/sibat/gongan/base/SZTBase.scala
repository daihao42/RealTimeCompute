package com.sibat.gongan.base

import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes


import com.sibat.gongan.imp.Core
import com.sibat.gongan.imp.ESQueryTrait
import com.sibat.gongan.imp.CommonCoreTrait
import com.sibat.gongan.imp.IPropertiesTrait
import com.sibat.gongan.util.EZJSONParser


object SZTBase extends Core with ESQueryTrait with IPropertiesTrait with CommonCoreTrait{

  def Alarm(p:String):Map[String,String] = {
    val hitmark = new EZJSONParser(
                  ESTermQuery(ESINDEX,ESTYPE,"cardid",p).toString
                    )
                      .getMap("hits")
                      .mark

    // make sure hit the id
    if (hitmark.getMap("total").query == "0.0")
      return Map[String,String]("status" -> "NotFound!")

    val jsonmark = hitmark
                      .getMap("hits")
                      .getList(0)
                      .getMap("_source")
                      .mark

    Map[String,String]("key" -> jsonmark.getMap("cardid").query,
                        "context" -> jsonmark.getMap("col1").query,
                        "age" -> "test",
                        "status" -> "Hit"
                      )
  }

  /*************
  解析SZT数据，并通过反射返回通用的HBase存储接口
  *************/
  def FormatPut(alarm:Map[String,String]):Put = {
    val put = new Put(Bytes.toBytes(alarm("key")))
    put.add(Bytes.toBytes("clos"),Bytes.toBytes("name"),Bytes.toBytes(alarm("context")))
    put.add(Bytes.toBytes("clos"),Bytes.toBytes("age"),Bytes.toBytes(alarm("age")))
    put
  }


  def main(args: Array[String]): Unit = {
    println(Alarm("36306020"))
  }


}
