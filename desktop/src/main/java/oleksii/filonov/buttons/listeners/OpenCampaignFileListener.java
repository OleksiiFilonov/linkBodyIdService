package oleksii.filonov.buttons.listeners;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import oleksii.filonov.gui.MainFileChooser;

public class OpenCampaignFileListener extends FileChooserListener {

	public OpenCampaignFileListener(final MainFileChooser fileChooser, final JComponent parentComponent) {
		super(fileChooser, parentComponent);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final int returnValue = getFileChooser().showOpenDialog(getParentComponent());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			final File selectedFile = getFileChooser().getSelectedFile();
			getFileChooser().setCampaignFile(selectedFile);
		}
	}

}