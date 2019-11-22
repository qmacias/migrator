package migrator.ext.phinx;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import migrator.app.database.format.ColumnFormatManager;
import migrator.app.database.format.FakeColumnFormatManager;
import migrator.app.database.format.LengthColumnFormatter;
import migrator.app.database.format.PrecisionColumnFormatter;
import migrator.app.database.format.SimpleColumnFormat;
import migrator.app.domain.table.model.Column;
import migrator.app.domain.table.model.Index;
import migrator.app.domain.table.model.Table;
import migrator.app.migration.model.ChangeCommand;
import migrator.app.migration.model.ColumnProperty;
import migrator.app.migration.model.SimpleColumnProperty;
import migrator.app.migration.model.SimpleIndexProperty;
import migrator.app.migration.model.SimpleTableProperty;
import migrator.app.migration.model.TableChange;
import migrator.ext.phinx.PhinxMigrationGenerator;
import migrator.ext.phinx.mock.FileStorage;
import migrator.ext.phinx.mock.FakeTimestampFileStorageFactory;
import migrator.ext.php.PhpCommandFactory;
import migrator.lib.stringformatter.PascalCaseFormatter;

public class PhinxMigrationGeneratorTest {
    protected PhinxMigrationGenerator migrator;
    protected FileStorage storage;
    protected ColumnFormatManager columnFormatManager;

    @BeforeEach
    public void setUp() {
        this.columnFormatManager = new FakeColumnFormatManager(
            new SimpleColumnFormat("test")
        );
        this.storage = new FileStorage();
        this.migrator = new PhinxMigrationGenerator(
            new FakeTimestampFileStorageFactory(this.storage),
            new PhpCommandFactory(),
            new PascalCaseFormatter()
        );
    }

