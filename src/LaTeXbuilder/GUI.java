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

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


public class GUI extends Application {

	//TODO Make the file separator dynamic (e.g., / or \) again
	/* Members */
	private static String 	mStrDirWorking 	= null;
	private static Stage 	mPrimaryStage;
	private File 		mFileIn;
	private File 		mFileOut;
	private File 		mFilePreamble;
	@FXML
	private TextField 	mTxtFileWrite;
	@FXML
	private TextArea 	mTxtAreaCode;
	@FXML
	private TextArea 	mTxtAreaLog;
	@FXML
	private Button 		mBtnBuild;
	@FXML
	private Button 		mBtnWrite;
	@FXML
	private MenuBar 	mMenuBar;
	@FXML
	private MenuItem 	mMenuItemOpen;
	@FXML
	private CheckBox 	mChckbxEmbed;
	@FXML
	private MenuItem 	mMenuItemPreamble;
	@FXML
	private MenuItem 	mMenuItemClose;
	@FXML
	private TextField  	mTxtFieldBorder;
	
	
	/* Methods */
	
	public static void setStage(Stage stage){
		mPrimaryStage = stage;
	}
	
	public static void setWorkingDir(String dir){
		mStrDirWorking = dir;
	}
	
	public void printToLog(String message){
		if (mTxtAreaLog != null)
			mTxtAreaLog.appendText(message);
	}
	
	@FXML
	public void onActionPerformed(ActionEvent event) {
		
		Object eventObject = event.getSource();
		
		/*
		 * 'Build' button clicked
		 */
		if (eventObject.equals(mBtnBuild)){

			// --- Get contents from the code text field
			String strCode = mTxtAreaCode.getText();
			//TODO Check whether file with directory is given or only file
			String strFileOut = mTxtFileWrite.getText();
			if (strFileOut.equals("")){
				strFileOut = mFileIn.getAbsolutePath();
			}
			String strBorder = mTxtFieldBorder.getText();

			LaTeXService latexService = new LaTeXService();
			LaTeXService.setLaTeXBuildParams(strBorder);
			LaTeXbuilder.putIniArg("build", "border", strBorder, 0);
			latexService.buildLaTeXAsync(strCode, strFileOut, mChckbxEmbed.isSelected());
			
		}
		/*
		 * 'Open...' menu item clicked
		 */
		else if (eventObject.equals(mMenuItemOpen)){
			FileChooser fileChooser = new FileChooser();
			if (mStrDirWorking != null){
				fileChooser.setInitialDirectory(new File(mStrDirWorking));
			}
			fileChooser.getExtensionFilters().addAll(
					new ExtensionFilter("Accepted output formats", "*.png", "*.pdf"));
			mFileIn = fileChooser.showOpenDialog(mPrimaryStage);
			if (mFileIn != null){
				
				String strCode;
				LaTeXService laTeXService = new LaTeXService();
				try {
					strCode = laTeXService.readLaTeXCodeFromFile(mFileIn.getAbsolutePath());
					if (strCode == null){
						Printing.error("No LaTeX code found in the file.");
					}
					else{
						mTxtAreaCode.setText(strCode);
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
		else if (eventObject.equals(mMenuItemPreamble)){
			FileChooser fileChooser = new FileChooser();
			mFilePreamble = fileChooser.showOpenDialog(mPrimaryStage);
			if (mFilePreamble != null){
				LaTeXbuilder.putIniArg("build", "latexPreambleFile", mFilePreamble.getAbsolutePath(), 0);
				LaTeXService.setPreambleFile(mFilePreamble.getAbsolutePath());
			}
		}
		/*
		 * '...' button for write directory choice clicked
		 */
		else if (eventObject.equals(mBtnWrite)){
			DirectoryChooser dirChooser = new DirectoryChooser();
			mFileOut = dirChooser.showDialog(mPrimaryStage);
			if (mFileOut != null){				
				mTxtFileWrite.setText(mFileOut.getAbsolutePath());
			}
		}
		/*
		 * 'Close' menu item clicked
		 */
		else if (eventObject.equals(mMenuItemClose)){
			System.exit(0);
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		GUI.setStage(primaryStage);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
		Parent root = loader.load();
		//Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
		Scene scene = new Scene(root);
		
		// Adding the title to the window (primaryStage)
		primaryStage.setTitle("LaTeXbuilder");
		primaryStage.setScene(scene);
		
		GUI gui = loader.<GUI>getController();
		Printing.init(gui);
		
		// Show the window(primaryStage)
		primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		System.exit(0);
	}
}
