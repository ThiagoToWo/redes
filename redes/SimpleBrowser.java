package redes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class SimpleBrowser extends JFrame {
	private JTextField entrada;
	private JEditorPane conteudo;
	
	public SimpleBrowser() {
		super("Web Browser Simples");
		entrada = new JTextField("Entre a URL do arquivo aqui.");
		entrada.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getThePage(e.getActionCommand());
			}
		});
		
		conteudo = new JEditorPane();
		conteudo.setEditable(false);
		conteudo.addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					getThePage(e.getURL().toString());
				}
				
			}
		});
		
		add(entrada, BorderLayout.NORTH);
		add(new JScrollPane(conteudo), BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setLocation(500, 200);
		setVisible(true);		
	}

	protected void getThePage(String local) {
		try {
			conteudo.setPage(local);
			entrada.setText(local);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Erro ao retornar esta URL", 
					"Má URL", JOptionPane.ERROR_MESSAGE);
		}
		
	}
}