    @Test public void testPhpMigrationCreateTableWithColumn() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", "create"),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        this.columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "string", null, false, "255", false, "", false),
                        new SimpleColumnProperty("2", "column_name", "string", null, false, "255", false, "", false),
                        new ChangeCommand("2", "create")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->addColumn('column_name', 'string', ['null' => false])\n" +
                        "\t\t\t->create();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationRenameTable() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "new_table_name"),
            new ChangeCommand("1", "update"),
            FXCollections.observableArrayList(),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->renameTable('new_table_name')\n" +
                        "\t\t\t->update();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationUpdateTableAddColumn() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        this.columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "column_format", null, false, "255", false, "", false),
                        new SimpleColumnProperty("2", "column_name", "column_format", null, false, "255", false, "", false),
                        new ChangeCommand("2", "create")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->addColumn('column_name', 'column_format', ['null' => false])\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationUpdateTableRemoveColumn() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        this.columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "column_format", null, false, "255", false, "", false),
                        new SimpleColumnProperty("1", "column_name", "column_format", null, false, "255", false, "", false),
                        new ChangeCommand("2", "delete")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" + 
                        "\t\t\t->removeColumn('column_name')\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationUpdateTableRenameColumn() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        this.columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "column_format", "", false, "255", false, "", false),
                        new SimpleColumnProperty("2", "new_column_name", "column_format", "", false, "255", false, "", false),
                        new ChangeCommand("2", "update")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->renameColumn('column_name', 'new_column_name')\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationDeleteTable() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", "delete"),
            FXCollections.observableArrayList(),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->dropTable('table_name');\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationCreateTableWithColumnAndIndex() {
        ColumnProperty columnChange1 = new SimpleColumnProperty("2", "id", "integer", null, false, "11", true, "", false);
        ColumnProperty columnChange2 = new SimpleColumnProperty("4", "name", "string", null, false, "255", false, "", false);
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", "create"),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        this.columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "id", "integer", null, false, "11", true, "", false),
                        columnChange1,
                        new ChangeCommand("2", "create")
                    ),
                    new Column(
                        this.columnFormatManager,
                        "2",
                        "1",
                        new SimpleColumnProperty("3", "name", "string", null, false, "255", false, "", false),
                        columnChange2,
                        new ChangeCommand("3", "create")
                    )
                )
            ),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Index(
                        "1",
                        "1",
                        new SimpleIndexProperty("1", "id_name", Arrays.asList()),
                        new SimpleIndexProperty("2", "id_name", Arrays.asList(columnChange1, columnChange2)),
                        new ChangeCommand("4", "create")
                    )
                )
            )
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->addColumn('id', 'integer', ['null' => false])\n" +
                        "\t\t\t->addColumn('name', 'string', ['null' => false])\n" +
                        "\t\t\t->addIndex(['id', 'name'], ['name' => 'id_name'])\n" +
                        "\t\t\t->create();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationRemoveIndex() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Index(
                        "1",
                        "1",
                        new SimpleIndexProperty("1", "id_name", Arrays.asList()),
                        new SimpleIndexProperty("2", "id_name", Arrays.asList()),
                        new ChangeCommand("2", "delete")
                    )
                )
            )
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->removeIndexByName('id_name')\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationNoChangeRetunrnsEmptyCommand() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertNull(this.storage.load());
    }

    @Test public void testPhpMigrationGenerateChangedColumnFormat() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        this.columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "column_format", null, false, "255", false, "", false),
                        new SimpleColumnProperty("2", "column_name", "string", null, false, "255", false, "", false),
                        new ChangeCommand("2", "update")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->changeColumn('column_name', 'string', ['null' => false])\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationGenerateChangedColumnDefaultValue() {
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        this.columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "string", "", false, "255", false, "", false),
                        new SimpleColumnProperty("2", "column_name", "string", "default_value", false, "255", false, "", false),
                        new ChangeCommand("2", "update")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->changeColumn('column_name', 'string', ['null' => false, 'default' => 'default_value'])\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationGenerateChangedColumnFormatLength() {
        ColumnFormatManager columnFormatManager = new FakeColumnFormatManager(
            new SimpleColumnFormat("string", true, false, false, false , new LengthColumnFormatter("string"))
        );
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "string", "", false, "255", false, "", false),
                        new SimpleColumnProperty("2", "column_name", "string", "", false, "250", false, "", false),
                        new ChangeCommand("2", "update")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->changeColumn('column_name', 'string', ['null' => false, 'default' => '', 'length' => 250])\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationGenerateChangedColumnFormatPrecision() {
        ColumnFormatManager columnFormatManager = new FakeColumnFormatManager(
            new SimpleColumnFormat("string", true, false, true, false, new PrecisionColumnFormatter("string"))
        );
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "string", "", false, "10", false, "4", false),
                        new SimpleColumnProperty("2", "column_name", "string", "", false, "10", false, "5", false),
                        new ChangeCommand("2", "update")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->changeColumn('column_name', 'string', ['null' => false, 'default' => '', 'precision' => 10, 'scale' => 5])\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationGenerateChangedColumnSigned() {
        ColumnFormatManager columnFormatManager = new FakeColumnFormatManager(
            new SimpleColumnFormat("string", true, true, false, false, new LengthColumnFormatter("string"))
        );
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "string", "", false, "10", true, "", false),
                        new SimpleColumnProperty("2", "column_name", "string", "", false, "10", false, "", false),
                        new ChangeCommand("2", "update")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->changeColumn('column_name', 'string', ['null' => false, 'default' => '', 'length' => 10, 'signed' => false])\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }

    @Test public void testPhpMigrationGenerateChangedColumnAutoIncrement() {
        ColumnFormatManager columnFormatManager = new FakeColumnFormatManager(
            new SimpleColumnFormat("integer", true, true, false, true, new LengthColumnFormatter("integer"))
        );
        TableChange change = new Table(
            "id-1",
            "1",
            new SimpleTableProperty("1", "table_name"),
            new SimpleTableProperty("2", "table_name"),
            new ChangeCommand("1", ""),
            FXCollections.observableArrayList(
                Arrays.asList(
                    new Column(
                        columnFormatManager,
                        "1",
                        "1",
                        new SimpleColumnProperty("1", "column_name", "integer", "", false, "10", false, "", false),
                        new SimpleColumnProperty("2", "column_name", "integer", "", false, "10", false, "", true),
                        new ChangeCommand("2", "update")
                    )
                )
            ),
            FXCollections.observableArrayList()
        );
        this.migrator.generateMigration("", "MigrationByMigrator", change);
        assertEquals(
            "<?php\n\n" +
            "use Phinx\\Migration\\AbstractMigration;\n\n" +
            "class MigrationByMigrator extends AbstractMigration\n" +
            "{\n" +
                "\tpublic function change()\n" +
                "\t{\n" +
                    "\t\t$this->table('table_name')\n" +
                        "\t\t\t->changeColumn('column_name', 'integer', ['null' => false, 'default' => '', 'length' => 10, 'signed' => false, 'identity' => true])\n" +
                        "\t\t\t->save();\n" +
                "\t}\n" +
            "}\n",
            this.storage.load()
        );
    }
}
