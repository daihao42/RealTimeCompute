package com.sibat.gongan.imp

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.client.transport.TransportClient

import com.sibat.gongan.util.ESConnectionPool

trait ESQueryTrait {

  private def RecallResource[T](process: TransportClient => T):T = {
    val client = ESConnectionPool.Connection
      try{
        process(client)
      }
      finally{
        ESConnectionPool.Close(client)
      }
  }

  /*******************
    term query as k-v
  *******************/
  def ESTermQuery(index:String,types:String,col:String,query:String) = {
    def _subQuery(client:TransportClient) = {
      client.prepareSearch(index)
        .setTypes(types)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(QueryBuilders.termQuery(col, query))                 // Query
        // .setPostFilter(QueryBuilders.rangeQuery("col").from("A").to("B"))     // Filter
        // .setFrom(0).setSize(60).setExplain(true)
        .get()
    }
    RecallResource(_subQuery)
  }

}
