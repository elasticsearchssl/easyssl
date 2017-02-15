package org.elasticsearch.plugin.easyssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.AccessController;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.jboss.netty.handler.ssl.SslContext;

/**
 * SSL Contexts creator.
 * 
 * Create server SSL context.
 * Create client SSL context. When transport ssl enabled, both contexts are required(each node acts as both client and server).
 * 
 * @author				Mike Huang
 * @since				Feb 11, 2017 12:28:15 PM
 *
 * Copyright 2017 
*/
public final class SSLContextCreator {
	
	private static final ESLogger logger = Loggers.getLogger(SSLContextCreator.class);
	
	private SSLContextCreator(){}
		
	private static final Contexts CONTEXTS = new Contexts();
	
	public static Contexts getSslContexts(SSLConfig config) throws Exception{
		  
		if(CONTEXTS.getClientContext() == null){
			
			
			AccessController.doPrivileged(
	            new PrivilegedAction<Void>() {
	              @Override
	              public Void run() {
	            	  
	                try {  
	                	
	                	KeyStore ks = KeyStore.getInstance("JKS");
	        			ks.load(new FileInputStream(config.getKeystoreFile()), config.getKesystorePassword());
	        	      
	        			String alias 		= getKeyAlias(config,ks);
	        			String keyPassword 	= config.getKeyPassword();
	        			Key key = ks.getKey(alias, keyPassword.toCharArray());
	        			if (key == null) {
	        				throw new IllegalArgumentException("Private key not found in keystore for alias: " + alias);
	        			}
	        			
	                	File chainFile = createCertChainPEMFile(ks.getCertificateChain(alias));
	        			File keyFile   = createPrivateKeyPEMFile(key);
	        				            						
						if (config.isSslTransportEnabled()) {
							CONTEXTS.setClientContext(SslContext.newClientContext(chainFile));
						}		
						CONTEXTS.setServerContext(SslContext.newServerContext(chainFile, keyFile, null)); 
						
	                }catch(Exception e){
	                	logger.error(e.getMessage(),e);
	                	System.exit(1);
	                }
	                return null;
	              }
	            }
	            );
			
		}
		
		return CONTEXTS;
	}

	private static String getKeyAlias(SSLConfig config,KeyStore ks)throws Exception{
	  
		  String keyAlias = config.getKeyAlias();
		  if(keyAlias != null){
			  if(!ks.isKeyEntry(keyAlias)){
				  throw new IllegalArgumentException(keyAlias + " is not a key entry.");
			  }  	  
		  }else{
			  keyAlias = getFirstKeyEntryAlias(ks);
			  if(keyAlias == null){
				  throw new IllegalArgumentException("There is no key entry in key store " + config.getKeystoreFile());
			  }
		  }
		  return keyAlias;
	}
  
	private static File createCertChainPEMFile(Certificate[] cchain)throws Exception{
	  
		  StringBuilder sb = new StringBuilder();
	      for (Certificate c : cchain) {
	        sb.append("-----BEGIN CERTIFICATE-----\n");
	        sb.append(new String(Base64.getEncoder().encode(c.getEncoded())));
	        sb.append("\n");
	        sb.append("-----END CERTIFICATE-----\n");
	      }
	      return tempFile("certchain", "pem", sb.toString());
	}
  
	private static File createPrivateKeyPEMFile(Key key)throws Exception{
	  
		  StringBuilder sb = new StringBuilder();
	      sb.append("---BEGIN PRIVATE KEY---\n")
	      .append(new String(Base64.getEncoder().encode(key.getEncoded())))
	      .append("\n")
	      .append("---END PRIVATE KEY---");
	      return tempFile("privatekey", "pem", sb.toString());
	}
  
  	private static String getFirstKeyEntryAlias(KeyStore ks)throws Exception{
		  
		  Enumeration<String> e = ks.aliases();
		  while(e.hasMoreElements()){
			  String alias = e.nextElement();
			  if(ks.isKeyEntry(alias)){
				  return alias;
			  }
		  }
		  return null;
	}
  
  	private static File tempFile(String prefix, String suffix, String content)throws IOException{

	    File tempFile = File.createTempFile(prefix, suffix);
	    tempFile.deleteOnExit();
	    
	    try(FileWriter fw = new FileWriter(tempFile)){
	    	fw.write(content);
	    }
	    return tempFile;
  	}
  
 
}
