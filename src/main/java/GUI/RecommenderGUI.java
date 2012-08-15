package GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
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

public class RecommenderGUI extends JFrame implements MassageListener {

	Mediator m = null;
	private JRadioButton rdbtnFreebase = null;
	private JRadioButton rdbtnDbpedia = null;
	private JTextArea textArea = null;
	private JTextField useridtextField = null;
	private JRadioButton rdbtnUnweighted = null;
	private JRadioButton rdbtnWeighted = null;
	private JRadioButton rdbtnCombUnWei = null;
	private JRadioButton rdbtnWeightedUserprofile = null;
	private JRadioButton rdbtnUnweightedUserprofile = null;
	private JRadioButton rdbtnCosineSimilartiy = null;
	private JRadioButton rdbtnMyCosineI_00 = null;
	private JRadioButton rdbtnMyCosineII_0 = null;
	private JRadioButton rdbtnMyCosineIII_11 = null;
	private JRadioButton rdbtnApproach2 = null;
	private JRadioButton rdbtnApproach3 = null;
	private JTextArea textArea_1 = null;
	private CheckBoxList<String> cl = null;
	private JButton btnSelectImportant = null;
	private JButton deselectAllButton = null;
	private JButton selectAllButton = null;
	private JPanel panel_7 = null;
	private JScrollPane scrollListFeatures = null;
	private JTextField textFieldMinSamePred = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Action initializeDataSource = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	private final Action action_2 = new SwingAction_3();
	private final Action precisionrecallAction = new SwingAction_4();
	private final Action top5ListAction = new Top5Action();
	private final Action selectImportantAction = new SwingAction_SelImportFeatures();

