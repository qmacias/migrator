package migrator.app;

import migrator.app.domain.project.service.ProjectGuiKit;
import migrator.app.domain.table.service.TableGuiKit;
import migrator.app.loading.LoadingIndicator;

public interface Gui {
    public TableGuiKit getTableKit();
    public ProjectGuiKit getProject();
    public LoadingIndicator getLoadingIndicator();
}