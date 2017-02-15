package org.elasticsearch.plugin.easyssl;

import org.jboss.netty.handler.ssl.SslContext;

/**
 *
 * @author				Mike Huang(xinshenghuang)
 * @since				Feb 12, 2017 3:54:51 PM
 *
 * Copyright 2017
 */
public class Contexts {

	private SslContext serverContext;
	private SslContext clientContext;


	public SslContext getServerContext() {
		return serverContext;
	}

	public SslContext getClientContext() {
		return clientContext;
	}

	public void setServerContext(SslContext serverContext) {
		this.serverContext = serverContext;
	}

	public void setClientContext(SslContext clientContext) {
		this.clientContext = clientContext;
	}
	
	
}
