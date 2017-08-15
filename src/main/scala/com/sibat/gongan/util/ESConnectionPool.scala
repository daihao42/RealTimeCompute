package com.sibat.gongan.util

import java.util.concurrent.ConcurrentLinkedQueue
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import java.net.InetAddress
import scala.util.Try

import com.sibat.gongan.imp.IPropertiesTrait

object  ESConnectionPool extends IPropertiesTrait{

  private val MAXPOOLSIZE = 20

  private val esconnections = new ConcurrentLinkedQueue[TransportClient]()

  private def GetConnection():TransportClient = {
    val client = TransportClient.builder().settings(
                        Settings.settingsBuilder()
                        .put("cluster.name", ESCLUSTER).build()
                  ).build()
    ESNODES.split(",").foreach( ip => {
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), ESTRANSPORTCLIENTPORT.toInt));
        })
    client
  }

  def Connection() = if(!esconnections.isEmpty) esconnections.poll else GetConnection

  def Close(client:TransportClient):Unit = if(esconnections.size <= MAXPOOLSIZE) esconnections.offer(client) else Try(client.close())

}
