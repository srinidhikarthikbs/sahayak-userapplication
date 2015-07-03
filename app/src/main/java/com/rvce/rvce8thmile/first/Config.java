package com.rvce.rvce8thmile.first;

public interface Config {

	// used to share GCM regId with application server - using php app server
	static final String APP_SERVER_URL = "http://ibmhackblind.mybluemix.net/gcmnew.php?shareRegId=1&email="+statictry.email;

	// GCM server using java
	// static final String APP_SERVER_URL =
	// "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "526631252908";


}
