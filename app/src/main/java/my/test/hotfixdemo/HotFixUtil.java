package my.test.hotfixdemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YiVjay
 * on 2020/4/20
 */
public class HotFixUtil {


    public static void DownDex(Context context, String fileName) {
        File dexDir = context.getDir("dex", Context.MODE_PRIVATE);
        String dexPath = dexDir.getAbsoluteFile() + File.separator + fileName;
        File dexFile = new File(dexPath);
        if (!dexFile.exists()) {
            try {
                dexFile.createNewFile();
                copyFiles(context, fileName, dexFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static List<File> getDexList(File fileDir) {
        List<File> list = new ArrayList<>();
        File[] files = fileDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".dex")) {
                list.add(file);
            }
        }
        return list;
    }

    public static void LoadDex(Context context) {
        File file =context.getDir("dex",Context.MODE_PRIVATE);
        List<File> dexList = getDexList(file);
        if(dexList.size()<=0){
            return;
        }
        try {
            Field pathListField = getField(context.getClassLoader(), "pathList");
            Object pathList = pathListField.get(context.getClassLoader());
            Field dexElementsField = getField(pathList, "dexElements");
            Object[] oldDexElements = (Object[]) dexElementsField.get(pathList);

            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            Method makeDexElements = getMethod(pathList, "makeDexElements", List.class, File.class
                    , List.class, ClassLoader.class);
            Object[] newDexElements = (Object[]) makeDexElements.invoke(pathList, dexList, file, suppressedExceptions, context.getClassLoader());

            Object[] allDexElements = (Object[]) Array.newInstance(oldDexElements.getClass().getComponentType(), oldDexElements.length + newDexElements.length);

            System.arraycopy(newDexElements,0,allDexElements,0,newDexElements.length);
            System.arraycopy(oldDexElements,0,allDexElements,newDexElements.length,oldDexElements.length);

            dexElementsField.set(pathList,allDexElements);

            Log.d("TAG","装载完成");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Field getField(Object object, String name) throws NoSuchFieldException {
        Class<?> aClass = object.getClass();
        while (aClass != null) {
            try {
                Field declaredField = aClass.getDeclaredField(name);
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                return declaredField;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                aClass = aClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("找不到对应属性");
    }

    public static Method getMethod(Object object, String name, Class... value) throws NoSuchMethodException {
        Class<?> aClass = object.getClass();
        while (aClass != null) {
            try {
                Method declaredField = null;
                declaredField = aClass.getDeclaredMethod(name, value);
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                return declaredField;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                aClass = aClass.getSuperclass();
            }
        }
        throw new NoSuchMethodException("找不到对应方法");
    }


    public static void copyFiles(Context context, String fileName, File desFile) {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = context.getAssets().open(fileName);
            out = new FileOutputStream(desFile.getAbsolutePath());
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = in.read(bytes)) != -1)
                out.write(bytes, 0, len);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
