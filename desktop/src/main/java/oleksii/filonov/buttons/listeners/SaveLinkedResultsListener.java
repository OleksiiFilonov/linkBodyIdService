package oleksii.filonov.buttons.listeners;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import oleksii.filonov.gui.MainFileChooser;
import oleksii.filonov.reader.ReadDataException;
import oleksii.filonov.writer.DataBuilder;
import oleksii.filonov.writer.XSSFBuilder;

public class SaveLinkedResultsListener extends FileChooserListener {

	private final LinksCalculator linksCalculator;

	private final DataBuilder documentBuilder;

	public SaveLinkedResultsListener(final MainFileChooser fileChooser, final JComponent parentComponent) {
		super(fileChooser, parentComponent);
		linksCalculator = new LinksCalculator();
		documentBuilder = new XSSFBuilder();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final int returnValue = getFileChooser().showSaveDialog(getParentComponent());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			getFileChooser().setLinkedBodyFile(getFileChooser().getSelectedFile());
			try {
				linksCalculator.calculate(getFileChooser());
				documentBuilder.createDocument();
				documentBuilder.createLinkedSheetWithName("Body");
				documentBuilder.writeBodyIdsColumnToLinkedSheet("Номер Кузова", linksCalculator.getBodyIds());
				documentBuilder.linkExistingBodyIds(linksCalculator.getBodyIdLinks(), getFileChooser()
						.getCampaignFile().getName());
				documentBuilder.saveToFile(getFileChooser().getLinkedBodyFile());
			} catch (final ReadDataException exc) {
				System.err.println("Error while calculation links file: " + exc.getLocalizedMessage());
				JOptionPane.showMessageDialog(getParentComponent(), "Ошибка при поиски соответствий номеров кузовов");
			} catch (final IOException exc) {
				System.err.println("Error while saving body links file: " + exc.getLocalizedMessage());
				JOptionPane.showMessageDialog(getParentComponent(), "Ошибка при сохранении файла с результатами");
			}
		}
	}

}
