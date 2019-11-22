# GTinker
一个支持Dex动态插桩的轻量级热修复框架,在实现原理借鉴了腾讯Tinker。

### 使用步骤：
 1. 需要App支持分包：
 
        dexOptions {
            javaMaxHeapSize "4g"
            preDexLibraries = false
            additionalParameters = [ // 配置multidex参数
                                     '--multi-dex', // 多dex分包
                                     '--set-max-idx-number=50000', // 每个包内方法数上限
                                     '--main-dex-list=' + '/multidex-config.txt', // 打包到主classes.dex的文件列表
                                     '--minimal-main-dex'
            ]
        }
        
  2.创建multidex-config.txt并配置主包内容，建议配置主要基类就OK（必须保证稳定，不能crash）
     
     cc.guider.architeature.inspurhotfix/MainActivity.class
     cc.guider.architeature.inspurhotfix/BaseActivity.class
     cc.guider.architeature.inspurhotfix/GuiderApplication.class
     
  3.Application中启用分包并加载热修复文件
  
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
        // 加载热修复Dex文件
        FixDexUtils.loadFixedDex(context);
    }
    
  4.适当时机调用修复方法（因实现机制问题，支持冷启动生效）
  
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
      
