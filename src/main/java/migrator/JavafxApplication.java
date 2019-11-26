package migrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import migrator.app.Bootstrap;
import migrator.app.Gui;
import migrator.app.Router;
import migrator.app.domain.project.model.Project;
import migrator.ext.flyway.FlywayExtension;
import migrator.ext.javafx.JavafxGui;
import migrator.ext.javafx.MainController;
import migrator.ext.javafx.component.JavafxLayout;
import migrator.ext.javafx.component.ViewLoader;
import migrator.ext.javafx.project.route.CommitViewRoute;
import migrator.ext.javafx.project.route.ProjectIndexRoute;
import migrator.ext.javafx.project.route.ProjectViewRoute;
import migrator.ext.javafx.table.route.ColumnViewRoute;
import migrator.ext.javafx.table.route.IndexViewRoute;
import migrator.ext.javafx.table.route.TableIndexRoute;
import migrator.ext.javafx.table.route.TableViewRoute;
import migrator.ext.mariadb.MariadbExtension;
import migrator.ext.mysql.MysqlExtension;
import migrator.ext.phinx.PhinxExtension;
import migrator.ext.php.PhpExtension;
import migrator.ext.postgresql.PostgresqlExtension;
import migrator.ext.sentry.SentryExtension;
import migrator.ext.sql.SqlExtension;
import migrator.lib.persistance.ListPersistance;
import migrator.lib.persistance.Persistance;
import migrator.app.Container;
import migrator.app.EnviromentConfig;

public class JavafxApplication extends Application {
    protected Persistance<List<Project>> projectsPersistance;
    protected Container container;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.projectsPersistance = new ListPersistance<>("project.list");

        String env = "production";
        if (System.getenv("MIGRATOR_ENV") != null) {
            env = System.getenv("MIGRATOR_ENV");
        }
        EnviromentConfig enviromentConfig = new EnviromentConfig(env);

        Bootstrap bootstrap = new Bootstrap(
            Arrays.asList(
                new PhinxExtension(),
                new FlywayExtension(),
                new MysqlExtension(),
                new MariadbExtension(),
                new PostgresqlExtension(),
                new PhpExtension(),
                new SqlExtension(),
                new SentryExtension(enviromentConfig.getProperties("sentry"))
            )
        );
        this.container = bootstrap.getContainer();
        this.container.getTableService().start();
        this.container.getColumnService().start();
        this.container.getIndexService().start();

        // Seed data from persistance
        this.seed();

        ViewLoader viewLoader = new ViewLoader();
        Gui gui = new JavafxGui(this.container, viewLoader, primaryStage);
        MainController mainController = new MainController(viewLoader, this.container);

        JavafxLayout layout = new JavafxLayout(mainController.getBodyPane(), mainController.getSidePane());

        Router router = new Router(this.container.getActiveRoute());
        router.connect(
            "table.index",
            new TableIndexRoute(gui.getTableKit(), layout)
        );
        router.connect(
            "table.view",
            new TableViewRoute(gui.getTableKit(), layout)
        );
        router.connect(
            "column.view",
            new ColumnViewRoute(gui.getTableKit(), layout)
        );
        router.connect(
            "index.view",
            new IndexViewRoute(gui.getTableKit(), layout)
        );
        router.connect(
            "commit.view",
            new CommitViewRoute(gui.getProject(), layout)
        );
        router.connect(
            "project.index",
            new ProjectIndexRoute(
                layout,
                gui.getProject(),
                container.getProjectService(),
                container.getActiveRoute()
            )
        );
        router.connect(
            "project.view",
            new ProjectViewRoute(layout, gui.getProject())
        );

        container.getActiveRoute().changeTo("project.index");

        Scene scene = new Scene((Pane) mainController.getContent(), 1280, 720);
        scene.getStylesheets().addAll(
            getClass().getResource("/styles/layout.css").toExternalForm(),
            getClass().getResource("/styles/text.css").toExternalForm(),
            getClass().getResource("/styles/button.css").toExternalForm(),
            getClass().getResource("/styles/table.css").toExternalForm(),
            getClass().getResource("/styles/card.css").toExternalForm(),
            getClass().getResource("/styles/toast.css").toExternalForm(),
            getClass().getResource("/styles/form.css").toExternalForm(),
            getClass().getResource("/styles/scroll.css").toExternalForm(),
            getClass().getResource("/styles/main.css").toExternalForm()
        );
        container.getHotkeyService().connectKeyboard("find", "CTRL+F");
        container.getHotkeyService().connectKeyboard("cancel", "ESCAPE");
        scene.getRoot().setOnKeyPressed((e) -> {
            this.container.getHotkeyService()
                .keyPressed(
                    container.getHotkeyFactory().create(e.getCode().getCode(), e.isControlDown(), e.isShiftDown())
                );
        });
        
        primaryStage.setTitle("Migrator");
        primaryStage.getIcons().add(
            new Image(getClass().getResourceAsStream("/images/logo.png"))
        );
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    protected void seed() {
        this.container.getChangeCommandRepository().addAll(
            this.container.getChangeCommandStorage().load()
        );
        this.container.getColumnPropertyRepository().addAll(
            this.container.getColumnPropertyStorage().load()
        );
        this.container.getIndexPropertyRepository().addAll(
            this.container.getIndexPropertyStorage().load()
        );
        this.container.getTablePropertyRepository().addAll(
            this.container.getTablePropertyStorage().load()
        );
        this.container.getColumnRepository().addAll(
            this.container.getColumnStorage().load()
        );
        this.container.getIndexRepository().addAll(
            this.container.getIndexStorage().load()
        );
        this.container.getTableRepository().addAll(
            this.container.getTableStorage().load()
        );

        this.container.getProjectService().getList()
            .setAll(
                this.projectsPersistance.load(new ArrayList<>())
            );
    }

    @Override
    public void stop() throws Exception {
        this.container.getChangeCommandStorage().store(
            this.container.getChangeCommandRepository().getAll()
        );
        this.container.getColumnPropertyStorage().store(
            this.container.getColumnPropertyRepository().getAll()
        );
        this.container.getIndexPropertyStorage().store(
            this.container.getIndexPropertyRepository().getAll()
        );
        this.container.getTablePropertyStorage().store(
            this.container.getTablePropertyRepository().getAll()
        );
        this.container.getColumnStorage().store(
            this.container.getColumnRepository().getAll()
        );
        this.container.getIndexStorage().store(
            this.container.getIndexRepository().getAll()
        );
        this.container.getTableStorage().store(
            this.container.getTableRepository().getAll()
        );

        this.projectsPersistance.store(
            this.container.getProjectService().getList()
        );
        
        super.stop();
    }

    public Container getContainer() {
        return this.container;
    }
}
