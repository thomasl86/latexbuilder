/*
* This file is part of LaTeXbuilder.
* 
* LaTeXbuilder is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* LaTeXbuilder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with LaTeXbuilder.  If not, see <http://www.gnu.org/licenses/>.
*/

package LaTeXbuilder;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBox;

public class GUI extends JFrame implements ActionListener, WindowListener {

	
	/* Members */
	
	private static final long serialVersionUID = 1L;
	private static String mStrDirWorking 	= null;
	private static JTextArea mTxtAreaLog;
	private final String LABEL_BTN_BUILD 	= "Build";
	private final String LABEL_BTN_OPEN 	= "Open..."; 
	private final String LABEL_BTN_PREAMBLE = "Preamble file...";
	private final String LABEL_BTN_WRITE 	= "...";
	private File 		mFileIn;
	private File 		mFileOut;
	private File 		mFilePreamble;
	private JLabel 		mLabelWrite;
	private JTextField 	mTxtFileWrite;
	private JTextArea 	mTxtCode;
	private JButton 	mBtnBuild;
	private JMenuBar 	menuBar;
	private JMenu 		mnFile;
	private JMenuItem 	mntmOpen;
	private JCheckBox 	mChckbxEmbed;
	private JMenuItem 	mntmPreamble;
	private JTextField  mTxtBorder;
	
	
	/* Constructors */
	
	public GUI(){
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("LaTeXbuilder");
		
		mTxtCode = new JTextArea(12, 50);
		mTxtCode.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		mTxtCode.setEditable(true);
		mTxtCode.setVisible(true);
		mTxtCode.setBounds(0, 0, 250, 100);
		mLabelWrite = new JLabel("Save to (dir"+File.separator+"file.ext):");
		
		mTxtFileWrite = new JTextField(25);
		mTxtFileWrite.setEditable(true);
		mTxtFileWrite.setVisible(true);
		
		mTxtBorder = new JTextField();
		mTxtBorder.setColumns(10);
		mTxtBorder.setEditable(true);
		mTxtBorder.setVisible(true);
		
		mBtnBuild = new JButton(LABEL_BTN_BUILD);
		mBtnBuild.addActionListener(this);
		mBtnBuild.setVisible(true);
		mBtnBuild.setSize(12, 12);
		
		JButton btnBrowseBuild = new JButton(LABEL_BTN_WRITE);
		btnBrowseBuild.addActionListener(this);
		
		JLabel lblLog = new JLabel("Log:");
		
		mChckbxEmbed = new JCheckBox("embed code", true);
		
		mTxtAreaLog = new JTextArea();
		
		JLabel lblBorder = new JLabel("Border:");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(6)
							.addComponent(mTxtCode, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(mChckbxEmbed)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(mLabelWrite)
										.addGroup(groupLayout.createSequentialGroup()
											.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addGroup(groupLayout.createSequentialGroup()
													.addComponent(lblBorder)
													.addPreferredGap(ComponentPlacement.RELATED)
													.addComponent(mTxtBorder, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
												.addComponent(mTxtFileWrite, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(btnBrowseBuild, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)))
									.addGap(18)
									.addComponent(mBtnBuild, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblLog)))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(mTxtAreaLog, GroupLayout.PREFERRED_SIZE, 342, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(mTxtCode, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(mLabelWrite)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(mTxtFileWrite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnBrowseBuild)
						.addComponent(mBtnBuild, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(mChckbxEmbed)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBorder)
						.addComponent(mTxtBorder, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(11)
					.addComponent(lblLog)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(mTxtAreaLog, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(15, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);

		setSize(378, 414);
		
		addWindowListener(this);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmOpen = new JMenuItem(LABEL_BTN_OPEN);
		mntmOpen.addActionListener(this);
		mnFile.add(mntmOpen);
		
		mntmPreamble = new JMenuItem(LABEL_BTN_PREAMBLE);
		mntmPreamble.addActionListener(this);
		mnFile.add(mntmPreamble);
	}

	
	/* Methods */
	
	public static void setWorkingDir(String dir){
		mStrDirWorking = dir;
	}
	
	public static void printToLog(String message){
		if (mTxtAreaLog != null)
			mTxtAreaLog.setText(message);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		/*
		 * 'Build' button clicked
		 */
		if (command.equals(LABEL_BTN_BUILD)){

			// --- Get contents from the code text field
			String strCode = mTxtCode.getText();
			//TODO Check whether file with directory is given or only file
			String strFileOut = mTxtFileWrite.getText();
			if (strFileOut.equals("")){
				strFileOut = mFileIn.getAbsolutePath();
			}
			String strBorder = mTxtBorder.getText();

			LaTeXService latexService = new LaTeXService();
			LaTeXService.setLaTeXBuildParams(strBorder);
			LaTeXbuilder.putIniArg("build", "border", strBorder, 0);
			latexService.buildLaTeXAsync(strCode, strFileOut, mChckbxEmbed.isSelected());
			
		}
		/*
		 * 'Open...' menu item clicked
		 */
		else if (command.equals(LABEL_BTN_OPEN)){
			JFileChooser fileChooser = new JFileChooser();
			if (mStrDirWorking != null){
				fileChooser.setCurrentDirectory(new File(mStrDirWorking));
			}
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				mFileIn = fileChooser.getSelectedFile();
				
				String strCode;
				LaTeXService laTeXService = new LaTeXService();
				try {
					strCode = laTeXService.readLaTeXCodeFromFile(mFileIn.getAbsolutePath());
					if (strCode == null){
						Printing.error("No LaTeX code found in the file.");
					}
					else{
						mTxtCode.setText(strCode);
						mTxtFileWrite.setText(mFileIn.getAbsolutePath());
						String strFilename = mFileIn.getName();
						int idx = mFileIn.getAbsolutePath().indexOf(strFilename);
						String strDir = mFileIn.getAbsolutePath().substring(0,idx);
						LaTeXbuilder.putIniArg("gui", "workingdir", strDir, 1);
					}
				} catch (IOException e) {
					Printing.error("Could not read file (IOException).");
				}
			}
		}
		/*
		 * 'Preamble...' menu item clicked
		 */
		else if (command.equals(LABEL_BTN_PREAMBLE)){
			JFileChooser fileChooser = new JFileChooser();
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				mFilePreamble = fileChooser.getSelectedFile();
				LaTeXbuilder.putIniArg("build", "latexPreambleFile", mFilePreamble.getAbsolutePath(), 0);
				LaTeXService.setPreambleFile(mFilePreamble.getAbsolutePath());
			}
		}
		/*
		 * '...' button for write directory choice clicked
		 */
		else if (command.equals(LABEL_BTN_WRITE)){
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				mFileOut = fileChooser.getSelectedFile();
				
				mTxtFileWrite.setText(mFileOut.getAbsolutePath());
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void windowOpened(WindowEvent e) {
		
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
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}
}
