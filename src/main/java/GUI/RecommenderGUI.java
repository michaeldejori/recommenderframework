package GUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import recommendation.Mediator;
import recommendation.itemfeature.ItemFeature;
import java.awt.FlowLayout;
import java.awt.Component;
import javax.swing.Box;

public class RecommenderGUI extends JFrame implements MassageListener {
	private static RecommenderGUI gui = null;

	Mediator m = null;
	private JRadioButton rdbtnFreebase = null;
	private JRadioButton rdbtnDbpedia = null;
	private JTextArea textArea = null;
	private JTextField useridtextField = null;
	private JRadioButton rdbtnUnweighted = null;
	private JRadioButton rdbtnWeighted = null;
	private JRadioButton rdbtnMyCosine = null;
	private JRadioButton rdbtnCosineSimilartiy = null;
	private JRadioButton rdbtnPearsonCor = null;
	private JRadioButton rdbtnLoglikelihood = null;
	private JTextArea textArea_1 = null;
	private CheckBoxList cl = null;

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
		setSize(1100, 600);
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
		rdbtnDbpedia.setSelected(true);
		panel_1.add(rdbtnDbpedia);
		rdbtnDbpedia.setAction(action_1);

		JButton btnInitializeData = new JButton("Initialize Data");
		panel_1.add(btnInitializeData);
		btnInitializeData.setAction(initializeDataSource);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Item Features",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_3);

		rdbtnUnweighted = new JRadioButton("Unweighted");
		rdbtnUnweighted.setSelected(true);
		rdbtnUnweighted.setAction(new AbstractAction("Unweighted") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnWeighted.setSelected(false);
				}

			}
		});
		panel_3.add(rdbtnUnweighted);

		rdbtnWeighted = new JRadioButton("Weighted");
		rdbtnWeighted.setAction(new AbstractAction("Weighted") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnUnweighted.setSelected(false);
				}

			}
		});
		panel_3.add(rdbtnWeighted);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Similarity Method",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_4);

		rdbtnMyCosine = new JRadioButton("Mein Cosine Similartiy");
		rdbtnMyCosine.setAction(new AbstractAction("Mein Cosine Similartiy") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnPearsonCor.setSelected(false);
					rdbtnLoglikelihood.setSelected(false);
					rdbtnCosineSimilartiy.setSelected(false);
				}

			}
		});
		panel_4.add(rdbtnMyCosine);

		rdbtnCosineSimilartiy = new JRadioButton("Cosine Similartiy");
		rdbtnCosineSimilartiy
				.setAction(new AbstractAction("Cosine Similartiy") {

					public void actionPerformed(ActionEvent e) {
						if (((JRadioButton) e.getSource()).isSelected()) {
							rdbtnMyCosine.setSelected(false);
							rdbtnPearsonCor.setSelected(false);
							rdbtnLoglikelihood.setSelected(false);
						}

					}
				});
		panel_4.add(rdbtnCosineSimilartiy);

		rdbtnPearsonCor = new JRadioButton("Pearson Cor");
		rdbtnPearsonCor.setAction(new AbstractAction("Pearson Cor (mahout)") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnMyCosine.setSelected(false);
					rdbtnCosineSimilartiy.setSelected(false);
					rdbtnLoglikelihood.setSelected(false);
				}

			}
		});
		panel_4.add(rdbtnPearsonCor);

		rdbtnLoglikelihood = new JRadioButton("Loglikelihood");
		rdbtnLoglikelihood.setAction(new AbstractAction("Loglikelihood") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnMyCosine.setSelected(false);
					rdbtnCosineSimilartiy.setSelected(false);
					rdbtnPearsonCor.setSelected(false);
				}

			}
		});
		panel_4.add(rdbtnLoglikelihood);

		textArea = new JTextArea();
		getContentPane().add(textArea, BorderLayout.SOUTH);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.WEST);
		panel_2.setLayout(new GridLayout(2, 1));
		
		JPanel panel_6 = new JPanel();
		panel_2.add(panel_6);

		JLabel lblNewLabel = new JLabel("UserID");
		panel_6.add(lblNewLabel);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		panel_6.add(horizontalGlue);

		useridtextField = new JTextField("325");
		panel_6.add(useridtextField);
		useridtextField.setColumns(10);

		JButton btnMakeRecommendaati = new JButton("Make Recommendaati");
		panel_6.add(btnMakeRecommendaati);
		btnMakeRecommendaati.setAction(action_2);
		textArea_1 = new JTextArea();
		//panel_5.add(textArea_1, BorderLayout.CENTER);
		JScrollPane scrollingArea = new JScrollPane(textArea_1);
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BorderLayout());
		panel_5.add(scrollingArea, BorderLayout.CENTER);
		getContentPane().add(panel_5, BorderLayout.CENTER);
		
		Vector<String> vecString = ItemFeature.getDistinctPredicates();
		
		LinkedHashMap<String, Boolean> lhm = new LinkedHashMap<String, Boolean>();
		for (int i = 0; i < vecString.size(); i++){
			lhm.put(vecString.get(i), true);
		}
		
		JPanel panel_7 = new JPanel();
		panel_2.add(panel_7);
		cl = new CheckBoxList(lhm);
		JScrollPane scrollList = new JScrollPane(cl);
		panel_7.add(scrollList);
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
			getSelectedPredicateFilterValues();
			// m.makeRecommendation3(useridtextField.getText());
			try {
				int x = Integer.parseInt(useridtextField.getText());
				int weighted = getWeightedMethod();
				int matching = getMatchingMethod();
				m.makeRecommendation(weighted, 2, matching, useridtextField.getText());
			} catch (NumberFormatException nFE) {
				pushStatusMessage("Input not a valid Integer");
			}
		}
	}


	public int getWeightedMethod() {
		if (this.rdbtnWeighted.isSelected()){
			return Mediator.WEIGHTED;
		} else if (this.rdbtnUnweighted.isSelected())
			return Mediator.UNWEIGHTED;
		else return -1;
	}
	
	public int getMatchingMethod() {
		if (this.rdbtnCosineSimilartiy.isSelected()){
			return Mediator.COSINE_SIM;
		} else if (this.rdbtnLoglikelihood.isSelected()){
			return Mediator.LOGLIKELIHOOD_SIM;
		} else if (this.rdbtnMyCosine.isSelected()){
			return Mediator.MYCOSINE_SIM;
		} else if (this.rdbtnPearsonCor.isSelected()){
			return Mediator.PEARSON_SIM;
		} else
			return -1;
	}
	
	public void newMessage(String message) {
		this.textArea_1.append(message);

	}
	
	public Vector<String> getSelectedPredicateFilterValues(){
		Vector<String> filter = new Vector<String>();
		LinkedHashMap<String, Boolean> hm = this.cl.getItems();
		Set<String> s = hm.keySet();
		for (Iterator<String> it = s.iterator(); it.hasNext(); ) {
			String str = it.next();
			if(hm.get(str)){
				filter.add(str);
			}
		}
		return filter;
	}
}
