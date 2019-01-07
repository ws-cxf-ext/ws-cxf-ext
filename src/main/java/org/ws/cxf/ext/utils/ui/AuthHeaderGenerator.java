package org.ws.cxf.ext.utils.ui;

import static org.ws.cxf.ext.Constants.EMPTY_STRING;
import static org.ws.cxf.ext.utils.Utils.generateSignature;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * User interface to generate header tokens.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public class AuthHeaderGenerator {

	private static final int GRID_WIDTH = 2;

	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 200;

	/**
	 * Labels.
	 */
	private static final String LABEL_URI = "URI";
	private static final String LABEL_ENV = "Environment";
	private static final String LABEL_APPID = "Appid";
	private static final String LABEL_AUTH = "Authorization";

	/**
	 * Buttons.
	 */
	private static final String BTN_RESULT = "Result";
	private static final String BTN_ERASE = "Erase";

	/**
	 * Title
	 */
	private static final String TITLE_UI = "Auth header generator";

	/**
	 * Hide default constructor
	 */
	private AuthHeaderGenerator() {

	}

	/**
	 * Swing GUI for generate auth token.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		JFrame frame = new JFrame(TITLE_UI);

		JTextField iuri = new JTextField();
		JTextField ienv = new JTextField();
		JTextField iappid = new JTextField();
		JTextArea iresult = new JTextArea();
		JScrollPane sp = new JScrollPane(iresult);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, GRID_WIDTH));

		JLabel luri = new JLabel(LABEL_URI);
		JLabel lenv = new JLabel(LABEL_ENV);
		JLabel lappid = new JLabel(LABEL_APPID);
		JLabel lauth = new JLabel(LABEL_AUTH);

		JButton bgenerate = new JButton(BTN_RESULT);
		JButton berase = new JButton(BTN_ERASE);

		addFieldPanel(luri, iuri, panel);
		addFieldPanel(lenv, ienv, panel);
		addFieldPanel(lappid, iappid, panel);

		panel.add(lauth);
		panel.add(sp);

		panel.add(berase);
		panel.add(bgenerate);

		frame.add(panel);
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		berase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iuri.setText(EMPTY_STRING);
				ienv.setText(EMPTY_STRING);
				iappid.setText(EMPTY_STRING);
				iresult.setText(EMPTY_STRING);
			}
		});

		bgenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iresult.setText(generateSignature(iappid.getText(), ienv.getText(), iuri.getText()));
			}
		});
	}



	/**
	 * Adding field to panel.
	 * 
	 * @param label
	 * @param input
	 * @param panel
	 */
	private static void addFieldPanel(JLabel label, JTextField input, JPanel panel) {
		panel.add(label);
		panel.add(input);
	}
}
