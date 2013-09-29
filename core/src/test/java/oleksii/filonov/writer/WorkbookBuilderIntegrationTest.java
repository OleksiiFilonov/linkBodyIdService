package oleksii.filonov.writer;

import static oleksii.filonov.TestConstants.*;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import com.google.common.collect.*;
import oleksii.filonov.reader.*;
import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.ss.usermodel.*;
import org.hamcrest.*;
import org.junit.*;

public class WorkbookBuilderIntegrationTest {

	private static final String BODY_ID_MARKER = "Номер кузова";
	private static final String VIN_MARKER = "VIN";
    private static final int VIN_LINK_COLUMN_INDEX = 6;
    private static final int VIN_DESC_COLUMN_INDEX = 7;
    private static final Matcher<Integer> LINK_COL_INDEX = CoreMatchers.equalTo(VIN_LINK_COLUMN_INDEX);
    private static final Matcher<Integer> DESC_COL_INDEX = CoreMatchers.equalTo(VIN_DESC_COLUMN_INDEX);
    private static final String DESC_10C150 = "УСТРАНЕНИЕ ШУМА ОТ ПЕРЕДНЕГО СИДЕНЬЯ С РУЧНОЙ РЕГУЛИРОВКОЙ(TSB No. In English : HCE11-91-P560-RBMDVF)";
    private static final String DESC_10C116 = "ЗАМЕНА КЛЕММ КАБЕЛЯ АКБ(TSB No. In English : HCE11-11-P180-RBMDENFDLMTQCMHRNF)";
    private static final String DESC_10CR07 = "SOLARIS / ACCENT (RBr) ПЛАСТИКОВАЯ ШАЙБА ВЕДУЩЕГО ВАЛА (ОБЕ СТОРОНЫ) СНЯТИЕ";
    private static final String DESC_10CR08 = "SOLARIS / ACCENT (RBr) ЗАМЕНА РЕЙКИ РУЛЕВОГО УПРАВЛЕНИЯ С УСИЛИТЕЛЕМ В СБОРЕ";

    private ColumnReaderHelper columnReaderHelper;
    private ColumnExcelReader columnExcelReader;
	private CampaignProcessor campaignProcessor;
    private VinListProcessor vinListProcessor;

    @Before
	public void setUp() throws InvalidFormatException, IOException {
        if(!Files.exists(TARGET_RESOURCE)) {
            Files.createDirectory(TARGET_RESOURCE);
        }
		columnReaderHelper = new ColumnReaderHelper();
		campaignProcessor = new CampaignProcessor();
		campaignProcessor.setColumnReaderHelper(columnReaderHelper);
        columnExcelReader = new ColumnExcelReader();
        columnExcelReader.setColumnReaderHelper(columnReaderHelper);
        vinListProcessor = new VinListProcessor();
        vinListProcessor.setColumnReaderHelper(columnReaderHelper);
    }

	@Test
	public void formLinkedDocument() throws IOException, InvalidFormatException {
        final Workbook clientWB = WorkbookFactory.create(CLIENT_FILE);
        final Sheet clientSheet = clientWB.getSheetAt(0);
        final Cell[] bodyIds = columnExcelReader.getColumnValues(clientSheet, BODY_ID_MARKER);
        DataBuilder excelBuilder = new WorkbookBuilder();
        excelBuilder.useWorkbook(clientWB);
        excelBuilder.setPathToCampaignFile(CAMPAIGN_FILE.getName());
		final ListMultimap<String, Cell> linkedBodyIdWithCampaigns = campaignProcessor.linkBodyIdWithCampaigns(
				bodyIds, CAMPAIGN_FILE, VIN_MARKER);
        final Map<String, String> bodyIdDescriptionMap = vinListProcessor.mapVinListIdToDescription(CAMPAIGN_FILE, "Номер кампании", "Описание");
        excelBuilder.setVinListDescriptionMap(bodyIdDescriptionMap);
        excelBuilder.assignTasks(bodyIds, linkedBodyIdWithCampaigns);
		excelBuilder.saveToFile(LINKED_RESULT_PATH.toFile());
        verifyResults();
    }

    private void verifyResults() throws IOException, InvalidFormatException {
        final Workbook workbookForVerification = WorkbookFactory.create(LINKED_RESULT_PATH.toFile());
        final Sheet verifyClientSheet = workbookForVerification.getSheetAt(0);
        final Iterator<Row> clientIterator = verifyClientSheet.rowIterator();
        //check for cell type
        final Cell cell_10c150_firstOccurrence = columnReaderHelper.findCell(clientIterator, "10C150");
        assertThat(cell_10c150_firstOccurrence.getColumnIndex(), LINK_COL_INDEX);
        assertThat(columnReaderHelper.findCellFrom(cell_10c150_firstOccurrence, clientIterator, DESC_10C150).getColumnIndex(), DESC_COL_INDEX);
        final Cell cell_10C116 = columnReaderHelper.findCell(clientIterator, "10C116");
        assertThat(cell_10C116.getColumnIndex(), LINK_COL_INDEX);
        final Cell descCell_10C116 = columnReaderHelper.findCellFrom(cell_10C116, clientIterator, DESC_10C116);
        assertThat(descCell_10C116.getColumnIndex(), DESC_COL_INDEX);
        final Cell cell_10C150_secondOccurrence = columnReaderHelper.findCellFrom(descCell_10C116, clientIterator, "10C150");
        assertThat(cell_10C150_secondOccurrence.getColumnIndex(), LINK_COL_INDEX);
        assertThat(columnReaderHelper.findCellFrom(cell_10C150_secondOccurrence, clientIterator, DESC_10C150).getColumnIndex(), DESC_COL_INDEX);
        Cell cell_10CR07 = columnReaderHelper.findCell(clientIterator, "10CR07");
        assertThat(cell_10CR07.getColumnIndex(), LINK_COL_INDEX);
        assertThat(columnReaderHelper.findCellFrom(cell_10CR07, clientIterator, DESC_10CR07).getColumnIndex(), DESC_COL_INDEX);
        Cell cell_10CR08 = columnReaderHelper.findCell(clientIterator, "10CR08");
        assertThat(cell_10CR08.getColumnIndex(), LINK_COL_INDEX);
        assertThat(columnReaderHelper.findCellFrom(cell_10CR08, clientIterator, DESC_10CR08).getColumnIndex(), DESC_COL_INDEX);
        Cell cell_20CR22 = columnReaderHelper.findCell(clientIterator, "20CR22");
        assertThat(cell_20CR22.getColumnIndex(), LINK_COL_INDEX);
        assertThat(columnReaderHelper.findCellFrom(cell_20CR22, clientIterator, "Ремонт Бензобака").getColumnIndex(), DESC_COL_INDEX);
    }

    @Test
    @Ignore
    public void printResultFile() throws IOException, InvalidFormatException {
        final Workbook workbookForVerification = WorkbookFactory.create(LINKED_RESULT_PATH.toFile());
        final Sheet verifyClientSheet = workbookForVerification.getSheetAt(0);
        final Iterator<Row> clientIterator = verifyClientSheet.rowIterator();
        while (clientIterator.hasNext()) {
            Row row = clientIterator.next();
            Cell cell = row.getCell(6);
            if (cell != null) {
                Hyperlink hyperlink = cell.getHyperlink();
                if (hyperlink != null)
                    System.out.println(hyperlink.getAddress());
            }
        }
    }

}
