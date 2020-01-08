package migrator.app;

import java.util.Collection;

import migrator.app.code.CodeConfig;
import migrator.app.code.CodeManager;
import migrator.app.database.DatabaseContainer;
import migrator.app.domain.column.ColumnRepository;
import migrator.app.domain.column.service.ColumnActiveState;
import migrator.app.domain.column.service.ColumnFactory;
import migrator.app.domain.column.service.ColumnService;
import migrator.app.domain.connection.service.ConnectionFactory;
import migrator.app.domain.connection.service.ConnectionService;
import migrator.app.domain.database.service.DatabaseFactory;
import migrator.app.domain.database.service.DatabaseService;
import migrator.app.domain.index.IndexRepository;
import migrator.app.domain.index.service.IndexActiveState;
import migrator.app.domain.index.service.IndexFactory;
import migrator.app.domain.index.service.IndexService;
import migrator.app.domain.project.service.ProjectFactory;
import migrator.app.domain.project.service.ProjectService;
import migrator.app.domain.table.service.TableFactory;
import migrator.app.domain.table.service.TableService;
import migrator.app.gui.GuiContainer;
import migrator.app.domain.table.TableRepository;
import migrator.app.domain.table.model.Column;
import migrator.app.domain.table.model.Index;
import migrator.app.domain.table.model.Table;
import migrator.app.domain.table.service.TableActiveState;
import migrator.app.migration.Migration;
import migrator.app.migration.MigrationConfig;
import migrator.app.migration.model.ChangeCommand;
import migrator.app.migration.model.ColumnProperty;
import migrator.app.migration.model.IndexProperty;
import migrator.app.migration.model.TableProperty;
import migrator.app.router.ActiveRoute;
import migrator.app.toast.ToastService;
import migrator.lib.config.ValueConfig;
import migrator.lib.hotkyes.HotkeyFactory;
import migrator.lib.hotkyes.HotkeysService;
import migrator.lib.logger.Logger;
import migrator.lib.repository.UniqueRepository;
import migrator.lib.storage.Storage;

public class ConfigContainer {
    private MigrationConfig migrationConfig;
    private CodeConfig codeConfig;

    private ValueConfig<ActiveRoute> activeRoute;
    private ValueConfig<Migration> migration;
    private ValueConfig<CodeManager> codeManager;
    private ValueConfig<Logger> logger;
    private ValueConfig<HotkeyFactory> hotkeyFactory;
    private ValueConfig<HotkeysService> hotkeyService;
    private ValueConfig<ToastService> toastService;
    private ValueConfig<GuiContainer> guiContainer;
    private ValueConfig<DatabaseContainer> databaseContainer;

    private ValueConfig<ConnectionFactory> connectionFactory;
    private ValueConfig<DatabaseFactory> databaseFactory;
    private ValueConfig<ConnectionService> connectionService;
    private ValueConfig<DatabaseService> databaseService;

    private ValueConfig<UniqueRepository<ChangeCommand>> changeCommandRepository;
    private ValueConfig<Storage<Collection<ChangeCommand>>> changeCommandStorage;

    private ValueConfig<ProjectFactory> projectFactory;
    private ValueConfig<ProjectService> projectService;

    private ValueConfig<TableFactory> tableFactory;
    private ValueConfig<TableService> tableService;
    private ValueConfig<TableActiveState> tableActiveState;
    private ValueConfig<TableRepository> tableRepository;
    private ValueConfig<UniqueRepository<TableProperty>> tablePropertyRepository;
    private ValueConfig<Storage<Collection<Table>>> tableStorage;
    private ValueConfig<Storage<Collection<TableProperty>>> tablePropertyStorage;

    private ValueConfig<ColumnFactory> columnFactory;
    private ValueConfig<ColumnRepository> columnRepository;
    private ValueConfig<ColumnActiveState> columnActiveState;
    private ValueConfig<UniqueRepository<ColumnProperty>> columnPropertyRepository;
    private ValueConfig<ColumnService> columnService;
    private ValueConfig<Storage<Collection<ColumnProperty>>> columnPropertyStorage;
    private ValueConfig<Storage<Collection<Column>>> columnStorage;
    
    private ValueConfig<IndexFactory> indexFactory;
    private ValueConfig<IndexService> indexService;
    private ValueConfig<IndexActiveState> indexActiveState;
    private ValueConfig<IndexRepository> indexRepository;
    private ValueConfig<UniqueRepository<IndexProperty>> indexPropertyRepository;
    private ValueConfig<Storage<Collection<Index>>> indexStorage;
    private ValueConfig<Storage<Collection<IndexProperty>>> indexPropertyStorage;

