package mains;

import javax.swing.JOptionPane;

import redes.SimpleClient;

public class MainSimpleClient {

	public static void main(String[] args) {
		String host = JOptionPane.showInputDialog("IP do servidor: ");
		
		if (host == null) {
			new SimpleClient("127.0.0.1").runClient();;
		} else {
			new SimpleClient(host).runClient();
		}
	}

}
