package oleksii.filonov.readers;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

public class ColumnReaderHelper {

    public Cell findCell(final Iterator<Row> rowsIterator, final String lookUpValue) {
        Cell foundCell;
        while (rowsIterator.hasNext()) {
            final Row row = rowsIterator.next();
            foundCell = lookUpCell(row, lookUpValue);
            if (foundCell != null) {
                return foundCell;
            }
        }
        throw new ReadDataException(String.format("Колонка \"%s\" не найдена", lookUpValue));
    }

    public Cell findCellFrom(final Cell startCell, final Iterator<Row> rowsIterator, final String lookUpValue) {
        final Cell foundCell = lookupInCurrentRow(startCell, lookUpValue);
        if (foundCell == null) {
            return findCell(rowsIterator, lookUpValue);
        }
        return foundCell;
    }

    private Cell lookupInCurrentRow(final Cell startCell, final String lookUpValue) {
        final Row currentRow = startCell.getRow();
        for (int i = startCell.getColumnIndex(); i < currentRow.getLastCellNum(); ++i) {
            if (equals(lookUpValue, currentRow.getCell(i))) {
                return currentRow.getCell(i);
            }
        }
        return null;
    }

    private Cell lookUpCell(final Row row, final String lookUpValue) {
        for (final Cell cell : row) {
            if (equals(lookUpValue, cell)) {
                return cell;
            }
        }
        return null;
    }

    private boolean equals(final String lookUpValue, final Cell cell) {
        return (cell.getCellType() == CELL_TYPE_STRING) &&
                lookUpValue.equalsIgnoreCase(cell.getStringCellValue().trim());
    }

    public boolean isStringType(final Cell cell) {
        return cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING;
    }

}
