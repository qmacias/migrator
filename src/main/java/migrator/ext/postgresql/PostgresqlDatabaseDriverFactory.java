package migrator.ext.postgresql;

import migrator.app.database.driver.DatabaseDriver;
import migrator.app.database.driver.DatabaseDriverFactory;
import migrator.app.domain.column.ColumnRepository;
import migrator.app.domain.column.service.ColumnFactory;
import migrator.app.domain.index.service.IndexFactory;
import migrator.app.domain.table.service.TableFactory;
import migrator.lib.config.ValueConfig;
import migrator.lib.logger.Logger;

public class PostgresqlDatabaseDriverFactory implements DatabaseDriverFactory {
    protected TableFactory tableFactory;
    protected ColumnFactory columnFactory;
    protected IndexFactory indexFactory;
    protected ColumnRepository columnRepository;
    protected ValueConfig<Logger> loggerConfig;

    public PostgresqlDatabaseDriverFactory(TableFactory tableFactory, ColumnFactory columnFactory, IndexFactory indexFactory, ValueConfig<Logger> loggerConfig, ColumnRepository columnRepository) {
        this.tableFactory = tableFactory;
        this.columnFactory = columnFactory;
        this.indexFactory = indexFactory;
        this.columnRepository = columnRepository;
        this.loggerConfig = loggerConfig;
    }

    @Override
    public DatabaseDriver create(String url, String user, String password) {
        return new PostgresqlDatabaseDriver(
            this.tableFactory,
            this.columnFactory,
            this.indexFactory,
            this.columnRepository,
            this.loggerConfig.get(),
            url,
            user,
            password
        );
    }
}