package com.sibat.gongan.util

import org.apache.spark.streaming.kafka._
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.StreamingContext


object KafkaReader{

  def Read(ssc:StreamingContext,brokers:String,topics:String)={
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,
        Map[String, String]("metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder"),
        topics.split(',').toSet)
  }

}
