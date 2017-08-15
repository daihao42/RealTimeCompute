package com.sibat.gongan.imp

import java.util.Properties
import java.io.FileInputStream
import scala.collection.mutable.Map

trait IPropertiesTrait {

  private val conf = Map[String,String]()

  /**************
    初始化配置
  *************/
  private def InitProperties(): Unit = {
    val props = new Properties()
    props.load(new FileInputStream("common.properties"))
    props.keySet().toArray().foreach { x =>
                    conf += (x.toString -> props.getProperty(x.toString()))
                }
  }

  /************
    获取配置
  ************/
  private def GET(propname : String)(): String = {
     if(conf.isEmpty){
       InitProperties()
     }
     conf(propname)
   }

   def SPARKMASTER = GET("master")

   def APPNAME = GET("appName")

   def KAFKABROKERS = GET("brokers")

   def INPUTTOPICS = GET("inTopics")

   def OUTPUTTOPICS = GET("outTopics")

   def KAFKAPORT = GET("kafkaport")

   def ZOOKEEPERPORT = GET("zookeeperport")

   def ZOOKEEPEQUORUM = GET("quorum")

   def HBASETABLENAME = GET("tablename")

   def ESNODES = GET("ESnodes")

   def ESTRANSPORTCLIENTPORT = GET("transportclientport")

   def ESINDEX = GET("ESIndex")

   def ESTYPE = GET("ESType")

   def ESCLUSTER = GET("ESCluster")

}
