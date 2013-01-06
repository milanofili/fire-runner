package org.sharif.ce.firerclient.network;

public interface IGpsPointsSender {
	public void responseReady(String response);
	public void errorOccurred(String errorStr); 
}
