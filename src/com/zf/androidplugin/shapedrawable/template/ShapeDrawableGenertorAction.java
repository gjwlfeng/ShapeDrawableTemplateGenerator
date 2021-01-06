package com.zf.androidplugin.shapedrawable.template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import java.io.IOException;

/**
 * Created by Lenovo on 2016/1/14.
 */
public class ShapeDrawableGenertorAction extends AnAction
{
    private static final String SUFFIX_XML = ".xml";
    private VirtualFile[] virtualFiles = null;
    private String shapeDrawabelName;
    private static final String TEMPLATEFILENAME_PATH = "/shape_drawable_template.xml";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent)
    {
        if (anActionEvent == null)
            return;

        final AnActionEvent finalAnActionEvent = anActionEvent;
        final Project project = anActionEvent.getProject();

        if (virtualFiles.length != 1)
        {
            String select_one_dir = I18n.getString(ResourceI18n.SELECT_ONE_DIR);
            showInfoDialog(select_one_dir, anActionEvent);
        } else if (!virtualFiles[0].isDirectory())
        {
            String select_dir = I18n.getString(ResourceI18n.SELECT_DIR);
            showInfoDialog(select_dir, anActionEvent);
        } else
        {
            String title = I18n.getString(ResourceI18n.SET_TITLE);
            String message = I18n.getString(ResourceI18n.PLEASE_ENTER_SHAPEDRAWABLE_NAME);

            do
            {
                if (shapeDrawabelName == null)
                {
                } else if ("".equals(shapeDrawabelName.trim()))
                {
                    String string = I18n.getString(ResourceI18n.PLEASE_ENTER_FILE_NAME);
                    showInfoDialog(string, anActionEvent);
                } else if (!FileOperation.isValidFileName(FileOperation.addSuffixXml(shapeDrawabelName)))
                {
                    String string = I18n.getString(ResourceI18n.FILE_NAME_INVALID);
                    showInfoDialog(string, anActionEvent);
                } else if (FileOperation.isFindChild(virtualFiles[0],FileOperation.addSuffixXml(shapeDrawabelName)))
                {
                    String string = I18n.getString(ResourceI18n.FILE_ALREADY_EXISTS);
                    showInfoDialog(string, anActionEvent);
                }
                shapeDrawabelName = Messages.showInputDialog(project, message, title, Messages.getQuestionIcon());
            }
            while ((shapeDrawabelName != null) && ("".equals(shapeDrawabelName.trim()) || FileOperation.isFindChild(virtualFiles[0],FileOperation.addSuffixXml(shapeDrawabelName)) || (!FileOperation.isValidFileName(FileOperation.addSuffixXml(shapeDrawabelName)))));

            if (shapeDrawabelName == null)
                return;
            ApplicationManager.getApplication().runWriteAction(new Runnable()
            {
                @Override
                public void run()
                {
                    //创建文件
                    String fileName = FileOperation.addSuffixXml(shapeDrawabelName);
                    VirtualFile xmlVirtualFile = null;
                    try
                    {
                        if (virtualFiles[0].findChild(fileName) == null)
                            xmlVirtualFile = virtualFiles[0].createChildData(null, fileName);
                    } catch (IOException e)
                    {
                        String string = I18n.getString(ResourceI18n.CREATE_FILE_FAILED);
                        showErrorDialog(string, finalAnActionEvent);
                        e.printStackTrace();
                        return;
                    }
                    //复制 模板文件
                    try
                    {
                        FileOperation.copyFile(TEMPLATEFILENAME_PATH, xmlVirtualFile);
                    } catch (IOException e)
                    {
                        String string = I18n.getString(ResourceI18n.COPY_TEMPLATE_FILE_FAILED);
                        showErrorDialog(string, finalAnActionEvent);
                        e.printStackTrace();
                        return;
                    }
                    //打开文件
                    FileOperation.openFile(project, xmlVirtualFile);
                }
            });
        }
    }

    @Override
    public void update(AnActionEvent e)
    {
        shapeDrawabelName=null;
        virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        if (virtualFiles == null)
            e.getPresentation().setEnabled(false);
    }

    private void showInfoDialog(String text, AnActionEvent e)
    {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar((Project) DataKeys.PROJECT.getData(e.getDataContext()));

        if (statusBar != null)
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.INFO, null).setFadeoutTime(10000L).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }

    private void showErrorDialog(String text, AnActionEvent e)
    {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar((Project) DataKeys.PROJECT.getData(e.getDataContext()));

        if (statusBar != null)
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(text, MessageType.ERROR, null).setFadeoutTime(10000L).createBalloon().show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.atRight);
    }


}
