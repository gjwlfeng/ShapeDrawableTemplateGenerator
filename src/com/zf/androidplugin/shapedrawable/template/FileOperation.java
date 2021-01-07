package com.zf.androidplugin.shapedrawable.template;

import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Lenovo on 2016/1/20.
 */
public class FileOperation {

    //.xml 后缀
    private static final String SUFFIX_XML = ".xml";
    private static String fileName;

    public static void openFile(Project project, VirtualFile xmlVirtualFile) {
        FileEditorManagerEx fileEditorManagerEx = FileEditorManagerEx.getInstanceEx(project);
        if (fileEditorManagerEx == null) {
            return;
        }
        fileEditorManagerEx.openFile(xmlVirtualFile, true);
    }

    public static void copyFile(VirtualFile sourceVirtualFile, VirtualFile targetVirtualFile) throws IOException {
        InputStream inputStream = sourceVirtualFile.getInputStream();
        copyFile(inputStream, targetVirtualFile);
    }

    public static void copyFile(InputStream inputStream, VirtualFile targetVirtualFile) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = targetVirtualFile.getOutputStream(null);

            int length = 0;
            byte[] by = new byte[1024];
            while ((length = inputStream.read(by)) != -1) {
                outputStream.write(by, 0, length);
            }
            outputStream.flush();

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyFile(String filePath, VirtualFile targetVirtualFile) throws IOException {
        InputStream inputStream = FileOperation.class.getResourceAsStream(filePath);
        copyFile(inputStream, targetVirtualFile);
    }


    /**
     * 判断是否有该文件
     *
     * @param virtualFile
     * @param fileName
     * @return
     */
    public static boolean isFindChild(VirtualFile virtualFile, String fileName) {
        if (!virtualFile.isDirectory())
            return false;
        return virtualFile.findChild(fileName.toLowerCase().endsWith(SUFFIX_XML) ? fileName.toLowerCase() : fileName.toLowerCase() + SUFFIX_XML) != null;
    }

    /**
     * 添加后缀 .xml
     *
     * @param fileName
     * @return
     */
    public static String addSuffixXml(String fileName) {
        if (fileName == null)
            throw new IllegalArgumentException("File name cannot be empty\n");
        StringBuilder stringBuilder = new StringBuilder(fileName.toLowerCase());
        return isSuffixXml(fileName) ? stringBuilder.toString() : stringBuilder.append(SUFFIX_XML).toString();
    }

    /**
     * 判断后缀是不是.xml结尾
     *
     * @param content
     * @return
     */
    public static boolean isSuffixXml(String content) {
        if (content == null)
            throw new IllegalArgumentException("Parameters cannot be empty\n");
        return content.toLowerCase().endsWith(SUFFIX_XML);
    }

    /**
     * 创建文件夹
     *
     * @param baseDir
     * @param childDir
     */
    public static VirtualFile createDir(VirtualFile baseDir, String childDir) throws IOException {
        if (!baseDir.isDirectory())
            throw new IllegalArgumentException("Must be a folder\n");
        VirtualFile child = baseDir.findChild(childDir);
        return child == null ? baseDir.createChildDirectory(null, childDir) : child;
    }

    /**
     * 创建文件
     *
     * @param baseDir
     * @param selectorFile
     */
    public static VirtualFile creteFile(VirtualFile baseDir, String selectorFile) throws IOException {
        if (!baseDir.isDirectory())
            throw new IllegalArgumentException("Must be a folder\n");

        VirtualFile child = baseDir.findChild(selectorFile);
        return child == null ? baseDir.createChildData(null, selectorFile) : child;

    }

    /**
     * 判断文件名是否有效
     *
     * @param fileName
     * @return
     */
    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255) {
            return false;
        } else {
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
        }
    }
}

