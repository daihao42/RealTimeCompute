package com.sibat.gongan

import org.apache.spark.{SparkConf,SparkContext}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.hadoop.hbase.client.{Put,Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.{StreamingContext,Seconds,Minutes}
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.streaming.dstream.InputDStream
import java.util.Properties

import com.sibat.gongan.util._
import com.sibat.gongan.imp.IPropertiesTrait
import com.sibat.gongan.util.HBaseConnectionPool
import com.sibat.gongan.base.SZTBase


object Main extends IPropertiesTrait {

	def sparkInit(url:String)(appName:String):SparkContext = new SparkContext(new SparkConf().setAppName(appName).setMaster(url))

	def sparkStreamingInit(sc:SparkContext)(sec:Int):StreamingContext = new StreamingContext(sc,Seconds(sec))

	def kafkaProducerConfig(brokers:String) = {
    	val p = new Properties()
    	p.setProperty("bootstrap.servers", brokers)
    	p.setProperty("key.serializer", classOf[StringSerializer].getName)
    	p.setProperty("value.serializer", classOf[StringSerializer].getName)
    	p
  	}

	def main(args: Array[String]): Unit = {

		val sc = sparkInit(SPARKMASTER)(APPNAME)

		//加载配置文件到worker上
		sc.addFile("/home/hadoop/dai/sparktest/sparkhbase/Gongan/common.properties")

		val ssc = sparkStreamingInit(sc)(60)

		ssc.checkpoint("checkpoint")

		// 广播KafkaSink
		val kafkaProducer: Broadcast[KafkaSink[String, String]] = ssc.sparkContext.broadcast(KafkaSink[String, String](kafkaProducerConfig(KAFKABROKERS)))

		KafkaReader.Read(ssc,KAFKABROKERS,INPUTTOPICS).
			foreachRDD({
				rdd => {
					if(!rdd.isEmpty){
					rdd.foreachPartition(
						partitionrdd => {
							val table = HBaseConnectionPool.Connection(HBASETABLENAME)
							partitionrdd.foreach(
							 	p =>{
										val alarm:Map[String,String] = SZTBase.Alarm(p._2.toString)

										kafkaProducer.value.send(OUTPUTTOPICS, alarm("status"))
										// write to hbase
										if(alarm("status") == "Hit")  table.put(SZTBase.FormatPut(alarm))
								})
							table.flushCommits()
							table.close()
							})
						}
					}
				})

		ssc.start()
		ssc.awaitTermination()
	}

}
