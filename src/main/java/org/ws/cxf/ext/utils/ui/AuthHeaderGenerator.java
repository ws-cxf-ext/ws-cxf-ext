package org.ws.cxf.ext.utils.ui;

import static org.ws.cxf.ext.utils.HTTPUtils.httpBuildQuery;
import static org.ws.cxf.ext.utils.SecurityUtils.generateAuthParameters;

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
		JFrame frame = new JFrame("Auth header generator");

		JTextField iuri = new JTextField();
		JTextField ienv = new JTextField();
		JTextField iappid = new JTextField();
		JTextArea iresult = new JTextArea();
		JScrollPane sp = new JScrollPane(iresult);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, GRID_WIDTH));

		JLabel luri = new JLabel("URI");
		JLabel lenv = new JLabel("Environment");
		JLabel lappid = new JLabel("Appid");
		JLabel lauth = new JLabel("Authorization");

		JButton bgenerate = new JButton("Result");

		JButton berase = new JButton("Erase");

		panel.add(luri);
		panel.add(iuri);

		panel.add(lenv);
		panel.add(ienv);

		panel.add(lappid);
		panel.add(iappid);

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
				iuri.setText("");
				ienv.setText("");
				iappid.setText("");
				iresult.setText("");
			}
		});

		bgenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iresult.setText("Auth " + httpBuildQuery(generateAuthParameters(iappid.getText(), ienv.getText(), iuri.getText())));
			}
		});
	}
}
