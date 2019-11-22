package cc.guider.architeature.inspurhotfix;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import cc.guider.architeature.hotfixlib.FixDexUtils;

/**
 * @author JefferyLeng
 * @email lengzheng@haier-jiuzhidao.com
 */
public class GuiderApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
        // 加载热修复Dex文件
        FixDexUtils.loadFixedDex(context);
    }
}
