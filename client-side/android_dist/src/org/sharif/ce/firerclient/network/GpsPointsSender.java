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

import android.widget.Toast;

public class GpsPointsSender {
	private List<GpsPoint> packettoSend;
	private IGpsPointsSender delegate;
	
	private Socket sockettoRemoteEndPoint;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	public GpsPointsSender (List<GpsPoint> pointtoSend, IGpsPointsSender delegate) {
		setPackettoSend(pointtoSend);
		setDelegate(delegate);
	}
	
	public void connect(String host, int port) {
		
		
		try {
			
			sockettoRemoteEndPoint = new Socket(host, port);
			inputStream = new DataInputStream(sockettoRemoteEndPoint.getInputStream());
			outputStream = new DataOutputStream(sockettoRemoteEndPoint.getOutputStream());
			
			outputStream.write(getHttpMessagetoSend(host));
			System.out.println(getHttpMessagetoSend(host));
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		byte[] bodyStream = getGpsPointBytes();
		
		String headerStr = String.format("POST / HTTP/1.0\r\nHost: %s\r\nContent-Length: %d\r\n\r\n", host, bodyStream.length);
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
