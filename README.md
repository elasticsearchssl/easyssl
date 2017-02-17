# easyssl
An elasticsearch plugin adding ssl to both http(client-to-cluster) and transport(node-to-node) with several simple java files.
It is simple, easy, and free.
##To install:
  plugin install https://github.com/elasticsearchssl/easyssl/blob/master/archive/2.4.4.zip?raw=true
##To configure:
* elasticsearch.yml:
  <pre>
    #when this plug-in in play, https is enabled between client and elasticsearch cluster.
    #to disable easyssl, you have to remove this plug-in.
    #assuming your keystore contains key pair and certiface chains for this node, and trusted certificates of all nodes in the cluster.
    #put your keystore in /concig directory of this node.
    easyssl:
      keystore.file: ${path.home}/config/keystore.jks
      #replace with yours:
      keystore.password: easyssl
      #replace with yours:
      key.password: easyssl
      #optional. if not specified, the first key entry in the store is used.
      #key.alias:
      #add ssl to node-to-node communication. default is true.
      transport.ssl.enaled: true
 </pre>
* To grant elasticsearch the access to call Security.getProperty("ssl.KeyManagerFactory.algorithm"), add the line below to /bin/elasticsearch.sh before JAVA_OPTS is referred:
<pre>
  export JAVA_OPTS="${JAVA_OPTS} -Djava.security.policy=file://${ES_HOME}/plugins/easyssl/elasticsearch-security.policy"
</pre>
