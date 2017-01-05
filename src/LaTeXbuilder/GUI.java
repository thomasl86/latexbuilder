package LaTeXbuilder;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class GUI extends JFrame implements ActionListener, WindowListener {

	
	/* Members */
	
	private static final long serialVersionUID = 1L;
	private JTextField mTxtFileRead;
	private JLabel mLabelRead;
	private JLabel mLabelWrite;
	private JTextField mTxtFileWrite;
	private JTextArea mTxtCode;
	private JButton mBtnBuild;
	private JButton mBtnLoad;
	private final String LABEL_BTN_BUILD = "Build";
	private final String LABEL_BTN_LOAD = "Load image";
	
	
	/* Constructors */
	
	public GUI(){
		
		setLayout(new FlowLayout());
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("LaTeXbuilder");
		
		mTxtCode = new JTextArea(12, 50);
		mTxtCode.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		mTxtCode.setEditable(true);
		mTxtCode.setVisible(true);
		mTxtCode.setBounds(0, 0, 250, 100);
		
		mTxtFileRead = new JTextField(25);
		mTxtFileRead.setEditable(true);
		mTxtFileRead.setVisible(true);
		
		mLabelRead = new JLabel("Read file:");
		mLabelWrite = new JLabel("Write file:");
		
		mTxtFileWrite = new JTextField(25);
		mTxtFileWrite.setEditable(true);
		mTxtFileWrite.setVisible(true);
		
		mBtnBuild = new JButton(LABEL_BTN_BUILD);
		mBtnBuild.addActionListener(this);
		mBtnBuild.setVisible(true);
		mBtnBuild.setSize(12, 12);
		
		mBtnLoad = new JButton(LABEL_BTN_LOAD);
		mBtnLoad.addActionListener(this);
		mBtnLoad.setVisible(true);
		
		
		add(mTxtCode);
		add(mLabelRead);
		add(mTxtFileRead);
		add(mLabelWrite);
		add(mTxtFileWrite);
		add(mBtnBuild);
		add(mBtnLoad);

		setSize(360, 300);
		
		addWindowListener(this);
	}

	
	/* Methods */
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals(LABEL_BTN_BUILD)){

			// --- Get contents from the code text field
			String strCode = mTxtCode.getText();
			String strFileOut = mTxtFileWrite.getText();

			LaTeXService latexService = new LaTeXService();
			//TODO fix the second input parameter
			latexService.buildLaTeX(strCode, strFileOut, true);
			
		}
		else if (command.equals(LABEL_BTN_LOAD)){
			String strFilename = mTxtFileRead.getText();
			
			String strCode;
			LaTeXService laTeXService = new LaTeXService();
			try {
				strCode = laTeXService.readLaTeXCodeFromFile(strFilename);
				mTxtCode.setText(strCode);
			} catch (IOException e) {
				mTxtCode.setText("ERROR: Could not read file (IOException)");
			}
			
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
