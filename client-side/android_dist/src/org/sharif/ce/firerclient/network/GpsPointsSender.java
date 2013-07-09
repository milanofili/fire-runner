package org.sharif.ce.firerclient.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.sharif.ce.firerclient.model.GpsPoint;
import org.sharif.ce.firerclient.network.NetMessages.NetGpsPoint;
import org.sharif.ce.firerclient.network.NetMessages.NetWayPoint;
import org.sharif.ce.firerclient.settings.SettingManager;

import android.widget.Toast;

public class GpsPointsSender {
	private List<GpsPoint> packettoSend;
	private IGpsPointsSender delegate;
	
	private Socket sockettoRemoteEndPoint;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private String username;
	private String password;
	private double weight;
	
	public GpsPointsSender (List<GpsPoint> pointtoSend, IGpsPointsSender delegate) {
		setPackettoSend(pointtoSend);
		setDelegate(delegate);
	}
	
	public GpsPointsSender (List<GpsPoint> points, String username, String password, 
			double weight, IGpsPointsSender delegate) {
		setPackettoSend(points);
		this.username = username;
		this.password = password;
		this.weight = weight;
		setDelegate(delegate);
	}
	
	public void connect(String host, int port) {
		
		
		try {
			
			sockettoRemoteEndPoint = new Socket(host, port);
			inputStream = new DataInputStream(sockettoRemoteEndPoint.getInputStream());
			outputStream = new DataOutputStream(sockettoRemoteEndPoint.getOutputStream());
			
			outputStream.write(getHttpMessagetoSend(host));
			System.out.println(getHttpMessagetoSend(host));
			
			byte[] buffer = new byte[1000];
			
			delegate.responseReady("");
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
			delegate.errorOccurred(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			delegate.errorOccurred(e.getMessage());
		}
		
		
		
		
	}
	
	public void close() {
		
	}

	public List<GpsPoint> getPackettoSend() {
		return packettoSend;
	}

	public void setPackettoSend(List<GpsPoint> packettoSend) {
		this.packettoSend = packettoSend;
	}

	public IGpsPointsSender getDelegate() {
		return delegate;
	}

	public void setDelegate(IGpsPointsSender delegate) {
		this.delegate = delegate;
	}
	
	private byte[] getGpsPointBytes() {
		
		NetWayPoint.Builder nwp = NetWayPoint.newBuilder();
		for (int i =0; i < packettoSend.size(); i++) {
			NetGpsPoint ngp = toNetGpsPoint(packettoSend.get(i));	
			
			nwp.addGpsPoints(ngp);
		}
		
		return nwp.build().toByteArray();
	}
	
	public byte[] getNetWayPoints(String username, String password, double weight) {
		
		NetWayPoint.Builder nwp = NetWayPoint.newBuilder();
		for (int i =0; i < packettoSend.size(); i++) {
			NetGpsPoint ngp = toNetGpsPoint(packettoSend.get(i));	
			
			nwp.addGpsPoints(ngp);
		}
		
		nwp.setPassword(password);
		nwp.setUsername(username);
		nwp.setWeight(weight);
		
		return nwp.build().toByteArray();
	}
	
	private NetGpsPoint toNetGpsPoint(GpsPoint point) {
		NetGpsPoint.Builder ngp = NetGpsPoint.newBuilder();
		
		ngp.setAlt(point.getAltitude());
		ngp.setLat(point.getLatitude());
		ngp.setLng(point.getLongitude());
		ngp.setTimeTag(point.getTimeTag().toString());
		ngp.setWayId(point.getWayId());
		
		return ngp.build();
	}
	
	private byte[] getHttpMessagetoSend(String host) {
		
		
		
		byte[] bodyStream = getNetWayPoints(username, password, weight);
		
		String headerStr = String.format("POST /firerbackend/getPosition HTTP/1.1\r\nHost: %s\r\nContent-Length: %d\r\nAccept-Language: en-US,en;q=0.8\r\nAccept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\nUser-Agent: Firer android client\r\nCookie: \r\n\r\n", host, bodyStream.length);
		byte[] headerStream = headerStr.getBytes();
		
		byte[] ret = new byte[headerStream.length + bodyStream.length];
		System.arraycopy(headerStream, 0, ret, 0, headerStream.length);
		System.arraycopy(bodyStream, 0, ret, headerStream.length, bodyStream.length);
		
		return ret;
	}
	
	private String readResponseHeader(DataInputStream stream) {
		return "";
	}

	

}
