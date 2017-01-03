package tv.wanzi.demo.retrofit.utils;

import android.content.Context;
import android.os.Environment;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tv.wanzi.demo.retrofit.MyApplication;

/**
 * Created by drawf on 17/1/2.
 * ------------------------------
 */

public class FileUtils {

    public static boolean hasSDCardMounted() {
        String state = Environment.getExternalStorageState();
        return state != null && state.equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 单个资源文件拷贝到外部存储目录
     *
     * @param filePath 被拷贝的文件路径 e.g. "image/a.jpg"
     * @return 拷贝后的File对象
     * @throws IOException
     */
    public static File copyAsset2EFD(String filePath) throws IOException {
        if (!hasSDCardMounted()) return null;
        Context context = MyApplication.getContext();

        File filesDir = context.getExternalFilesDir(null);///storage/sdcard0/Android/data/PackageName/files
        File destFile = new File(filesDir, filePath);

        if (!destFile.exists()) {
            if (filePath.contains(".")) {// TODO: drawf 17/1/3 need refactor
                destFile.getParentFile().mkdirs();
            } else {
                destFile.mkdirs();
            }

            InputStream inputStream = context.getAssets().open(filePath);
            FileOutputStream outputStream = new FileOutputStream(destFile);
            ByteStreams.copy(inputStream, outputStream);

            outputStream.close();
            inputStream.close();

        }
        return destFile;
    }

    /**
     * 获取外部存储目录的File对象
     *
     * @param filePath 文件路径 e.g. "image/a.jpg"
     * @return 目标文件存在返回File对象，否则返回null
     */
    public static File getEFDFile(String filePath) {
        if (!hasSDCardMounted()) return null;
        Context context = MyApplication.getContext();

        File filesDir = context.getExternalFilesDir(null);///storage/sdcard0/Android/data/PackageName/files
        File destFile = new File(filesDir, filePath);
        if (destFile.exists() && destFile.isFile()) {
            return destFile;
        }
        return null;
    }

}
