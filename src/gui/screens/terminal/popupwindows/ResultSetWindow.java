package gui.screens.terminal.popupwindows;

import datastructures.relation.resultset.ResultSet;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.TableData;
import gui.screens.tables.components.tabledatawindow.TableDataWindow;
import utilities.OptimizerUtilities;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Just calls a class I already created, but modified to accept result set data. Quick patchwork.
 */
public class ResultSetWindow {
    public ResultSetWindow(ResultSet resultSet) {
        String name = "Result Set";
        List<String> columnNames = resultSet.getColumns().stream()
                .map(Column::getColumnName)
                .collect(Collectors.toList());
        columnNames = OptimizerUtilities.removePrefixedColumnNames(columnNames);
        List<List<String>> data = resultSet.getData();
        new TableDataWindow(name, columnNames, data);
    }
}