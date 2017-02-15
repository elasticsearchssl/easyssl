package org.elasticsearch.plugin.easyssl;

import java.io.File;

import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;

public class SSLConfig {

	private final ESLogger logger = Loggers.getLogger(this.getClass());
	
	private boolean sslTransportEnabled;
	private File 	keystoreFile;
	private char[] 	kesystorePassword;
	private String 	keyPassword;
	private String 	keyAlias;
	
	public SSLConfig(Settings settings){
		
		Settings easyssl = settings.getByPrefix("easyssl.");
		if(easyssl == null){
			throw new IllegalArgumentException("easyssl is not configured in elasticsearch.yml.");
		}
		
		String keystoreFileName = easyssl.get("keystore.file");
		if(keystoreFileName == null || keystoreFileName.trim().length() == 0){
			throw new IllegalArgumentException("ssl keystore file is not configured.");
		}
		keystoreFile = new File(keystoreFileName);
		if(!keystoreFile.exists()){
			throw new IllegalArgumentException(keystoreFileName + " does not exist.");
		}
		
		String kesystorePass = easyssl.get("keystore.password");
		if(kesystorePass == null || kesystorePass.trim().length() == 0){
			throw new IllegalArgumentException("ssl keystore password is not configured.");
		}else{
			kesystorePassword = kesystorePass.trim().toCharArray();
		}
		
		keyPassword	= easyssl.get("key.password");
		if(keyPassword == null || keyPassword.trim().length() == 0){
			logger.info("ssl key password is not configured. Use default \"\".");
			keyPassword = null;
		}
		
		keyAlias = easyssl.get("key.alias");
		if(keyAlias == null || keyAlias.trim().length() == 0){
			keyAlias = null;
			logger.info("ssl key alias is not configured. The first key entry in the key store will be used.");
		}
		
		sslTransportEnabled = easyssl.getAsBoolean("transport.ssl.enaled", true);
		
		logger.info("Config [keystoreFile=" + keystoreFile + ", kesystorePassword =" + kesystorePass + ", keyPassword=" + keyPassword
				+ ", keyAlias=" + keyAlias + ", sslTransportEnabled=" + sslTransportEnabled + "]");
	}

	

	public boolean isSslTransportEnabled() {
		return sslTransportEnabled;
	}

	public File getKeystoreFile() {
		return keystoreFile;
	}

	public char[] getKesystorePassword() {
		return kesystorePassword;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public String getKeyAlias() {
		return keyAlias;
	}

}
