package com.zf.androidplugin.shapedrawable.template;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by Lenovo on 2016/1/14.
 */
public class ShapeDrawableGenerateAction extends AnAction {
    private static final String SUFFIX_XML = ".xml";
    private VirtualFile[] virtualFiles = null;

    private String shapeDrawableName;
    private static final String TEMPLATE_FILE_NAME_PATH = "/shape_drawable_template.xml";

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        final AnActionEvent finalAnActionEvent = anActionEvent;
        final Project project = anActionEvent.getProject();

        if (virtualFiles.length != 1) {
            String select_one_dir = I18n.getString(ResourceI18n.SELECT_ONE_DIR);
            showErrorDialog(anActionEvent, select_one_dir);
            return;
        }

        for (VirtualFile virtualFile : virtualFiles) {
            if (!virtualFile.isDirectory()) {
                String select_dir = I18n.getString(ResourceI18n.SELECT_DIR);
                showErrorDialog(anActionEvent, select_dir);
                return;
            }
        }

        boolean isNeedInputDialog = true;
        String title = I18n.getString(ResourceI18n.SET_TITLE);
        String tipMessage = I18n.getString(ResourceI18n.PLEASE_ENTER_SHAPE_DRAWABLE_NAME);
        while (isNeedInputDialog) {
            shapeDrawableName = Messages.showInputDialog(project, tipMessage, title, Messages.getQuestionIcon());
            if (shapeDrawableName != null) {
                if ("".equals(shapeDrawableName.trim())) {
                    String message = I18n.getString(ResourceI18n.PLEASE_ENTER_FILE_NAME);
                    showErrorDialog(anActionEvent, message);
                    //判断文件名是否有效
                } else if (!FileOperation.isValidFileName(FileOperation.addSuffixXml(shapeDrawableName))) {
                    String message = I18n.getString(ResourceI18n.FILE_NAME_INVALID);
                    showErrorDialog(anActionEvent, message);
                    //判断文件夹下是否已经有该文件
                } else if (FileOperation.isFindChild(virtualFiles[0], FileOperation.addSuffixXml(shapeDrawableName))) {
                    String message = I18n.getString(ResourceI18n.FILE_ALREADY_EXISTS);
                    showErrorDialog(anActionEvent, message);
                } else {
                    isNeedInputDialog = false;
                }
            } else {
                isNeedInputDialog = false;
            }
        }
        if (shapeDrawableName == null) {
            return;
        }
        ApplicationManager.getApplication().
                runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        //创建文件
                        String fileName = FileOperation.addSuffixXml(shapeDrawableName);
                        VirtualFile xmlVirtualFile = null;
                        try {
                            if (virtualFiles[0].findChild(fileName) == null)
                                xmlVirtualFile = virtualFiles[0].createChildData(null, fileName);
                        } catch (IOException e) {
                            String message = I18n.getString(ResourceI18n.CREATE_FILE_FAILED);
                            showErrorDialog(finalAnActionEvent, message);
                            e.printStackTrace();
                            return;
                        }
                        //复制 模板文件
                        try {
                            FileOperation.copyFile(TEMPLATE_FILE_NAME_PATH, xmlVirtualFile);
                            FileOperation.openFile(project, xmlVirtualFile);
                        } catch (IOException e) {
                            String message = I18n.getString(ResourceI18n.COPY_TEMPLATE_FILE_FAILED);
                            showErrorDialog(finalAnActionEvent, message);
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void update(AnActionEvent e) {
        shapeDrawableName = null;
        virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        e.getPresentation().setEnabled(virtualFiles != null);
    }

    private void showInfoDialog(AnActionEvent e, String content) {
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        MyNotifier.notifyError(project, NotificationType.INFORMATION, content);
    }

    private void showErrorDialog(AnActionEvent e, String content) {
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        MyNotifier.notifyError(project, NotificationType.ERROR, content);
    }

//    private void showInfoDialog(String text, AnActionEvent e)
//    {
//        StatusBar statusBar = WindowManager.getInstance().getStatusBar((Project) DataKeys.PROJECT.getData(e.getDataContext()));
//
//        if (statusBar != null)
//            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.INFO, null).setFadeoutTime(10000L).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
//    }
//
//    private void showErrorDialog(String text, AnActionEvent e)
//    {
//        StatusBar statusBar = WindowManager.getInstance().getStatusBar((Project) DataKeys.PROJECT.getData(e.getDataContext()));
//
//        if (statusBar != null)
//            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.ERROR, null).setFadeoutTime(10000L).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
//    }


}
