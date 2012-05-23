package GUI;

import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import recommendation.Mediator;
import javax.swing.JTextField;

public class RecommenderGUI extends JFrame {
	private static RecommenderGUI gui = null;

	Mediator m = null;
	private JRadioButton rdbtnFreebase = null;
	private JRadioButton rdbtnDbpedia = null;
	private JTextArea textArea = null;
	private JTextField useridtextField = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Action initializeDataSource = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	private final Action action = new SwingAction_2();
	
	private final Action action_2 = new SwingAction_3();

	public RecommenderGUI() {
		super("Recommender System Framework");
		setSize(1000, 600);
		setLocation(50, 50);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		// create Mediator
		m = new Mediator(this);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "JPanel title",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_1);

		rdbtnFreebase = new JRadioButton("Freebase");
		panel_1.add(rdbtnFreebase);
		rdbtnFreebase.setAction(action);

		rdbtnDbpedia = new JRadioButton("dbPedia");
		panel_1.add(rdbtnDbpedia);
		rdbtnDbpedia.setAction(action_1);

		JButton btnInitializeData = new JButton("Initialize Data");
		panel_1.add(btnInitializeData);
		btnInitializeData.setAction(initializeDataSource);

		textArea = new JTextArea();
		getContentPane().add(textArea, BorderLayout.SOUTH);
		
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.WEST);
		
		useridtextField = new JTextField();
		panel_2.add(useridtextField);
		useridtextField.setColumns(10);
		
		JButton btnMakeRecommendaati = new JButton("Make Recommendaati");
		btnMakeRecommendaati.setAction(action_2);
		panel_2.add(btnMakeRecommendaati);
		setVisible(true);
	}

	public static void main(String[] args) {
		gui = new RecommenderGUI();
	}

	public void pushStatusMessage(String s) {
		this.textArea.append(s + "\n");
	}

	/**
	 * initializeDataSource
	 * 
	 * @author michael
	 * 
	 */
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Initialize");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println("Action Button");
			m.initializeDataSource("dbPedia");
		}
	}

	private class SwingAction_1 extends AbstractAction {
		public SwingAction_1() {
			putValue(NAME, "dbpedia");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private class SwingAction_2 extends AbstractAction {
		public SwingAction_2() {
			putValue(NAME, "freebase");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
		}
	}
	private class SwingAction_3 extends AbstractAction {
		public SwingAction_3() {
			putValue(NAME, "Make Recommendation");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
			m.makeRecommendation(useridtextField.getText());
		}
	}
}
