package migrator.ext.javafx.table.component;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import migrator.app.Container;
import migrator.app.Gui;
import migrator.app.breadcrumps.BreadcrumpsComponent;
import migrator.app.domain.project.service.ProjectService;
import migrator.app.domain.table.component.TableList;
import migrator.app.domain.table.model.Table;
import migrator.app.domain.table.service.TableGuiKit;
import migrator.app.domain.table.service.TableActiveState;
import migrator.app.domain.table.service.TableFactory;
import migrator.app.router.ActiveRoute;
import migrator.ext.javafx.component.card.CardListComponent;
import migrator.ext.javafx.component.card.withmarks.CardWithMarksComponentFactory;
import migrator.ext.javafx.component.SearchComponent;
import migrator.ext.javafx.component.ViewComponent;
import migrator.ext.javafx.component.ViewLoader;
import migrator.lib.emitter.Subscription;
import migrator.lib.hotkyes.Hotkey;

public class JavafxTableList extends ViewComponent implements TableList {
    protected TableFactory tableFactory;
    protected TableActiveState tableActiveState;
    protected ProjectService projectService;
    protected TableGuiKit guiKit;
    protected BreadcrumpsComponent breadcrumpsComponent;
    protected ActiveRoute activeRoute;
    protected CardListComponent<Table> cardListComponent;
    protected SearchComponent searchComponent;

    @FXML protected FlowPane tables;
    @FXML protected VBox breadcrumpsContainer;
    @FXML protected VBox tableCards;
    @FXML protected VBox searchBox;

    public JavafxTableList(ViewLoader viewLoader, Container container, Gui gui) {
        super(viewLoader);
        this.activeRoute = container.getActiveRoute();
        this.tableFactory = container.getTableFactory();
        this.tableActiveState = container.getTableActiveState();
        this.projectService = container.getProjectService();
        this.guiKit = gui.getTableKit();

        this.breadcrumpsComponent = gui.getBreadcrumps().createBreadcrumps(
            this.projectService.getOpened().get()
        );

        this.cardListComponent = new CardListComponent<>(
            this.tableActiveState.getList(),
            new TableCardFactory(),
            new CardWithMarksComponentFactory(viewLoader),
            viewLoader
        );

        this.cardListComponent.onPrimary((Table eventTable) -> {
            this.tableActiveState.activate(eventTable);
        });

        this.searchComponent = new SearchComponent(viewLoader, this.tableActiveState.searchProperty());

        Subscription<Hotkey> subscription = container.getHotkeyService().on("find", (hotkey) -> {
            this.searchBox.getChildren().add(
                (Node) this.searchComponent.getContent()
            );
            this.searchComponent.focus();
        });
        this.subscriptions.add(subscription);

        subscription = container.getHotkeyService().on("cancel", (hotkey) -> {
            this.searchComponent.clear();
            this.searchBox.getChildren().clear();
            System.out.println("close find");
        });
        this.subscriptions.add(subscription);

        this.loadView("/layout/table/index.fxml");
    }

    @FXML public void initialize() {
        this.tableCards.getChildren()
            .setAll(
                (Node) this.cardListComponent.getContent()
            );

        this.breadcrumpsContainer.getChildren()
            .setAll(
                (Node) this.breadcrumpsComponent.getContent()
            );
    }

    @Override
    @FXML public void addTable() {
        Table newTable = this.tableFactory.createWithCreateChange(this.projectService.getOpened().get(), "new_table");
        this.tableActiveState.addAndActivate(newTable);
    }

    @Override
    @FXML public void commit() {
        this.activeRoute.changeTo("commit.view", this.projectService.getOpened().get());
    }
}