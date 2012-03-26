package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.FlowLayout;

@SuppressWarnings("serial")
public class SequenceSelectorPanel extends JPanel implements ActionListener {

	private Properties sequencesMap;

	private JComboBox comboBox = null;
	private JTextArea textArea = null;

	private JPanel comboBoxPanel = null;

	private JPanel textAreaPanel = null;

	private JLabel comboBoxLabel = null;

	private JLabel textAreaLabel = null;

	/**
	 * This is the default constructor
	 */
	public SequenceSelectorPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setSize(505, 261);
		this.add(getComboBoxPanel(), null);
		add(Box.createRigidArea(new Dimension(0, 10)));
		this.add(getTextAreaPanel(), null);
	}

	/**
	 * This method initializes comboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getComboBox() {

		if (comboBox == null) {

			comboBox = new JComboBox();
			comboBox.addActionListener(this);
		}
		return comboBox;
	}

	/**
	 * This method initializes textArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getTextArea() {

		if (textArea == null) {

			textArea = new JTextArea();
			textArea.setRows(4);
			textArea.setEditable(false);
			textArea.setLineWrap(true);
			textArea.setColumns(30);
		}
		return textArea;
	}

	public void load(String filePath) {

		sequencesMap = new Properties();

		try {
			sequencesMap.load(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		addItems();
		updateTextArea();
	}

	private void addItems() {

		@SuppressWarnings("rawtypes")
		Enumeration en = sequencesMap.propertyNames();

		while (en.hasMoreElements()) {

			String key = (String) en.nextElement();
			String sequence = sequencesMap.getProperty(key);

			boolean isValid = Pattern.matches("[HPhp]*", sequence);

			if (!isValid)
				sequencesMap.setProperty(key, "");

			comboBox.addItem(key);
		}
	}

	public String getSelectedSequnce() {

		String sequence = sequencesMap.getProperty((String) comboBox
				.getSelectedItem());

		if (sequence.equals(""))
			return null;

		return sequence;
	}

	private void updateTextArea() {

		String sequence = getSelectedSequnce();

		if (sequence == null)
			textArea.setText("Not a valid sequence");

		else
			textArea.setText(sequence);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == comboBox)
			updateTextArea();
	}

	/**
	 * This method initializes comboBoxPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getComboBoxPanel() {

		if (comboBoxPanel == null) {

			FlowLayout flowLayout = new FlowLayout();

			flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout.setHgap(20);

			comboBoxLabel = new JLabel();
			comboBoxLabel.setText("select sequnce");
			comboBoxPanel = new JPanel();
			comboBoxPanel.setLayout(flowLayout);
			comboBoxPanel.add(comboBoxLabel, null);
			comboBoxPanel.add(getComboBox(), null);
		}

		return comboBoxPanel;
	}

	/**
	 * This method initializes textAreaPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTextAreaPanel() {

		if (textAreaPanel == null) {

			FlowLayout flowLayout1 = new FlowLayout();

			flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout1.setHgap(20);

			textAreaLabel = new JLabel();
			textAreaLabel.setText("sequnce:");
			textAreaPanel = new JPanel();
			textAreaPanel.setLayout(flowLayout1);
			textAreaPanel.add(textAreaLabel, null);
			textAreaPanel.add(getTextArea(), null);
		}

		return textAreaPanel;
	}

	public void addComboListener(ActionListener listener) {
		comboBox.addActionListener(listener);
	}
} // @jve:decl-index=0:visual-constraint="10,10"
