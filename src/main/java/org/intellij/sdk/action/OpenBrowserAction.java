package org.intellij.sdk.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class OpenBrowserAction extends AnAction implements DumbAware {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        OpenBrowserDialog dialog = new OpenBrowserDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setEnabledAndVisible(true);
    }
}
