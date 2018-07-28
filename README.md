# DashuMobile - [可参考另一版](https://github.com/huangguangda/MobileSafe)
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

## 启动界面线程的handler处理

![包结构](http://images.cnblogs.com/cnblogs_com/dashucoding/1247529/o_QQ%E6%88%AA%E5%9B%BE20180721094703.png)

#### DownloadUtils

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

#### HomeActivity

```
public class HomeActivity extends Activity {
    TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView = findViewById(R.id.tv);
    }
}
```

#### MyUtils
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

#### SplashActivity
```
public class SplashActivity extends Activity {
    private TextView tv_version_name;
    private int mLocalVersionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化UI
        initUI();
        //初始化数据
        initData();

        final VersionUpdateUtils versionUpdateUtils = new VersionUpdateUtils(tv_version_name,SplashActivity.this);
        new Thread(){
            @Override
            public void run(){
                super.run();
                versionUpdateUtils.getCloudVersion();

            }
        }.start();
    }


    /*
    *  初始化UI方法
    * */
    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
    }
    /*
     * 获取数据方法
     * */
    private void initData() {
        //1.应用版本名称
        tv_version_name.setText(getVersionName());
    }
    /*
     * 获取版本名称：清单文件中
     * @return 应用版本名称 放回null代表异常
     * */
    public String getVersionName() {
        //1.包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //2.从包的管理者对象中，获取指定包名大的基本信息(版本名称，版本号);
        //传0代表获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            //3.获取版本名称
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
```

#### VersionEntity

public class VersionEntity {
    //服务器
    public String versioncode;
    //版本描述
    public String description;
    //apk下载地址
    public String apkurl;
}

#### VersionUpdateUtils
```
public class VersionUpdateUtils {
    private TextView tv_version_name;
    private Activity context;
    private VersionEntity versionEntity;

    private static final int MESSAGE_IO_ERROR=102;
    private static final int MESSAGE_JSON_ERROR=103;
    private static final int MESSAGE_SHOW_DIALOG=104;
    private static final int MESSAGE_ENTERHOME=105;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_IO_ERROR:
                    Toast.makeText(context, "IO错误", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_ERROR:
                    Toast.makeText(context, "JSON解析错误", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                case MESSAGE_SHOW_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity ( intent );
                    context.finish ();
                    break;
            }
        }
    };



    public VersionUpdateUtils(TextView tv_version_name, Activity context){
        this.tv_version_name=tv_version_name;
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
                if (!tv_version_name.equals(versionEntity.versioncode)) {
                    //版本不同，需升级
                    /*System.out.println(versionEntity.description);
                    DownloadUtils downloadUtils = new DownloadUtils();
                    downloadUtils.downloadApk(versionEntity.apkurl,"mobileguard.apk",context);*/
                    // 版本号不一致
                    handler.sendEmptyMessage ( MESSAGE_SHOW_DIALOG );
                }
            }
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_ERROR);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_ERROR);
            e.printStackTrace();
        }
    }

    private void showUpdateDialog(final VersionEntity versionEntity) {
        //创建dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检测有新版本：" + versionEntity.versioncode);
        builder.setMessage(versionEntity.description);
        builder.setCancelable(false);
        builder.setIcon( R.mipmap.ic_launcher_round);
        builder.setPositiveButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载apk
                downloadNewApk(versionEntity.apkurl);
            }
        });
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                enterHome();
            }
        });
        builder.show();
    }
    //发送进入主界面消息
    private void enterHome() {
        handler.sendEmptyMessage(MESSAGE_ENTERHOME);
    }
    private void downloadNewApk(String apkurl) {
        DownloadUtils downloadUtils = new DownloadUtils();
        downloadUtils.downloadApk(apkurl, "mobileguard.apk", context);
    }

}
```

布局：

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_marginTop="50dp"
        android:text="欢迎来到主界面"
        android:layout_centerHorizontal="true"
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</RelativeLayout>
```

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@drawable/launch_bg">
    <TextView
        android:id="@+id/tv_version_name"
        android:layout_centerInParent="true"
        android:text="版本名称："
        android:shadowDx="1"
        android:shadowDy="1"
        android:shadowColor="#f00"
        android:shadowRadius="1"
        android:textSize="16sp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
    <ProgressBar
        android:layout_below="@+id/tv_version_name"
        android:layout_centerHorizontal="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</RelativeLayout>
```

展示[启动页](http://images.cnblogs.com/cnblogs_com/dashucoding/1247529/o_QQ%E6%88%AA%E5%9B%BE20180721095811.png) [主界面](http://images.cnblogs.com/cnblogs_com/dashucoding/1247529/o_QQ%E6%88%AA%E5%9B%BE20180721100110.png)

## 第二期：主界面实现

写介绍目录结构：
app下
```
manifests
java
 m1home
  adapter
   HomeAdapter
  entity
   VersionEntity
  utity
   DownloadUtils
   MyUtils
   VerstionUpdateUtils
  HomeActivity
 SplashActivity
 ```
 
 可自行用插件查看结构目录：
 
 主界面布局 activity_home.xml
 ```
 <?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="center"
    android:background="@drawable/bg_home">
    <LinearLayout
        android:gravity="center"
        android:orientation="horizontal"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <ImageView
            android:scaleType="fitXY"
            android:background="@drawable/superman"
            android:layout_width="100dp"
            android:layout_height="107dp" />
        <TextView
            android:typeface="normal"
            android:textScaleX="1.1"
            android:textColor="#90000000"
            android:text="主人，我是你的手机小护卫"
            android:textSize="16sp"
            android:id="@+id/tv_home"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

    </LinearLayout>
    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <GridView
            android:numColumns="3"
            android:verticalSpacing="10dp"
            android:horizontalSpacing="10dp"
            android:gravity="center"
            android:layout_centerInParent="true"
            android:id="@+id/gv_home"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

        </GridView>
    </RelativeLayout>
</LinearLayout>
 ```
 
item_home.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:gravity="center"
    android:orientation="vertical">
    <ImageView
        android:background="@drawable/atools"
        android:id="@+id/iv_home"
        android:layout_width="80dp"
        android:layout_height="80dp" />
    <TextView
        android:id="@+id/tv_home"
        android:textSize="18sp"
        android:textColor="#80000000"
        android:text="高级工具"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
</LinearLayout>
```

在m1home下创建adapter包，在adapter包下创建HomeAdapter类
   
```

public class HomeAdapter extends BaseAdapter{
    int[] imageId = {R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,R.drawable.trojan,R.drawable.sysoptimize,R.drawable.taskmanager,R.drawable.netmanager,R.drawable.atools,R.drawable.settings};
    String[] names = {"手机防盗","通讯卫士","软件管家","手机杀毒","缓存清理","进程管理","流量统计","高级工具","设置中心"};
    private Context context;

    public HomeAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return 9;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view1 = View.inflate(context,R.layout.item_home,null);
        ImageView iv_icon = view1.findViewById(R.id.iv_home);
        TextView tv_name = view1.findViewById(R.id.tv_home);
        iv_icon.setImageResource(imageId[position]);
        tv_name.setText(names[position]);
        return view1;
    }
}
```

HomeActivity.java

```
public class HomeActivity extends Activity {
    private GridView gv_home;
    private long mExitTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //getSupportActionBar().hide();
        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter(HomeActivity.this));
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){

                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis()-mExitTime)<2000){
                System.exit(0);
            }else {
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

}
```

主界面实现效果成功~
![效果](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180727035153.png)




