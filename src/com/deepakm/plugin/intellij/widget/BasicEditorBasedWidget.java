package com.deepakm.plugin.intellij.widget;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.CalledInAwt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

/**
 * Created by dmarathe on 2/2/16.
 */
public class BasicEditorBasedWidget extends EditorBasedWidget implements StatusBarWidget.TextPresentation {

    protected static final Logger LOGGER = Logger.getInstance(BasicEditorBasedWidget.class);
    private static final String PREFIX = "Open Files : ";
    int fileCounter;

    public BasicEditorBasedWidget(@NotNull Project project) {
        super(project);
        this.fileCounter =  FileEditorManager.getInstance(project).getAllEditors().length;
    }

    public void activate() {
        Project project = getProject();

        if (project != null) {
            installWidgetToStatusBar(project, this);
        }
    }

    public void deactivate() {
        Project project = getProject();
        if (project != null) {
            removeWidgetFromStatusBar(project, this);
        }
    }


    @NotNull
    @Override
    public String ID() {
        return BasicEditorBasedWidget.class.getName();
    }

    @Nullable
    @Override
    public WidgetPresentation getPresentation(@NotNull PlatformType type) {
        return this;
    }


    @Override
    public void dispose() {
        deactivate();
        super.dispose();
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        super.fileOpened(source, file);
        fileCounter += 1;
        update();
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        super.fileClosed(source, file);
        fileCounter -= 1;
        update();
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        super.selectionChanged(event);
    }

    @NotNull
    @Override
    public String getText() {
        return PREFIX + fileCounter;
    }

    @NotNull
    @Override
    public String getMaxPossibleText() {
        return PREFIX + "100";
    }

    @Override
    public float getAlignment() {
        return 0;
    }

    @Nullable
    @Override
    public String getTooltipText() {
        return getText();
    }

    @Nullable
    @Override
    public Consumer<MouseEvent> getClickConsumer() {
        System.out.println("consumer");
        return null;
    }

    @CalledInAwt
    private void update() {
        Project project = getProject();
        if (project == null || project.isDisposed()) return;
        System.out.println("Text is : " + getText());
        if (myStatusBar != null) {
            myStatusBar.updateWidget(ID());
        }
    }

    private void installWidgetToStatusBar(@NotNull final Project project, @NotNull final StatusBarWidget widget) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                if (statusBar != null && !isDisposed()) {
                    statusBar.addWidget(widget, "before " + (SystemInfo.isMac ? "Encoding" : "InsertOverwrite"), project);

                    update();
                }
            }
        });
    }

    private void removeWidgetFromStatusBar(@NotNull final Project project, @NotNull final StatusBarWidget widget) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                if (statusBar != null && !isDisposed()) {
                    statusBar.removeWidget(widget.ID());
                }
            }
        });
    }
}
