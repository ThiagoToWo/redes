package redes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SimpleServer extends JFrame {
	private JTextField entrada;
	private JTextArea display;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ServerSocket servidor;
	private Socket conexao;
	private int count = 1;
	
	public SimpleServer() {
		super("Servidor");
		
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
	
	public void runServer() {
		try {
			servidor = new ServerSocket(12345, 100);
			
			while (true) {
				try {
					esperarConexao();
					getStreams();
					processarConexao();
				} catch (EOFException e1) {
					displayMensagem("\nServidor terminou a conexão");
				} finally {
					fecharConexao();
					count++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void esperarConexao() throws IOException {
		displayMensagem("\nEsperando conexão.");
		conexao = servidor.accept();
		displayMensagem("\nConexão " + count + " recebida de: " + 
				conexao.getInetAddress().getHostName());
	}
	
	private void getStreams() throws IOException {
		out = new ObjectOutputStream(conexao.getOutputStream());
		out.flush();
		
		in = new ObjectInputStream(conexao.getInputStream());
		
		displayMensagem("\nI/O Streams retornados.");
	}
	
	private void processarConexao() throws IOException {
		String message = "Conectado com sucesso.";
		enviarMensagem(message);
		setEntradaEditavel(true);
		
		do {
			try {
				message = (String) in.readObject();
				displayMensagem("\n" + message);
			} catch (ClassNotFoundException e) {
				displayMensagem("\nObjeto de tipo desconhecido recebido.");
			}
		} while (!message.equals("CLIENTE >>> FIM"));
	}
	
	private void fecharConexao() {
		displayMensagem("\nTerminando a conexão.");
		setEntradaEditavel(false);
		
		try {
			out.close();
			in.close();
			conexao.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void enviarMensagem(String message) {
		try {
			out.writeObject("SERVIDOR >>> " + message);
			out.flush();
			displayMensagem("\nSERVIDOR >>> " + message);
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