	@SuppressWarnings("serial")
	public RecommenderGUI() {
		super("Recommender System Framework");
		setSize(1300, 600);
		setLocation(50, 50);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		// create Mediator
		m = new Mediator(this);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Data Source", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setLayout(new GridLayout(0, 2));
		panel.add(panel_1);

		rdbtnFreebase = new JRadioButton("Freebase");
		rdbtnFreebase.setSelected(true);
		panel_1.add(rdbtnFreebase);
		rdbtnFreebase.setAction(new AbstractAction("Freebase") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnDbpedia.setSelected(false);
				}

			}
		});

		rdbtnDbpedia = new JRadioButton("dbPedia");
		rdbtnDbpedia.setSelected(false);
		panel_1.add(rdbtnDbpedia);
		rdbtnDbpedia.setAction(new AbstractAction("dbPedia") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnFreebase.setSelected(false);
				}

			}
		});

		rdbtnUnweighted = new JRadioButton("Unweighted");
		panel_1.add(rdbtnUnweighted);
		rdbtnUnweighted.setSelected(true);
		rdbtnUnweighted.setAction(new AbstractAction("Unweighted") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnWeighted.setSelected(false);
					rdbtnCombUnWei.setSelected(false);
				}

			}
		});

		rdbtnWeighted = new JRadioButton("Weighted");
		panel_1.add(rdbtnWeighted);
		rdbtnWeighted.setAction(new AbstractAction("Weighted") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnUnweighted.setSelected(false);
					rdbtnCombUnWei.setSelected(false);
					
				}

			}
		});
		
		rdbtnCombUnWei = new JRadioButton("Combined");
		panel_1.add(rdbtnCombUnWei);
		rdbtnCombUnWei.setAction(new AbstractAction("Combined") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnUnweighted.setSelected(false);
					rdbtnWeighted.setSelected(false);
				}

			}
		});

		JButton btnInitializeData = new JButton("Initialize Data");
		panel_1.add(btnInitializeData);
		btnInitializeData.setAction(initializeDataSource);

		JPanel weightedProfilePanel = new JPanel();
		weightedProfilePanel.setBorder(new TitledBorder(null, "User Profile Weighted", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		weightedProfilePanel.setLayout(new GridLayout(0, 1));
		panel.add(weightedProfilePanel);

		rdbtnWeightedUserprofile = new JRadioButton("Weighted profile");
		weightedProfilePanel.add(rdbtnWeightedUserprofile);
		rdbtnWeightedUserprofile.setAction(new AbstractAction("Weighted profile") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnUnweightedUserprofile.setSelected(false);
				}
			}
		});

		rdbtnUnweightedUserprofile = new JRadioButton("Unweighted profile");
		weightedProfilePanel.add(rdbtnUnweightedUserprofile);
		rdbtnUnweightedUserprofile.setSelected(true);
		rdbtnUnweightedUserprofile.setAction(new AbstractAction("Unweighted profile") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnWeightedUserprofile.setSelected(false);
				}
			}
		});

		JPanel minSamePredPanel = new JPanel();
		minSamePredPanel.setBorder(new TitledBorder(null, "Matching restrictions", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		minSamePredPanel.setLayout(new GridLayout(0, 1));
		panel.add(minSamePredPanel);

		JLabel lblNewLabel_1 = new JLabel("New label");
		minSamePredPanel.add(lblNewLabel_1);

		textFieldMinSamePred = new JTextField("0");
		minSamePredPanel.add(textFieldMinSamePred);
		textFieldMinSamePred.setColumns(10);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Similarity Method", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		panel.add(panel_4);

		rdbtnCosineSimilartiy = new JRadioButton("Cosine Similartiy");
		rdbtnCosineSimilartiy.setSelected(true);
		rdbtnCosineSimilartiy.setAction(new AbstractAction("Cosine Similartiy") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnMyCosineI_00.setSelected(false);
					rdbtnMyCosineII_0.setSelected(false);
					rdbtnMyCosineIII_11.setSelected(false);
				}

			}
		});
		panel_4.add(rdbtnCosineSimilartiy);

		rdbtnMyCosineI_00 = new JRadioButton("MyCosine I(00)");
		rdbtnMyCosineI_00.setAction(new AbstractAction("MyCosine I(00)") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnCosineSimilartiy.setSelected(false);
					rdbtnMyCosineII_0.setSelected(false);
					rdbtnMyCosineIII_11.setSelected(false);
				}

			}
		});
		panel_4.add(rdbtnMyCosineI_00);

		rdbtnMyCosineII_0 = new JRadioButton("MyCosine II (O)");
		rdbtnMyCosineII_0.setAction(new AbstractAction("MyCosine II (O)") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnCosineSimilartiy.setSelected(false);
					rdbtnMyCosineI_00.setSelected(false);
					rdbtnMyCosineIII_11.setSelected(false);
				}

			}
		});
		panel_4.add(rdbtnMyCosineII_0);
		
		rdbtnMyCosineIII_11 = new JRadioButton("MyCosine III (11)");
		rdbtnMyCosineIII_11.setAction(new AbstractAction("MyCosine III (11)") {

			public void actionPerformed(ActionEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					rdbtnCosineSimilartiy.setSelected(false);
					rdbtnMyCosineI_00.setSelected(false);
					rdbtnMyCosineII_0.setSelected(false);
				}

			}
		});
		panel_4.add(rdbtnMyCosineIII_11);

		JPanel panel_8 = new JPanel();
		panel_8.setBorder(new TitledBorder(null, "Profile Construction Method", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		panel.add(panel_8);

		rdbtnApproach2 = new JRadioButton("Approach 2");
		panel_8.add(rdbtnApproach2);

		rdbtnApproach3 = new JRadioButton("Appraoch 3");
		rdbtnApproach3.setSelected(true);
		panel_8.add(rdbtnApproach3);

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

		JButton recPreButton = new JButton("New button");
		recPreButton.setAction(precisionrecallAction);
		panel_6.add(recPreButton);

		JButton top5button = new JButton("New button");
		top5button.setAction(top5ListAction);
		panel_6.add(top5button);
		
		JButton signTest = new JButton("New button");
		panel_6.add(signTest);
		signTest.setAction(new AbstractAction("SignTest") {

			public void actionPerformed(ActionEvent e) {
				Vector<String> predFilter = getSelectedPredicateFilterValues();
				// m.makeRecommendation3(useridtextField.getText());
				try {
					int weighted = getWeightedMethod();
					int matching = getMatchingMethod();
					int profcon = getProfileConstructionMethod();
					int weightedUserProfile = getWeightesUserProfile();
					m.signTest(weighted, profcon, matching, weightedUserProfile,
							Integer.parseInt(textFieldMinSamePred.getText()), predFilter);		
					
				} catch (NumberFormatException nFE) {
					System.out.println(nFE.getStackTrace());
					pushStatusMessage("Input not a valid Integer");
				}

			}
		});

		textArea_1 = new JTextArea();
		// panel_5.add(textArea_1, BorderLayout.CENTER);
		JScrollPane scrollingArea = new JScrollPane(textArea_1);
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BorderLayout());
		panel_5.add(scrollingArea, BorderLayout.CENTER);
		getContentPane().add(panel_5, BorderLayout.CENTER);

		LinkedHashMap<String, Boolean> lhm = new LinkedHashMap<String, Boolean>();
		cl = new CheckBoxList<String>(lhm);

		panel_7 = new JPanel();
		panel_2.add(panel_7);

		btnSelectImportant = new JButton("Select important");
		btnSelectImportant.setAction(selectImportantAction);
		panel_7.add(btnSelectImportant);
		
		deselectAllButton = new JButton("Select important");
		deselectAllButton.setAction(new AbstractAction("None") {
			
			public void actionPerformed(ActionEvent arg0) {
				cl.selectNone();
			}
		});
		panel_7.add(deselectAllButton);
		
		selectAllButton = new JButton("All");
		selectAllButton.setAction(new AbstractAction("All") {
			
			public void actionPerformed(ActionEvent arg0) {
				cl.selectAll();
			}
		});
		panel_7.add(selectAllButton);

		scrollListFeatures = new JScrollPane(cl);
		panel_7.add(scrollListFeatures);
		setVisible(true);
	}

	public static void main(String[] args) {
		new RecommenderGUI();
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
	@SuppressWarnings("serial")
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Initialize");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println("Action Button");
			if (rdbtnDbpedia.isSelected() && rdbtnWeighted.isSelected()) {
				m.initializeDataSource(ItemFeature.DBPEDIA_WEIGHTED);
			} else if (rdbtnDbpedia.isSelected() && rdbtnUnweighted.isSelected()) {
				m.initializeDataSource(ItemFeature.DBPEDIA_UNWEIGHTED);
			} else if (rdbtnDbpedia.isSelected() && rdbtnCombUnWei.isSelected()) {
				m.initializeDataSource(ItemFeature.DBPEDIA_COMB_WEI_UNW);
			} else if (rdbtnFreebase.isSelected() && rdbtnWeighted.isSelected()) {
				m.initializeDataSource(ItemFeature.FREEBASE_WEIGHTED);
			} else if (rdbtnFreebase.isSelected() && rdbtnUnweighted.isSelected()) {
				m.initializeDataSource(ItemFeature.FREEBASE_UNWEIGHTED);
			} else if (rdbtnFreebase.isSelected() && rdbtnCombUnWei.isSelected()) {
				m.initializeDataSource(ItemFeature.FREEBASE_COMB_WEI_UNW);
			}
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

	private class SwingAction_3 extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SwingAction_3() {
			putValue(NAME, "Make Recommendation");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			Vector<String> predFilter = getSelectedPredicateFilterValues();
			// m.makeRecommendation3(useridtextField.getText());
			int weighted = getWeightedMethod();
			int matching = getMatchingMethod();
			int prof_construction_method = getProfileConstructionMethod();
			int weightedUserProfile = getWeightesUserProfile();
			/*
			 * m.makeRecommendation(weighted, prof_construction_method, matching, useridtextField.getText());
			 */
			m.makesmallTestRecommendation(weighted, prof_construction_method, matching, weightedUserProfile,
					useridtextField.getText(), predFilter);

		}
	}

	private class SwingAction_4 extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public SwingAction_4() {
			putValue(NAME, "Recall Precision");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			Vector<String> predFilter = getSelectedPredicateFilterValues();
			// m.makeRecommendation3(useridtextField.getText());
			try {
				int weighted = getWeightedMethod();
				int matching = getMatchingMethod();
				int profcon = getProfileConstructionMethod();
				int weightedUserProfile = getWeightesUserProfile();
				m.determineTreshhold(weighted, profcon, matching, weightedUserProfile,
						Integer.parseInt(textFieldMinSamePred.getText()), predFilter);
			} catch (NumberFormatException nFE) {
				System.out.println(nFE.getStackTrace());
				pushStatusMessage("Input not a valid Integer");
			}
		}
	}

	private class Top5Action extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public Top5Action() {
			putValue(NAME, "Spearman Corr");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			Vector<String> predFilter = getSelectedPredicateFilterValues();
			// m.makeRecommendation3(useridtextField.getText());
			try {
				int weighted = getWeightedMethod();
				int matching = getMatchingMethod();
				int profcon = getProfileConstructionMethod();
				int weightedUserProfile = getWeightesUserProfile();
				m.spearmanCorrelation(weighted, profcon, matching, weightedUserProfile,
						Integer.parseInt(textFieldMinSamePred.getText()), predFilter);		
				
			} catch (NumberFormatException nFE) {
				System.out.println(nFE.getStackTrace());
				pushStatusMessage("Input not a valid Integer");
			}
		}

	}

	private class SwingAction_SelImportFeatures extends AbstractAction {

		public SwingAction_SelImportFeatures() {
			putValue(NAME, "Select important");
			putValue(SHORT_DESCRIPTION, "Select important features");
		}

		public void actionPerformed(ActionEvent arg0) {
			Vector<String> filter = new Vector<String>();
			if (rdbtnFreebase.isSelected()) {
				filter.add("http://rdf.freebase.com/ns/media_common.netflix_title.netflix_genres");
				filter.add("http://rdf.freebase.com/ns/film.film.genre");
				filter.add("http://test.com#hasActor");
				filter.add("http://rdf.freebase.com/ns/film.film.directed_by");
				filter.add("http://rdf.freebase.com/ns/film.film.cinematography");
				filter.add("http://rdf.freebase.com/ns/film.film.produced_by");
				filter.add("http://rdf.freebase.com/ns/film.film.edited_by");
				filter.add("http://rdf.freebase.com/ns/film.film.music");
				filter.add("http://rdf.freebase.com/ns/film.film.subjects");
				filter.add("http://rdf.freebase.com/ns/film.film.written_by");
				filter.add("http://rdf.freebase.com/ns/film.film.executive_produced_by");
				filter.add("http://rdf.freebase.com/ns/film.film.film_casting_director");
				filter.add("http://rdf.freebase.com/ns/film.film.story_by");
				filter.add("http://rdf.freebase.com/ns/film.film.film_production_design_by");
				filter.add("http://rdf.freebase.com/ns/film.film.costume_design_by");
				filter.add("http://rdf.freebase.com/ns/award.award_nominated_work.award_nominations");
				filter.add("http://rdf.freebase.com/ns/film.film.soundtrack");

			} else if (rdbtnDbpedia.isSelected()) {
				filter.add("http://dbpedia.org/ontology/director");
				filter.add("http://dbpedia.org/ontology/distributor");
				filter.add("http://dbpedia.org/ontology/musicComposer");
				filter.add("http://dbpedia.org/ontology/producer");
				filter.add("http://dbpedia.org/ontology/starring");
				filter.add("http://dbpedia.org/ontology/writer");
				filter.add("http://dbpedia.org/ontology/editing");
				filter.add("http://dbpedia.org/ontology/cinematography");
				filter.add("http://dbpedia.org/ontology/country");
				filter.add("http://dbpedia.org/property/director");
				filter.add("http://dbpedia.org/property/distributor");
				filter.add("http://dbpedia.org/property/music");
				filter.add("http://dbpedia.org/property/producer");
				filter.add("http://dbpedia.org/property/starring");
				filter.add("http://dbpedia.org/property/studio");
				filter.add("http://dbpedia.org/property/artist");
				filter.add("http://dbpedia.org/property/genre");
				filter.add("http://dbpedia.org/property/screenplay");
				filter.add("http://dbpedia.org/property/story");
				filter.add("http://dbpedia.org/property/state");
				filter.add("http://dbpedia.org/property/allWriting");
				filter.add("http://dbpedia.org/property/editing");
				filter.add("http://dbpedia.org/property/writer");
				filter.add("http://dbpedia.org/property/country");
				filter.add("http://purl.org/dc/terms/subject");
				filter.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
			}
			cl.selectinVector(filter);
			cl.repaint();
		}
	}

	public int getWeightedMethod() {
		if (this.rdbtnWeighted.isSelected()) {
			return Mediator.WEIGHTED;
		} else if (this.rdbtnUnweighted.isSelected())
			return Mediator.UNWEIGHTED;
		else
			return -1;
	}

	public int getMatchingMethod() {
		if (this.rdbtnCosineSimilartiy.isSelected()) {
			return Mediator.COSINE_SIM;
		} else if (this.rdbtnMyCosineII_0.isSelected()) {
			return Mediator.MYCOSINEII_0;
		} else if (this.rdbtnMyCosineI_00.isSelected()) {
			return Mediator.MYCOSINEI_00;
		} else if (this.rdbtnMyCosineIII_11.isSelected()) {
			return Mediator.MYCOSINEIII_11;
		} else
			return -1;
	}

	public int getProfileConstructionMethod() {
		if (this.rdbtnApproach2.isSelected()) {
			return Mediator.PROF_CONS_APP_2;
		} else if (this.rdbtnApproach3.isSelected()) {
			return Mediator.PROF_CONS_APP_3;
		}
		return -1;
	}

	public void newMessage(String message) {
		this.textArea_1.append(message);

	}

	public Vector<String> getSelectedPredicateFilterValues() {
		Vector<String> filter = new Vector<String>();
		LinkedHashMap<String, Boolean> hm = this.cl.getItems();
		Set<String> s = hm.keySet();
		for (Iterator<String> it = s.iterator(); it.hasNext();) {
			String str = it.next();
			if (hm.get(str)) {
				filter.add(str);
			}
		}
		return filter;
	}

	public int getWeightesUserProfile() {
		if (rdbtnWeightedUserprofile.isSelected()) {
			return Mediator.USER_PROFILE_WEIGHTED;
		} else if (rdbtnUnweightedUserprofile.isSelected()) {
			return Mediator.USER_PROFILE_UNWEIGHTED;
		}
		System.out.println("ERROR 1");
		return -1;
	}

	public void putFeaturesInCheckBox(Vector<String> features) {
		LinkedHashMap<String, Boolean> lhm = new LinkedHashMap<String, Boolean>();

		for (int i = 0; i < features.size(); i++) {
			lhm.put(features.get(i), true);
			System.out.println(features.get(i));
		}

		panel_7.remove(scrollListFeatures);

		cl = new CheckBoxList<String>(lhm);
		scrollListFeatures = new JScrollPane(cl);
		panel_7.add(scrollListFeatures);
	}
}
