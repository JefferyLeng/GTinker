package cc.guider.architeature.hotfixlib;

import android.content.Context;


import java.io.File;
import java.util.HashSet;

import cc.guider.architeature.hotfixlib.utils.ArrayUtils;
import cc.guider.architeature.hotfixlib.utils.Constants;
import cc.guider.architeature.hotfixlib.utils.ReflectUtils;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 修复dex文件工具类
 * @author JefferyLeng
 * @date 2018-09-16
 */
public class FixDexUtils {

    /**
     * 存放需要修复的dex集合，可能不止一个dex
     */
    private static HashSet<File> loadedDex = new HashSet<>();

    static {
        // 修复前，进行清理工作
        loadedDex.clear();
    }

    /**
     * 加载热修复的dex文件
     * @param context 上下文
     */
    public static void loadFixedDex(Context context) {
        if (context == null) {
            return;
        }
        // Dex文件目录（私有目录中，存在之前已经复制过来的修复包）
        File fileDir = context.getDir(Constants.DEX_DIR, Context.MODE_PRIVATE);
        File[] listFiles = fileDir.listFiles();
        // 遍历私有目录中所有的文件
        for (File file : listFiles) {
            // 找到修复包，加入到集合
            if (file.getName().endsWith(Constants.DEX_SUFFIX) && !"classes.dex".equals(file.getName())) {
                loadedDex.add(file);
            }
        }

        // 模拟类加载器
        createDexClassLoader(context, fileDir);
    }

    /**
     * 创建加载补丁的DexClassLoader（自有）
     * @param context 上下文
     * @param fileDir Dex文件目录
     */
    private static void createDexClassLoader(Context context, File fileDir) {
        // 创建临时的解压目录（先解压到该目录，再加载java）
        String optimizedDir = fileDir.getAbsolutePath() + File.separator + "opt_dex";
        // 不存在就创建
        File fopt = new File(optimizedDir);
        if (!fopt.exists()) {
            // 创建多级目录
            fopt.mkdirs();
        }
        for (File dex : loadedDex) {
            // 每遍历一个要修复的dex文件，就需要插桩一次
            DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath(),
                    optimizedDir, null, context.getClassLoader());
            hotfix(classLoader, context);
        }
    }

    /**
     * 热修复
     * @param classLoader 自有的类加载器，加载了修复包的DexClassLoader
     * @param context 上下文
     */
    private static void hotfix(DexClassLoader classLoader, Context context) {
        // 获取系统PathClassLoader类加载器
        PathClassLoader pathLoader = (PathClassLoader) context.getClassLoader();

        try {
            // 获取自有的dexElements数组对象
            Object myDexElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(classLoader));

            // 获取系统的dexElements数组对象
            Object systemDexElements = ReflectUtils.getDexElements(ReflectUtils.getPathList(pathLoader));

            // 合并成新的dexElements数组对象
            Object dexElements = ArrayUtils.combineArray(myDexElements, systemDexElements);

            // 通过反射再去获取   系统的pathList对象
            Object systemPathList = ReflectUtils.getPathList(pathLoader);

            // 重新赋值给系统的pathList属性  --- 修改了pathList中的dexElements数组对象
            ReflectUtils.setField(systemPathList, systemPathList.getClass(), dexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
