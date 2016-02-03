package com.deepakm.plugin.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.deepakm.plugin.intellij.widget.BasicEditorBasedWidget;

/**
 * Created by dmarathe on 2/2/16.
 */
public class EditorOpenFileCounter extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        final Runnable readRunner = new Runnable() {
            @Override
            public void run() {
                StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                final BasicEditorBasedWidget widget = new BasicEditorBasedWidget(project);

                if (statusBar.getWidget(widget.ID()) == null) {
                    widget.activate();
                }
            }
        };
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        ApplicationManager.getApplication().runWriteAction(readRunner);
                    }
                }, "EditorOpenFilesCounter", null);
            }
        });
    }
}


