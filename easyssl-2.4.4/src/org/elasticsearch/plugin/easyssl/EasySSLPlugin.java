package org.elasticsearch.plugin.easyssl;

import org.elasticsearch.http.HttpServerModule;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.TransportModule;

/**
 * 1. Add ssl to http port, communication between client and cluster.
 * 2. Add ssl to transport, communication between nodes. It is optional, controlled by configure property "transport.ssl.enabled".
 * 
 * @author				Mike Huang
 * @since				Feb 11, 2017 12:28:15 PM
 *
 * Copyright 2017 
*/
public class EasySSLPlugin extends Plugin {

	  @Override
	  public String name() {
		  return "easyssl";
	  }
	
	  @Override
	  public String description() {
		  return "SSL Plugin";
	  }
	
	  public void onModule(HttpServerModule module) {
		  module.setHttpServerTransport(HttpsServerTransport.class, this.getClass().getSimpleName());
	  }
	  
	  
	  public void onModule(TransportModule module) {
		  module.setTransport(SSLTransport.class, this.getClass().getSimpleName());
	  }
	  
}