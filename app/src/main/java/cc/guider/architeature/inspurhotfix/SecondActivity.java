package cc.guider.architeature.inspurhotfix;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cc.guider.architeature.hotfixlib.FixDexUtils;
import cc.guider.architeature.hotfixlib.utils.Constants;
import cc.guider.architeature.hotfixlib.utils.FileUitls;
import cc.guider.architeature.inspurhotfix.fix.BugFeature;

/**
 * @author jefferyleng
 */
public class SecondActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void show(View view) {
        BugFeature sort = new BugFeature();
        sort.math(this);
    }

    public void fix(View view) {
        fixBug();
    }

    private void fixBug() {
        // classes2.dex ---> /storage/emulated/0/classes2.dex
        // 通过服务器接口下载dex文件，v1.3.3版本有某一个热修复dex包
        File sourceFile = new File(Environment.getExternalStorageDirectory(), Constants.DEX_NAME);

        // 目标路径：私有目录里的临时文件夹odex ta/packan~1 /base.apk
        File targetFile = new File(getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath()
                + File.separator + Constants.DEX_NAME);

        // 如果存在，比如之前修复过classes2.dex。清理
        if (targetFile.exists()) {
            targetFile.delete();
            Toast.makeText(getApplicationContext(), "删除已存在的dex文件", Toast.LENGTH_SHORT).show();
        }

        try {
            // 复制修复包dex文件到app私有目录
            FileUitls.copyFile(sourceFile, targetFile);
            Toast.makeText(getApplicationContext(), "复制dex文件完成", Toast.LENGTH_SHORT).show();
            // 加载热修复Dex文件
            FixDexUtils.loadFixedDex(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
