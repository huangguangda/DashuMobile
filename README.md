# DashuMobile
达叔与他的朋友们【手机安全卫士项目】

## Github 欢迎 Star、Fork

### 如果喜欢，那就点个赞吧！❤️ 

## 手机安全卫士项目

### 一，项目结构

毅力这东西不是每个人都有的，天天学基础也不好，直接上手做项目也是可以的。该项目分为 9 个功能模块，包括手机防盗、通讯卫士、软件管家、手机杀毒、缓存清理、进程管理、流量统计、高级工具、设置中心。
手机安全卫士，欢迎页面进入到主界面。

## 【自动升级代码】

首先先去下载Code tree for GitHub插件，它能帮助你更好地查看项目结构与分支下载功能。**图片资源也是用插件帮忙下载，如有不懂自行百度**
来源
Chrome 网上应用店

借用Code tree for GitHub插件功能找到libs下的jar包，下载下来导入项目中。

```
commons-codec-1.9.jar
httpclient-4.5.2.jar
httpcore-4.4.4.jar
```

**自动升级部分代码**

在build.gradle(Module:app)
```
android {
    compileSdkVersion 26
    defaultConfig {
        applicationId "cn.edu.gdmec.android.dashumobile"
        minSdkVersion 19
        targetSdkVersion 26
        versionCode 1
        //
        versionName "1.2"
        //
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
    //添加代码：
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES.txt'
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/notice.txt'
        exclude 'META-INF/license.txt'
        exclude 'META-INF/dependencies.txt'
        exclude 'META-INF/LGPL2.1'
    }
    //
}

dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation 'com.android.support:appcompat-v7:26.1.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    //为导入jar包自动生成
    implementation files('libs/commons-codec-1.9.jar')
    implementation files('libs/httpclient-4.5.2.jar')
    implementation files('libs/httpcore-4.4.4.jar')
    //
}

```

创建DownloadUtils.class类，代码如下：

```
public class DownloadUtils {
    public void downloadApk(String url, String targetFile, Context context){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(mimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        request.setDestinationInExternalPublicDir("/download",targetFile);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long mTaskid = downloadManager.enqueue(request);
    }
}
```

创建VersionEntity.class类：

```
public class VersionEntity {
    //服务器
    public String versioncode;
    //版本描述
    public String description;
    //apk下载地址
    public String apkurl;
}
```

> 可先百度或Google：补充说明导包找到 --app->dependencies->jar dependency,将下载好的.jar包复制到libs中，在进行导包操作。

在清单文件中添加权限

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

创建下载的工具类DownloadUtils

```
public class DownloadUtils {
    public void downloadApk(String url, String targetFile, Context context){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(mimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        request.setDestinationInExternalPublicDir("/download",targetFile);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long mTaskid = downloadManager.enqueue(request);
    }
}
```

创建MyUtils类：

```
public class MyUtils {
    public static String getVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
```
创建VersionUpdateUtils类：

```
public class VersionUpdateUtils {
    private String mVersion;
    private Activity context;
    private VersionEntity versionEntity;

    public VersionUpdateUtils(String mVersion,Activity context){
        this.mVersion=mVersion;
        this.context=context;
    }

    public void getCloudVersion(){

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 5000);
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 5000);
            //http://android2017.duapp.com/updateinfo.html
            HttpGet httpGet = new HttpGet("http://android2017.duapp.com/updateinfo.html");

            HttpResponse execute = httpClient.execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = execute.getEntity();
                String result = EntityUtils.toString(httpEntity, "utf-8");

                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                versionEntity.versioncode = jsonObject.getString("code");
                versionEntity.description = jsonObject.getString("des");
                versionEntity.apkurl = jsonObject.getString("apkurl");
                if (!mVersion.equals(versionEntity.versioncode)) {
                    //版本不同，需升级
                    System.out.println(versionEntity.description);
                    DownloadUtils downloadUtils = new DownloadUtils();
                    downloadUtils.downloadApk(versionEntity.apkurl,"mobileguard.apk",context);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
```

创建SplashActivity类,为启动页：

```
public class SplashActivity extends AppCompatActivity {
    private TextView mTvVersion;
    private String mVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mVersion = MyUtils.getVersion(getApplicationContext());
        mTvVersion = (TextView) findViewById(R.id.tv_splash_version);
        mTvVersion.setText("版本号："+mVersion);
        final VersionUpdateUtils versionUpdateUtils = new VersionUpdateUtils(mVersion,SplashActivity.this);
        new Thread(){
            @Override
            public void run() {
                super.run();
                versionUpdateUtils.getCloudVersion();
            }
        }.start();
    }
}
```

activity_splash.xml的创建：

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/launch_bg">
    <ProgressBar
        android:id="@+id/pb_splash_loading"
        style="?android:attr/progressBarStyle"
        android:layout_alignParentTop="true"
        android:layout_centerHorizontal="true"
        android:layout_centerVertical="true"
        android:layout_marginTop="207dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <TextView
        android:layout_marginTop="50dp"
        android:layout_below="@+id/pb_splash_loading"
        android:text="version:1.0"
        android:layout_centerHorizontal="true"
        android:id="@+id/tv_splash_version"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</RelativeLayout>
```

展示虚拟机运行效果图：[效果图](http://images.cnblogs.com/cnblogs_com/dashucoding/1247529/o_QQ%E6%88%AA%E5%9B%BE20180718213151.png)






