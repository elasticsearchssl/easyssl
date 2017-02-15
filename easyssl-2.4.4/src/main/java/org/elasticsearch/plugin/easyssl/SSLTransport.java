package org.elasticsearch.plugin.easyssl;

import org.elasticsearch.Version;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.BigArrays;
import org.elasticsearch.indices.breaker.CircuitBreakerService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.netty.NettyTransport;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.ssl.SslContext;
import org.jboss.netty.handler.ssl.SslHandler;

/**
 * Add SSL to transport for node-to-node communication.
 * 
 * @author				Mike Huang(xinshenghuang)
 * @since				Feb 11, 2017 12:28:15 PM
 *
 * Copyright 2017
*/
public class SSLTransport extends NettyTransport{

	//is it thread-safe?
	private SslContext serverContext;
	private SslContext clientContext;
	
	@Inject
	public SSLTransport(Settings settings, ThreadPool threadPool, NetworkService networkService, BigArrays bigArrays,
			Version version, NamedWriteableRegistry namedWriteableRegistry,CircuitBreakerService circuitBreakerService) throws Exception{
		
		super(settings, threadPool, networkService, bigArrays, version, namedWriteableRegistry, circuitBreakerService);
	
		SSLConfig config = new SSLConfig(settings);
	    if (config.isSslTransportEnabled()) {
	    	Contexts contexts = SSLContextCreator.getSslContexts(config);
	    	serverContext = contexts.getServerContext();
	    	clientContext = contexts.getClientContext();
	    }
	}

	@Override
	public ChannelPipelineFactory configureClientChannelPipelineFactory() {
		if (clientContext != null){
			return new SSLClientChannelPipelineFactory(this);
		}
		return new ClientChannelPipelineFactory(this);
	}
	
	private class SSLClientChannelPipelineFactory extends ClientChannelPipelineFactory {

	    public SSLClientChannelPipelineFactory(NettyTransport transport) {
	    	super(transport);
	    }

	    @Override
		public ChannelPipeline getPipeline() throws Exception {
	    	ChannelPipeline pipeline = super.getPipeline();
	    	pipeline.addFirst("ssl handler", clientContext.newHandler());
	    	return pipeline;
	    }
	  }
	
	@Override
	public ChannelPipelineFactory configureServerChannelPipelineFactory(String name, Settings settings) {
		
		if (serverContext != null){
			return new SSLServerChannelPipelineFactory(this, name, settings);
		}
		return new ServerChannelPipelineFactory(this,name,settings);
    }
	
	private class SSLServerChannelPipelineFactory extends ServerChannelPipelineFactory {

	    public SSLServerChannelPipelineFactory(NettyTransport transport, String name, Settings settings) {
	      super(transport,name,settings);
	    }

	    @Override
		public ChannelPipeline getPipeline() throws Exception {
	    	 ChannelPipeline pipeline = super.getPipeline();
	    	 SslHandler handler = serverContext.newHandler();
	    	 pipeline.addFirst("ssl handler", handler);
	    	 return pipeline;
	    }
	  }

}
