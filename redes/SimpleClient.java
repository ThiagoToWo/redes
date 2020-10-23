package redes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SimpleClient extends JFrame {
	private JTextField entrada;
	private JTextArea display;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String message = "";
	private String servidor;
	private Socket cliente;
	
	public SimpleClient(String host) {
		super("Cliente");
		
		servidor = host;
		
		entrada = new JTextField();
		entrada.setEditable(false);
		entrada.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				enviarMensagem(e.getActionCommand());
				entrada.setText("");
			}
		});
		
		display = new JTextArea();
		
		add(entrada, BorderLayout.NORTH);
		add(new JScrollPane(display), BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocation(500, 200);
		setVisible(true);
	}
	
	public void runClient() {
		try {
			conectarAoServidor();
			getStreams();
			processarConexao();
		} catch (EOFException e1) {
			displayMensagem("\nCliente terminou a conexão");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fecharConexao();
		}		
	}

	private void conectarAoServidor() throws IOException{
		displayMensagem("\nTentando conexão");
		
		cliente = new Socket(InetAddress.getByName(servidor), 12345);
		
		displayMensagem("\nConectado a: " + cliente.getInetAddress().getHostName());
	}
	
	private void getStreams() throws IOException {
		out = new ObjectOutputStream(cliente.getOutputStream());
		out.flush();
		
		in = new ObjectInputStream(cliente.getInputStream());
		
		displayMensagem("\nI/O Streams retornados.");
	}
	
	private void processarConexao() throws IOException {
		setEntradaEditavel(true);
		
		do {
			try {
				message = (String) in.readObject();
				displayMensagem("\n" + message);
			} catch (ClassNotFoundException e) {
				displayMensagem("\nObjeto de tipo desconhecido recebido.");
			}
		} while (!message.equals("SERVIDOR >>> FIM"));
	}
	
	private void fecharConexao() {
		displayMensagem("\nTerminando a conexão.");
		setEntradaEditavel(false);
		
		try {
			out.close();
			in.close();
			cliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void enviarMensagem(String message) {
		try {
			out.writeObject("CLIENTE >>> " + message);
			out.flush();
			displayMensagem("\nCLIENTE >>> " + message);
		} catch (IOException e) {
			display.append("\nErro ao enviar a mensagem.");
		}
	}
	
	private void displayMensagem(final String message) {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				display.append(message);				
			}
		};
		
		SwingUtilities.invokeLater(run);
	}
	
	private void setEntradaEditavel(final boolean editable) {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				entrada.setEditable(editable);				
			}
		};
		SwingUtilities.invokeLater(run);
	}
}
