package oleksii.filonov.writer;

import com.google.common.collect.ListMultimap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WorkbookBuilder implements DataBuilder {

    private static final int SHIFT_ROW_OFFSET = 1;
    private Workbook clientWorkbook;
	private CellStyle foundCellStyle;
    private CellStyle linkCellStyle;
	private CreationHelper creationHelper;
    private String pathToCampaignFile;

    @Override
	public void useWorkbook(Workbook clientWorkbook) throws IOException, InvalidFormatException {
		this.clientWorkbook = clientWorkbook;
		creationHelper = this.clientWorkbook.getCreationHelper();
		initFoundCellStyle();
        initLinkCellStyle();
	}

	@Override
	public void saveToFile(final File fileToSave) throws IOException {
		final FileOutputStream recordStream = new FileOutputStream(fileToSave);
		clientWorkbook.write(recordStream);
		recordStream.close();
	}

    @Override
    public void setPathToCampaignFile(String pathToCampaignFile) {
        this.pathToCampaignFile = pathToCampaignFile;
    }

    @Override
	public void assignTasks(final Cell[] bodyIdCells, final ListMultimap<String, String> linkedBodies) {
		for (final Cell bodyIdCell : bodyIdCells) {
			final List<String> links = linkedBodies.get(bodyIdCell.getStringCellValue());
			if (!links.isEmpty()) {
                bodyIdCell.setCellStyle(foundCellStyle);
                final Row bodyIdRow = bodyIdCell.getRow();
				linkBodyIdsWithVINLists(bodyIdCell.getColumnIndex(), links, bodyIdRow);
				int cellIndex = 1;
				while (bodyIdRow.getCell(cellIndex) != null) {
					++cellIndex;
				}
			}
		}
	}

	private void linkBodyIdsWithVINLists(final int bodyIdColumnIndex, final List<String> vinListIds, final Row bodyIdRow) {

        addVinListToBodyId(bodyIdColumnIndex, vinListIds.get(0), bodyIdRow);

        final Sheet clientSheet = bodyIdRow.getSheet();
        final int bodyIdRowRowNum = bodyIdRow.getRowNum();
        for (int i = 1; i < vinListIds.size(); i++) {
            clientSheet.shiftRows(bodyIdRowRowNum + i, clientSheet.getLastRowNum(), SHIFT_ROW_OFFSET);
            final Row newRow = clientSheet.createRow(bodyIdRowRowNum + i);
            addVinListToBodyId(bodyIdColumnIndex, vinListIds.get(i), newRow);
		}
	}

    private void addVinListToBodyId(final int bodyIdColumnIndex, final String vinListId, final Row bodyIdRow) {
        final Cell linkToVin = bodyIdRow.createCell(bodyIdColumnIndex + 1, Cell.CELL_TYPE_STRING);
        linkToVin.setCellValue(vinListId);
        final Hyperlink cellHyperlink = creationHelper.createHyperlink(Hyperlink.LINK_FILE);
        cellHyperlink.setAddress(pathToCampaignFile + "#" + vinListId);
        cellHyperlink.setLabel(vinListId);
        linkToVin.setHyperlink(cellHyperlink);
        linkToVin.setCellStyle(linkCellStyle);
    }

    private void initLinkCellStyle() {
		linkCellStyle = clientWorkbook.createCellStyle();
        linkCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        linkCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	}

    private void initFoundCellStyle() {
		foundCellStyle = clientWorkbook.createCellStyle();
        foundCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		foundCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
	}

}
