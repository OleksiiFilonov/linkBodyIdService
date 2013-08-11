package oleksii.filonov.table.listeners;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import oleksii.filonov.gui.MainTable;
import oleksii.filonov.model.MainTableModel;
import oleksii.filonov.model.Record;
import oleksii.filonov.table.listeners.PasteListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PasteListenerTest {

    private static final int FIRST_ROW = 0;
    private static final int SECOND_ROW = 1;
    private static final int FIRST_COLUMN = 0;
    private static final int SECOND_COLUMN = 1;
    private static final String ONE_LINE_BUFFER = "KH0000000002";
    private static final String BODY_ID_FIRST_LINE = "KH0000000003";
    private static final String BODY_ID_SECOND_LINE = "KH0000000004";
    private static final String THREE_LINE_BUFFER = "\n" + BODY_ID_FIRST_LINE + "\n\n" + BODY_ID_SECOND_LINE;
    private static final String INITIAL_BODY_ID = "KH0000000001";

    private PasteListener pasteListener;

    private final MainTableModel tableModel = new MainTableModel();
    @Spy
    private final MainTable table = new MainTable(this.tableModel);

    private final Record initialRecord = new Record(INITIAL_BODY_ID);
    @Mock
    private ActionEvent pasteEvent;

    private Clipboard clipboard;

    @Before
    public void setUp() {
        this.tableModel.getRecords().clear();
        this.tableModel.getRecords().add(this.initialRecord);
        this.pasteListener = new PasteListener(this.table);
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Test
    public void InsertOneStringInBuffer_ThenValueOfSelectedBodyIdChanged() {
        insertOneLineIntoClipboard();
        selectFirstTopCell();
        this.pasteListener.actionPerformed(this.pasteEvent);
        final String bodyId = this.table.getValueAt(FIRST_ROW, FIRST_COLUMN);
        assertThat(bodyId, equalTo(ONE_LINE_BUFFER));
    }

    @Test
    public void insertOneStringAndSelecteCellIsNotBodyId_ThenNothingChanged() {
        insertOneLineIntoClipboard();
        when(this.table.getSelectedColumn()).thenReturn(SECOND_COLUMN);
        when(this.table.getSelectedRow()).thenReturn(FIRST_ROW);
        final int initRowCount = this.table.getRowCount();

        this.pasteListener.actionPerformed(this.pasteEvent);
        assertThat("The row count should remain the same", this.table.getRowCount(), equalTo(initRowCount));
        final String bodyId = this.table.getValueAt(FIRST_ROW, FIRST_COLUMN);
        assertThat(bodyId, equalTo(INITIAL_BODY_ID));
    }

    @Test
    public void Insert3StringsWithEmptyLine_ThenSelectedCellReplacedAnd1RowAddedAfterCurrentlySelectedRow() {
        final StringSelection clipbordString = new StringSelection(THREE_LINE_BUFFER);
        this.clipboard.setContents(clipbordString, clipbordString);
        this.tableModel.getRecords().add(this.initialRecord);
        selectFirstTopCell();
        final int initRowCount = this.table.getRowCount();

        this.pasteListener.actionPerformed(this.pasteEvent);
        assertThat("The row hasn't been inserted in the table", this.table.getRowCount(), equalTo(initRowCount + 1));
        final String firstInsertedBodyId = this.table.getValueAt(FIRST_ROW, FIRST_COLUMN);
        assertThat(firstInsertedBodyId, equalTo(BODY_ID_FIRST_LINE));
        final String secondInsertedBodyId = this.table.getValueAt(SECOND_ROW, FIRST_COLUMN);
        assertThat(secondInsertedBodyId, equalTo(BODY_ID_SECOND_LINE));
    }

    private void selectFirstTopCell() {
        when(this.table.getSelectedColumn()).thenReturn(FIRST_COLUMN);
        when(this.table.getSelectedRow()).thenReturn(FIRST_ROW);
    }

    private void insertOneLineIntoClipboard() {
        final StringSelection clipbordString = new StringSelection(ONE_LINE_BUFFER);
        this.clipboard.setContents(clipbordString, clipbordString);
    }
}