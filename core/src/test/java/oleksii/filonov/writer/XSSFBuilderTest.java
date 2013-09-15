package oleksii.filonov.writer;

import com.google.common.collect.ListMultimap;
import oleksii.filonov.reader.CampaignProcessor;
import oleksii.filonov.reader.ColumnReaderHelper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import static oleksii.filonov.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class XSSFBuilderTest {

    private static final String LINKED_SHEET_NAME = "Body";
	private static final String BODY_ID_MARKER = "Номер кузова";
	private static final String VIN_MARKER = "VIN";

	private static final String FIRST_BODY_ID = "KMHBT51DBBU022001";
	private static final String SECOND_BODY_ID = "KMHSU81XDDU112002";
	private static final String THIRD_BODY_ID = "KMHEC41BBCA350003";
	private static final String REAL_BODY_ID_FIRST_SHEET_ROW_ONE = "KMHEC41BABA263951";
	private static final String REAL_BODY_ID_FIRST_SHEET_ROW_TEN = "KMHEC41CBBA240950";
	private static final String NO_SUCH_BODY_ID_FIRST_SHEET_ROW_TEN = "KMH00000000000000";
	private static final String[] BODY_IDS = new String[] { FIRST_BODY_ID, SECOND_BODY_ID, THIRD_BODY_ID };

    private static final String[] COMPAIGN_FILE = new String[] { "src", "test", "resources", "Campaign.xlsx" };
	private static final File CAMPAIGN_FILE = Paths.get("", COMPAIGN_FILE).toFile();

	private ColumnReaderHelper columnReaderHelper;
	private CampaignProcessor campaignProcessor;

	private XSSFBuilder excelBuilder;

	@Before
	public void setUp() throws IOException {
        if(!Files.exists(TARGET_RESOURCE)) {
            Files.createDirectory(TARGET_RESOURCE);
        }
		excelBuilder = new XSSFBuilder();
		columnReaderHelper = new ColumnReaderHelper();
		campaignProcessor = new CampaignProcessor();
		campaignProcessor.setColumnReaderHelper(columnReaderHelper);
		excelBuilder.createDocument();
		excelBuilder.createLinkedSheetWithName(LINKED_SHEET_NAME);
	}

	@Test
	public void fillResultFileWithBodyId() throws IOException {
		excelBuilder.writeBodyIdsColumnToLinkedSheet(BODY_ID_MARKER, BODY_IDS);
		excelBuilder.saveToFile(RESULT_FILE.toFile());
		final Cell[] bodyIdCells = excelBuilder.getBodyIdCells();
		assertEquals(FIRST_BODY_ID, bodyIdCells[0].getStringCellValue());
		assertEquals(SECOND_BODY_ID, bodyIdCells[1].getStringCellValue());
		assertEquals(THIRD_BODY_ID, bodyIdCells[2].getStringCellValue());
	}

	@Test
	public void formResultFileWitLinks() throws IOException, InvalidFormatException {
		final String[] bodyIds = new String[] { REAL_BODY_ID_FIRST_SHEET_ROW_ONE, REAL_BODY_ID_FIRST_SHEET_ROW_TEN,
				NO_SUCH_BODY_ID_FIRST_SHEET_ROW_TEN };
		excelBuilder.writeBodyIdsColumnToLinkedSheet(BODY_ID_MARKER, bodyIds);
		final ListMultimap<String, String> bodyIdLinks = campaignProcessor.linkBodyIdWithCampaigns(bodyIds,
				CAMPAIGN_FILE, VIN_MARKER);
		excelBuilder.linkExistingBodyIds(bodyIdLinks, CAMPAIGN_FILE.getName());
		excelBuilder.saveToFile(LINKED_RESULT_FILE.toFile());
		final Cell[] bodyIdCells = excelBuilder.getBodyIdCells();
		assertEquals(REAL_BODY_ID_FIRST_SHEET_ROW_ONE, bodyIdCells[0].getStringCellValue());
		checkWrittenLinkedResultFile();
	}

	private void checkWrittenLinkedResultFile() throws IOException, InvalidFormatException {
		final Workbook linkedResultWB = WorkbookFactory.create(LINKED_RESULT_FILE.toFile());
		final Sheet linkedResultSheet = linkedResultWB.getSheet(LINKED_SHEET_NAME);
		final Iterator<Row> rows = linkedResultSheet.iterator();
		final int bodyIdColumnIndex = columnReaderHelper.findColumnIndex(rows, BODY_ID_MARKER);
		assertEquals(0, bodyIdColumnIndex);
		final Row firstVinRow = rows.next();
		final Cell firstBodyIdCell = firstVinRow.getCell(0);
		assertEquals(REAL_BODY_ID_FIRST_SHEET_ROW_ONE, firstBodyIdCell.getStringCellValue());
		final Cell firstVinCell = firstVinRow.getCell(1);
		assertEquals("'10C029'!B2", firstVinCell.getStringCellValue());
		final Row secondVinRow = rows.next();
		final Cell tenthCompaignVinCell = secondVinRow.getCell(1);
		assertEquals("'10C029'!B11", tenthCompaignVinCell.getStringCellValue());
		final Row thirdVinRow = rows.next();
		assertNull(thirdVinRow.getCell(1));
	}

}