    public ConfigContainer() {
        this.migrationConfig = new MigrationConfig();
        this.codeConfig = new CodeConfig();
        this.logger = new ValueConfig<>();
        this.hotkeyFactory = new ValueConfig<>();
        this.hotkeyService = new ValueConfig<>();
        this.guiContainer = new ValueConfig<>();
        this.databaseContainer = new ValueConfig<>();

        this.activeRoute = new ValueConfig<>();
        this.migration = new ValueConfig<>();
        this.codeManager = new ValueConfig<>();

        this.connectionFactory = new ValueConfig<>();
        this.databaseFactory = new ValueConfig<>();
        this.connectionService = new ValueConfig<>();
        this.databaseService = new ValueConfig<>();        
        this.toastService = new ValueConfig<>();

        this.changeCommandRepository = new ValueConfig<>();
        this.changeCommandStorage = new ValueConfig<>();

        this.projectFactory = new ValueConfig<>();
        this.projectService = new ValueConfig<>();
        
        this.tableFactory = new ValueConfig<>();
        this.tableActiveState = new ValueConfig<>();
        this.tableRepository = new ValueConfig<>();
        this.tablePropertyRepository = new ValueConfig<>();
        this.tableService = new ValueConfig<>();
        this.tableStorage = new ValueConfig<>();
        this.tablePropertyStorage = new ValueConfig<>();

        this.columnFactory = new ValueConfig<>();
        this.columnService = new ValueConfig<>();
        this.columnActiveState = new ValueConfig<>();
        this.columnRepository = new ValueConfig<>();
        this.columnPropertyRepository = new ValueConfig<>();
        this.columnStorage = new ValueConfig<>();
        this.columnPropertyStorage = new ValueConfig<>();

        this.indexFactory = new ValueConfig<>();
        this.indexService = new ValueConfig<>();
        this.indexActiveState = new ValueConfig<>();
        this.indexRepository = new ValueConfig<>();
        this.indexPropertyRepository = new ValueConfig<>();
        this.indexStorage = new ValueConfig<>();
        this.indexPropertyStorage = new ValueConfig<>();
    }

    public ValueConfig<HotkeyFactory> getHoteyFactory() {
        return this.hotkeyFactory;
    }

    public ValueConfig<HotkeysService> getHoteyService() {
        return this.hotkeyService;
    }

    public MigrationConfig getMigrationConfig() {
        return this.migrationConfig;
    }

    public CodeConfig getCodeConfig() {
        return this.codeConfig;
    }

    public ValueConfig<ActiveRoute> activeRouteConfig() {
        return this.activeRoute;
    }

    public ValueConfig<Migration> migrationConfig() {
        return this.migration;
    }

    public ValueConfig<Logger> loggerConfig() {
        return this.logger;
    }

    public ValueConfig<CodeManager> codeManagerConfig() {
        return this.codeManager;
    }

    public ValueConfig<ConnectionFactory> connectionFactoryConfig() {
        return this.connectionFactory;
    }

    public ValueConfig<DatabaseFactory> databaseFactoryConfig() {
        return this.databaseFactory;
    }

    public ValueConfig<TableFactory> tableFactoryConfig() {
        return this.tableFactory;
    }

    public ValueConfig<ColumnFactory> columnFactoryConfig() {
        return this.columnFactory;
    }

    public ValueConfig<IndexFactory> indexFactoryConfig() {
        return this.indexFactory;
    }

    public ValueConfig<ProjectFactory> projectFactoryConfig() {
        return this.projectFactory;
    }

    public ValueConfig<ConnectionService> connectionServiceConfig() {
        return this.connectionService;
    }

    public ValueConfig<DatabaseService> databaseServiceConfig() {
        return this.databaseService;
    }

    public ValueConfig<TableService> tableServiceConfig() {
        return this.tableService;
    }

    public ValueConfig<ColumnService> columnServiceConfig() {
        return this.columnService;
    }

    public ValueConfig<IndexService> indexServiceConfig() {
        return this.indexService;
    }

    public ValueConfig<ProjectService> projectServiceConfig() {
        return this.projectService;
    }

    public ValueConfig<ToastService> toastServiceConfig() {
        return this.toastService;
    }

    public ValueConfig<ColumnActiveState> columnActiveStateConfig() {
        return this.columnActiveState;
    }

    public ValueConfig<IndexActiveState> indexActiveStateConfig() {
        return this.indexActiveState;
    }

    public ValueConfig<TableActiveState> tableActiveStateConfig() {
        return this.tableActiveState;
    }

    public ValueConfig<TableRepository> tableRepositoryConfig() {
        return this.tableRepository;
    }

    public ValueConfig<ColumnRepository> columnRepositoryConfig() {
        return this.columnRepository;
    }

    public ValueConfig<IndexRepository> indexRepositoryConfig() {
        return this.indexRepository;
    }

    public ValueConfig<UniqueRepository<IndexProperty>> indexPropertyRepositoryConfig() {
        return this.indexPropertyRepository;
    }

    public ValueConfig<Storage<Collection<IndexProperty>>> indexPropertyStorageConfig() {
        return this.indexPropertyStorage;
    }

    public ValueConfig<Storage<Collection<Index>>> indexStorageConfig() {
        return this.indexStorage;
    }

    public ValueConfig<Storage<Collection<ChangeCommand>>> changeCommandStorageConfig() {
        return this.changeCommandStorage;
    }

    public ValueConfig<UniqueRepository<ChangeCommand>> changeCommandRepositoryConfig() {
        return this.changeCommandRepository;
    }

    public ValueConfig<UniqueRepository<ColumnProperty>> columnPropertyRepositoryConfig() {
        return this.columnPropertyRepository;
    }

    public ValueConfig<UniqueRepository<TableProperty>> tablePropertyRepositoryConfig() {
        return this.tablePropertyRepository;
    }

    public ValueConfig<Storage<Collection<Column>>> columnStorageConfig() {
        return this.columnStorage;
    }

    public ValueConfig<Storage<Collection<ColumnProperty>>> columnPropertyStorageConfig() {
        return this.columnPropertyStorage;
    }

    public ValueConfig<Storage<Collection<TableProperty>>> tablePropertyStorageConfig() {
        return this.tablePropertyStorage;
    }

    public ValueConfig<Storage<Collection<Table>>> tableStorageConfig() {
        return this.tableStorage;
    }

    public ValueConfig<GuiContainer> guiContainerConfig() {
        return this.guiContainer;
    }

    public ValueConfig<DatabaseContainer> databaseContainerConfig() {
        return this.databaseContainer;
    }
}