# get data from our producer into ElasticSearch
connect-standalone config/connect-standalone.properties config/elasticsearch.properties
bin/connect-standalone.sh ../connect-hands-on/config/connect-standalone.properties ../connect-hands-on/config/elasticsearch.properties

GET wikimedia.recentchange/_search
{
 "query": {
   "match_all": {}
 }
}