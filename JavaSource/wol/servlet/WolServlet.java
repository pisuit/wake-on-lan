package wol.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class WolServlet
 */
@WebServlet("/*")
public class WolServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Map<String, String> macMap;
	static {
		Map<String, String> map = new HashMap<String, String>();
		map.put("term", "5C-F9-DD-72-D6-32");
		map.put("nan", "00-1E-0B-B2-D3-4E");
		map.put("pure", "5C-F9-DD-72-C3-39");
		map.put("embre", "78-2B-CB-97-B0-21");
		
		macMap = Collections.unmodifiableMap(map);
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WolServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(macMap.get(String.valueOf(request.getPathInfo().substring(1))) == null){
			PrintWriter out = response.getWriter();
		    out.println("Who are you ??");
		} else {
			try {
	            byte[] macBytes = getMacBytes(macMap.get(String.valueOf(request.getPathInfo().substring(1))));
	            byte[] bytes = new byte[6 + 16 * macBytes.length];
	            for (int i = 0; i < 6; i++) {
	                bytes[i] = (byte) 0xff;
	            }
	            for (int i = 6; i < bytes.length; i += macBytes.length) {
	                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
	            }
	            
	            InetAddress address = InetAddress.getByName("172.16.72.255");
	            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, 9);
	            DatagramSocket socket = new DatagramSocket();
	            socket.send(packet);
	            socket.close();
	            
	            PrintWriter out = response.getWriter();
			    out.println("Wake-on-LAN packet sent.");
	        }
	        catch (Exception e) {
	            PrintWriter out = response.getWriter();
			    out.println("Failed to send Wake-on-LAN packet: "+e);
	        }
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

}
