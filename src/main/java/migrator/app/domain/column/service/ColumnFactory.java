package migrator.app.domain.column.service;

import migrator.app.domain.table.model.Column;
import migrator.app.migration.model.ChangeCommand;
import migrator.app.migration.model.ColumnProperty;
import migrator.app.migration.model.SimpleColumnProperty;

public class ColumnFactory {
    protected ColumnProperty simpleColumnProperty(String name, String format, String defaultValue, Boolean enableNull) {
        return new SimpleColumnProperty(name, format, defaultValue, enableNull);
    }

    public Column createNotChanged(String columnName, String format, String defaultValue, Boolean enableNull) {
        return new Column(
            new SimpleColumnProperty(columnName, format, defaultValue, enableNull), // original
            new SimpleColumnProperty(columnName, format, defaultValue, enableNull), // changed
            new ChangeCommand(ChangeCommand.NONE)
        );
    }

    public Column createWithCreateChange(String columnName) {
        return new Column(
            new SimpleColumnProperty(columnName, null, null, null), // original
            new SimpleColumnProperty(columnName, "string", "", false), // changed
            new ChangeCommand(ChangeCommand.CREATE)
        );
    }
}