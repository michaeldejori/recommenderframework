package GUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextFrame extends JFrame {
	
	private JTextArea textArea;
	
	public TextFrame(String title) {
		super(title);
		setSize(1100, 600);
		setLocation(50, 50);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		textArea = new JTextArea();
		JScrollPane scrollingArea = new JScrollPane(textArea);
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BorderLayout());
		panel_5.add(scrollingArea, BorderLayout.CENTER);
		getContentPane().add(panel_5, BorderLayout.CENTER);
		setVisible(true);
	}
	
	public void newMessage(String message) {
		this.textArea.append(message);

	}

}
