package org.elasticsearch.plugin.easyssl;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.http.netty.NettyHttpServerTransport;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.ssl.SslContext;

/**
 * Add SSL to http port for client and cluster communication.
 * 
 * @author				Mike Huang
 * @since				Feb 11, 2017 12:28:15 PM
 *
 * Copyright 2017 
*/
public class HttpsServerTransport extends NettyHttpServerTransport{

	//is it thread-safe?
	private SslContext serverContext;
	
	@Inject
	public HttpsServerTransport(Settings settings, NetworkService networkService, BigArrays bigArrays) throws Exception{
		
	    super(settings, networkService, bigArrays);
	    SSLConfig config = new SSLConfig(settings);
	    this.serverContext  = SSLContextCreator.getSslContexts(config).getServerContext();
	}
	
	@Override
	public ChannelPipelineFactory configureServerChannelPipelineFactory() {
	    return new HttpSSLChannelPipelineFactory(this);
	}
	
	private class HttpSSLChannelPipelineFactory extends HttpChannelPipelineFactory {

	    public HttpSSLChannelPipelineFactory(NettyHttpServerTransport transport) {
	    	super(transport, true);
	    }

	    @Override
		public ChannelPipeline getPipeline() throws Exception {
	    	ChannelPipeline pipeline = super.getPipeline();	 
	        pipeline.addFirst("ssl.handler", serverContext.newHandler());
	        return pipeline;
	    }
	  }
}

