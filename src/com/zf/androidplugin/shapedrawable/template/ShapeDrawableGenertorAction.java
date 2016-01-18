package com.zf.androidplugin.shapedrawable.template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
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
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Lenovo on 2016/1/14.
 */
public class ShapeDrawableGenertorAction extends AnAction
{
    private static final String SUFFIX_XML = ".xml";
    private VirtualFile[] virtualFiles = null;
    private String shapeDrawabelName = "";
    private static final String TEMPLATEFILENAME = "shape_drawable_template.xml";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent)
    {
        if (virtualFiles.length != 1)
        {
            showInfoDialog("请选择一下文件夹", anActionEvent);
        } else if (!virtualFiles[0].isDirectory())
        {
            showInfoDialog("请选择一下文件夹", anActionEvent);
        } else
        {
            Project project = anActionEvent.getProject();
            String title = "设置文件名";
            String message = "请输入ShapeDrawable文件名";

            do
            {
                if ("".equals(shapeDrawabelName.trim()))
                {
                    showInfoDialog("请输入文件名", anActionEvent);
                } else if (shapeDrawabelName != null)
                {
                    showInfoDialog("该目录下已经有文件", anActionEvent);
                }
                shapeDrawabelName = Messages.showInputDialog(project, message, title, Messages.getQuestionIcon());
            }
            while ("".equals(shapeDrawabelName.trim()) || virtualFiles[0].findChild(shapeDrawabelName.endsWith("SUFFIX_XML") ? shapeDrawabelName : shapeDrawabelName + SUFFIX_XML) != null);

            if (shapeDrawabelName == null)
                return;

            VirtualFile srcVirtualFile=null;
            try
            {
                srcVirtualFile= virtualFiles[0].createChildData(null, shapeDrawabelName.endsWith("SUFFIX_XML") ? shapeDrawabelName : shapeDrawabelName + SUFFIX_XML);
            } catch (IOException e)
            {
                showInfoDialog("生成Shape Drawable 模板文件失败", anActionEvent);
                e.printStackTrace();
                return;
            }


            final VirtualFile finalSrcVirtualFile = srcVirtualFile;

            WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), new Runnable()
            {
                @Override
                public void run()
                {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try
                    {
                        inputStream = this.getClass().getResourceAsStream("/" + TEMPLATEFILENAME);
                        outputStream = finalSrcVirtualFile.getOutputStream(null);
                        byte[] by = new byte[1024];
                        int length = 0;
                        while ((length = inputStream.read(by)) != -1)
                        {
                            outputStream.write(by, 0, length);
                        }
                        outputStream.flush();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        try
                        {
                            if (inputStream != null)
                            {
                                inputStream.close();
                            }
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        try
                        {
                            if (outputStream != null)
                            {
                                outputStream.close();
                            }
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void update(AnActionEvent e)
    {
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
