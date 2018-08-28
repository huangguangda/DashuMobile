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

## 手机防盗-设置及输入密码

设计思路：
在主界面点击『手机防盗』图标，判断是否记录了密码，如果记录就弹出输入密码对话框，否则弹出设置密码对话框，

输入密码对话框，读取密码，判断密码输入的是否与保存密码一致

设置密码对话框，判断非空，判断一致，长度要求，密码规则，保存密码

1.自定义圆角百背景的形状的drawable，drawable鼠标右键，new-drawable resource file ,coner_bg_white.xml
2.自定义对话框的style,在文件res/values/styles.xml 添加
3.自定义输入密码对话框和设置密码对话框，继承于对话框，加上密码的输入项
  1）新建布局文件setup_password_dialog.xml inter_password_dialog.xml
  2）创建密码输入框的dialog类


4.HomeActivity.java
  1）创建SharedPerefrence。
	2)创建方法：isSetUpPasword()  getPassword() savePassword()

## 项目结构中新增的文件

![效果](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180804200223.png)
![效果](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180804200256.png)

coner_bg_white.xml

```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
 <corners
     android:radius="6.0dp"/>
    <solid android:color="#ffffff"/>
</selector>
```
values/styles.xml
```
...
<style name="dialog_custom" parent="android:style/Theme.Dialog">
        <item name="android:windowFrame">@null</item>
        <item name="android:windowNoTitle">true</item>
        <item name="android:background">#00000000</item>
        <item name="android:windowBackground">@android:color/transparent</item>
    </style>
</resources>
```
setup_password_dialog.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="260dp"
    android:layout_height="220dp"
    android:minHeight="150dp"
    android:orientation="vertical"
    android:layout_gravity="center"
    android:background="@drawable/coner_bg_white">
    <TextView
        android:id="@+id/tv_setuppwd_title"
        android:padding="8dp"
        android:layout_margin="5dp"
        android:textColor="#1ABDE6"
        android:text="设置密码"
        android:textSize="20sp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
    <View
        android:layout_marginBottom="10dp"
        android:background="#1ABDE6"
        android:layout_width="match_parent"
        android:layout_height="1.5px"/>
    <EditText
        android:id="@+id/et_firstpwd"
        android:layout_margin="5dp"
        android:inputType="textPassword"
        android:hint="请输入密码"
        android:background="@drawable/edit_normal"
        android:layout_width="match_parent"
        android:layout_height="40dp" />
    <EditText
        android:id="@+id/et_affirm_password"
        android:layout_margin="5dp"
        android:inputType="textPassword"
        android:hint="请再次输入密码"
        android:background="@drawable/edit_normal"
        android:layout_width="match_parent"
        android:layout_height="40dp" />
    <View
        android:background="#1ABDE6"
        android:layout_marginTop="10dp"
        android:layout_width="match_parent"
        android:layout_height="1.0px"/>
    <LinearLayout
        android:gravity="center"
        android:orientation="horizontal"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <Button
            android:id="@+id/btn_ok"
            android:layout_weight="1"
            android:text="确认"
            android:background="@android:color/white"
            android:layout_width="0dp"
            android:layout_height="wrap_content" />
        <View
            android:background="#1ABDE6"
            android:layout_width="1.0px"
            android:layout_height="match_parent"/>
        <Button
            android:id="@+id/btn_cancel"
            android:layout_weight="1"
            android:text="取消"
            android:background="@android:color/white"
            android:layout_width="0dp"
            android:layout_height="wrap_content" />
    </LinearLayout>
</LinearLayout>
```
inter_password_dialog.xml

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="260dp"
    android:layout_height="170dp"
    android:minHeight="150dp"
    android:orientation="vertical"
    android:layout_gravity="center"
    android:background="@drawable/coner_bg_white">
    <TextView
        android:id="@+id/tv_interpwd_title"
        android:padding="8dp"
        android:layout_margin="5dp"
        android:textColor="#1ABDE6"
        android:text="输入密码"
        android:textSize="20sp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
    <View
        android:background="#1ABDE6"
        android:layout_marginBottom="10dp"
        android:layout_width="match_parent"
        android:layout_height="1.5px"/>
    <EditText
        android:id="@+id/et_inter_password"
        android:layout_margin="5dp"
        android:inputType="textPassword"
        android:hint="请输入密码"
        android:background="@drawable/edit_normal"
        android:layout_width="match_parent"
        android:layout_height="40dp" />
    <View
        android:layout_marginTop="10dp"
        android:background="#1ABDE6"
        android:layout_width="match_parent"
        android:layout_height="1.0px"/>
    <LinearLayout
        android:orientation="horizontal"
        android:gravity="center"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <Button
            android:id="@+id/btn_comfirm"
            android:layout_weight="1"
            android:text="确认"
            android:background="@android:color/white"
            android:layout_width="0dp"
            android:layout_height="wrap_content" />
        <View
            android:background="#1ABDE6"
            android:layout_width="1.0px"
            android:layout_height="match_parent"/>
        <Button
            android:id="@+id/btn_dismiss"
            android:layout_weight="1"
            android:text="取消"
            android:background="@android:color/white"
            android:layout_width="0dp"
            android:layout_height="wrap_content" />
    </LinearLayout>
</LinearLayout>
```
MD5Utils.java
```
public class MD5Utils {
    public static String encode(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);

                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
```
setUpPassWordDialog.java
```
public class SetUpPasswordDialog extends Dialog implements View.OnClickListener{
    //标题栏
    private TextView mTitleTV;
    //首次输入密码文本框
    public EditText mFirstPWDET;
    //确认密码文本框
    public EditText mAffirmET;
    //回调接口
    private MyCallBack myCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.setup_password_dialog);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        mTitleTV = findViewById(R.id.tv_setuppwd_title);
        mFirstPWDET = findViewById(R.id.et_firstpwd);
        mAffirmET = findViewById(R.id.et_affirm_password);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            mTitleTV.setText(title);
        }
    }

    public void setCallBack(MyCallBack myCallBack){
        this.myCallBack = myCallBack;
    }
    public SetUpPasswordDialog(Context context){
        super(context,R.style.dialog_custom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                System.out.print("SetupPasswordDialog");
                myCallBack.ok();
                break;
            case R.id.btn_cancel:
                myCallBack.cancel();
                break;
        }
    }
    public interface MyCallBack{
        void ok();
        void cancel();
    }
}
```
InterPasswordDialog.java
```
public class InterPasswordDialog extends Dialog implements View.OnClickListener{
    //对话框标题
    private TextView mTitleTV;
    //输入密码文本框
    private EditText mInterET;
    //确认按钮
    private Button mOKBtn;
    //取消按钮
    private Button mCancleBtn;
    //回调接口
    private MyCallBack myCallBack;
    private Context context;
    public InterPasswordDialog(Context context){
        super(context, R.style.dialog_custom);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.inter_password_dialog);
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        mTitleTV = findViewById(R.id.tv_interpwd_title);
        mInterET = findViewById(R.id.et_inter_password);
        mOKBtn = findViewById(R.id.btn_comfirm);
        mCancleBtn = findViewById(R.id.btn_dismiss);
        mOKBtn.setOnClickListener(this);
        mCancleBtn.setOnClickListener(this);
    }
    public void setTitle(String title){
        if (!TextUtils.isEmpty(title)){
            mTitleTV.setText(title);
        }
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_comfirm:
                myCallBack.confirm();
                break;
            case R.id.btn_dismiss:
                myCallBack.cancle();
                break;
        }
    }
    public String getPassword(){
        return mInterET.getText().toString();
    }
    public void setCallBack(MyCallBack myCallBack){
        this.myCallBack = myCallBack;
    }
    public interface MyCallBack{
        void confirm();
        void cancle();
    }
}
```
HomeActivity.java
```
public class HomeActivity extends Activity {
    private GridView gv_home;
    private long mExitTime;
    //存储手机防盗密码
    private SharedPreferences msharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //getSupportActionBar().hide();
        msharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter(HomeActivity.this));
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://点击手机防盗d
                        if (isSetUpPassword()){
                         //弹出输入密码对话框
                            showInterPswdDialog();
                        }else {
                            //弹出设置密码对话框
                            showSetUpPswdDialog();
                        }
                        break;
                }
            }
        });
    }
    public void startActivity(Class<?> cls){
        Intent intent = new Intent(HomeActivity.this,cls);
        startActivity(intent);
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
    //弹出设置密码对话框，本方法需要完成"手机防盗模块"之后才能启用
    private void showSetUpPswdDialog(){
        final SetUpPasswordDialog setUpPasswrodDialog = new SetUpPasswordDialog ( HomeActivity.this );
        setUpPasswrodDialog.setCallBack ( new SetUpPasswordDialog.MyCallBack (){
            @Override
            public void ok(){
                String firstPwsd = setUpPasswrodDialog.mFirstPWDET.getText ().toString ().trim ();
                String affirmPwsd = setUpPasswrodDialog.mAffirmET.getText ().toString ().trim ();
                if (!TextUtils.isEmpty ( firstPwsd )&&!TextUtils.isEmpty ( affirmPwsd )){
                    if (firstPwsd.equals ( affirmPwsd )){
                        // 两次密码一致,存储密码
                        savePswd(affirmPwsd);
                        setUpPasswrodDialog.dismiss ();
                        // 显示输入密码对话框
                        showInterPswdDialog();
                    }else {
                        Toast.makeText ( HomeActivity.this, "两次密码不一致！", Toast.LENGTH_LONG ).show();
                    }
                }else{
                    Toast.makeText ( HomeActivity.this, "密码不能为空！", Toast.LENGTH_LONG ).show ();
                }
            }
            @Override
            public void cancel(){
                setUpPasswrodDialog.dismiss ();
            }
        } );
        setUpPasswrodDialog.setCancelable ( true );
        setUpPasswrodDialog.show ();
    }
    // 弹出输入密码对话框   本方法需要完成"手机防盗模块"之后才能启用
    private void showInterPswdDialog(){
        final String password = getPassword();
        final InterPasswordDialog mInPswdDialog = new InterPasswordDialog ( HomeActivity.this );
        mInPswdDialog.setCallBack (new InterPasswordDialog.MyCallBack (){
            @Override
            public void confirm(){
                if (TextUtils.isEmpty ( mInPswdDialog.getPassword () )){
                    Toast.makeText ( HomeActivity.this, "密码不能为空！", Toast.LENGTH_LONG ).show ();
                }else if (password.equals ( MD5Utils.encode ( mInPswdDialog.getPassword () ) )){
                    // 进入防盗主界面
                    mInPswdDialog.dismiss ();
                    //startActivity ( LostFindActivity.class );
                    Toast.makeText ( HomeActivity.this, "可以进入手机防盗模块",Toast.LENGTH_LONG ).show ();
                }else {
                    // 对话框消失，弹出
                    mInPswdDialog.dismiss ();
                    Toast.makeText ( HomeActivity.this, "密码有误，请重新输入", Toast.LENGTH_LONG ).show ();
                }
            }
            @Override
            public void cancle(){
                mInPswdDialog.dismiss ();
            }
        });
        mInPswdDialog.setCancelable ( true );
        // 让对话框显示
        mInPswdDialog.show ();
    }

    //保存密码 本方法需要完成“手机防盗模块”之后才能启用
    private void savePswd(String affirmPwsd){
        SharedPreferences.Editor edit = msharedPreferences.edit();
        //为了防止用户隐私被泄漏，因此需要加密密码
        edit.putString("PhoneAntiTheftPWD", MD5Utils.encode(affirmPwsd));
        edit.commit();
    }

    //获取密码
    private String getPassword(){
        String password = msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if (TextUtils.isEmpty(password)){
            return "";
        }
        return password;
    }
    //判断用户是否设置过手机防盗密码
    private boolean isSetUpPassword(){
        String password = msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if (TextUtils.isEmpty(password)){
            return false;
        }
        return true;
    }

}
```

## 效果展示

![1](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180804195724.png)
![2](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180804195744.png)
![3](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180804195803.png)
![4](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180804195821.png)
![5](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180804195834.png)
![6](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180804195845.png)

## 手机防盗设置向导界面

#### 项目结构截图

![1](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180805222209.png)
![2](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180805222255.png)
![3](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180805222336.png)

#### 手机防盗界面第一部分框架和UI实现步骤

#### 1.在m2theftgauard包下创建Empty Activity名称为LostFindActivity。

1)准备图片：
```
swtch_btn_on.png
switch_btn_off.png 
arrow_right.png
sim_alarm_icon_small.png
```

2)增加文字样式 ：style.xml 中增加 textview16sp

3)增加颜色：color.xml 中增加 purple

4)编写开关按钮的背景设置的xml文件（开关两个状态及背景图） drawable/toggle_btn_bg_selector.xml

5)编写形状配置xml（紫色背景上边是圆角的矩形）  drawable/round_purple_tv_bg.xml  location_icon_small.png

6)编写按钮标题栏布局 layout/titlebar.xml

7)编写Activity的布局文件activity_lost_find.xml

8)编写Activity.java

#### 2.编写滑屏界面的指示点的布局文件layout/setup_radiogroup.xml

1）图片：
lock_screen_icon_small.png
delete_data_small.png

2)编写形状配置xml（紫色实心圆和白色实心圆） drawable/circle_purple.xml  drawable/circle_white.xml 

3)编写滑屏指示点的背景设置xml（显示紫色或白色实心圆） drawable/circle_purple_bg_selector.xml

4)编写滑屏界面的指示点的布局文件 layout/setup_radiogroup.xml

#### 3.创建BaseSetupActivity，抽象类

1)创建动画文件 pre_in.xml pre_out.xml next_in.xml next_out.xml

2)编写逻辑代码处理手势动作

#### 4.创建Setup1Activty父类是BaseSetupActivity

1)图片:
```
add.png
sim_alarm_icon.png
location_icon.png
lock_screen_icon.png
delete_data.png
```

2)编写布局文件activity_setup_1.xml

3)编写简单的逻辑代码

#### 5.创建Setup2Activity父类是BaseSetupActivity

1)图片
recomand_icon.9.png

2)编写sim卡绑定背景设置xml drawable/sim_bind_selector.xml

3)编写布局文件activity_setup_2.xml

4)编写简单逻辑代码

#### 6．创建Setup3Activity父类是BaseSetupActivity

1）图片
coner_white_rec.png
contact_et_left_icon.png

2）编写布局文件activity_setup_3.xml

3）编写简单的逻辑代码

#### 7．创建Setup4Activity父类是BaseSetupActivity

1）图片
security_phone.png

2）编写布局文件activity_setup_4.xml

3）编写简单的逻辑代码

#### 8.清单文件声明Activity

## m1Home/HomeActivity.java   在showInterpswdDialog()方法中加上启动手

```
// 弹出输入密码对话框   本方法需要完成"手机防盗模块"之后才能启用
    private void showInterPswdDialog(){
        ...
         // 进入防盗主界面
         mInPswdDialog.dismiss ();
         startActivity ( LostFindActivity.class );
         Toast.makeText ( HomeActivity.this, "可以进入手机防盗模块",Toast.LENGTH_LONG ).show ();
       }else {
          ...
    }
```

values/style.xml

```
...
<!--手机防盗界面增加的-->
    <style name="wrapcontent">
        <item name="android:layout_width">wrap_content</item>
        <item name="android:layout_height">wrap_content</item>
    </style>
    <!--TextView 16sp-->
    <style name="textview16sp" parent="wrapcontent">
        <item name="android:textSize">16sp</item>
        <item name="android:gravity">center_vertical</item>
    </style>
</resources>
```

values/color.xml

```
...
<!--手机防盗模块需要用到的-->
    <color name="purple">#5542B8</color>
</resources>
```

drawable/toggle_btn_bg_selector.xml
```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:drawable="@drawable/swtch_btn_on" android:state_checked="true"/>
    <item android:drawable="@drawable/switch_btn_off" android:state_checked="false"/>

</selector>
```

drawable/round_purple_tv_bg.xml
```
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" 
    android:shape="rectangle">
    <solid android:color="@color/purple"/>
    <corners android:topLeftRadius="5dp"
        android:topRightRadius="5dp"
        android:bottomLeftRadius="0dp"
        android:bottomRightRadius="0dp"/>
    
</shape>
```

layout/titlebar.xml
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/rl_titlebar"
    android:layout_width="match_parent"
    android:layout_height="55dp">
    <TextView
        android:id="@+id/tv_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="20sp"
        android:textColor="@android:color/white"
        android:layout_centerInParent="true"/>
    <ImageView
        android:id="@+id/imgv_leftbtn"
        android:layout_width="30dp"
        android:layout_height="30dp"
        android:layout_alignParentLeft="true"
        android:layout_centerVertical="true"
        android:layout_marginLeft="10dp"/>
    <ImageView
        android:id="@+id/imgv_rightbtn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentRight="true"
        android:layout_centerVertical="true"
        android:layout_marginRight="10dp"/>
</RelativeLayout>
```

layout/ activity_lost_find.xml   
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/white"
    android:orientation="vertical">
    <include layout="@layout/titlebar"/>
    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="50dp">
        <TextView
            style="@style/textview16sp"
            android:layout_alignParentLeft="true"
            android:layout_centerVertical="true"
            android:layout_marginLeft="10dp"
            android:text="安全号码"
            android:textColor="@color/purple"/>
        <TextView
            android:id="@+id/tv_safephone"
            style="@style/textview16sp"
            android:layout_alignParentRight="true"
            android:layout_centerVertical="true"
            android:layout_marginRight="15dp"
            android:textColor="@color/purple"/>
    </RelativeLayout>
    <View
        android:layout_width="match_parent"
        android:layout_height="10.px"
        android:background="#10000000"/>
    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="50dp">
        <TextView
            android:id="@+id/tv_lostfind_protectstauts"
            style="@style/textview16sp"
            android:layout_alignParentLeft="true"
            android:layout_centerVertical="true"
            android:layout_marginLeft="10dp"
            android:text="防盗保护是否开启"
            android:textColor="@color/purple"/>
        <ToggleButton
            android:id="@+id/togglebtn_lostfind"
            android:layout_width="70dp"
            android:layout_height="30dp"
            android:layout_alignParentRight="true"
            android:layout_centerVertical="true"
            android:layout_marginRight="10dp"
            android:background="@drawable/toggle_btn_bg_selector"
            android:textOff=""
            android:textOn=""/>
    </RelativeLayout>
    <View
        android:layout_width="match_parent"
        android:layout_height="1.0px"
        android:background="#10000000"/>
    <RelativeLayout
        android:id="@+id/rl_inter_setup_wizard"
        android:layout_width="match_parent"
        android:layout_height="50dp">
        <TextView
            style="@style/textview16sp"
            android:layout_alignParentLeft="true"
            android:layout_centerVertical="true"
            android:layout_marginLeft="10dp"
            android:text="重新进入设置向导"
            android:textColor="@color/purple"/>
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentRight="true"
            android:layout_centerVertical="true"
            android:layout_marginRight="10dp"
            android:background="@drawable/arrow_right"/>
    </RelativeLayout>
    <View
        android:layout_width="match_parent"
        android:layout_height="1.0px"
        android:background="#10000000"/>
    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="10dp"
        android:layout_marginRight="10dp"
        android:layout_marginTop="30dp"
        android:orientation="vertical">
        <TextView
            android:layout_width="match_parent"
            android:layout_height="35dp"
            android:background="@drawable/round_purple_tv_bg"
            android:gravity="center"
            android:text="短信指令功能简介"
            android:textColor="@android:color/white"
            android:textSize="16sp"/>
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="15dp">
            <TextView
                style="@style/textview16sp"
                android:layout_alignParentRight="true"
                android:layout_marginRight="10dp"
                android:text="#*alarm*#"
                android:textColor="@color/purple"/>
            <TextView
                style="@style/textview16sp"
                android:layout_alignParentLeft="true"
                android:layout_centerVertical="true"
                android:layout_marginLeft="10dp"
                android:drawableLeft="@drawable/sim_alarm_icon_small"
                android:drawablePadding="5dp"
                android:text="播放报警音乐"
                android:textColor="@color/purple"/>
        </RelativeLayout>
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="15dp">
            <TextView
                style="@style/textview16sp"
                android:layout_alignParentRight="true"
                android:layout_marginRight="10dp"
                android:text="#*location*#"
                android:textColor="@color/purple"/>
            <TextView
                style="@style/textview16sp"
                android:layout_alignParentLeft="true"
                android:layout_centerVertical="true"
                android:layout_marginLeft="10dp"
                android:drawableLeft="@drawable/location_icon_small"
                android:drawablePadding="5dp"
                android:text="GPS追踪"
                android:textColor="@color/purple"/>

        </RelativeLayout>
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="15dp">
            <TextView
                style="@style/textview16sp"
                android:layout_alignParentRight="true"
                android:layout_marginRight="10dp"
                android:text="#*lockScreen*#"
                android:textColor="@color/purple"/>
            <TextView
                style="@style/textview16sp"
                android:layout_alignParentLeft="true"
                android:layout_centerVertical="true"
                android:layout_marginLeft="10dp"
                android:drawableLeft="@drawable/lock_screen_icon_small"
                android:drawablePadding="5dp"
                android:text="远程锁屏"
                android:textColor="@color/purple"/>
        </RelativeLayout>
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="15dp">
            <TextView
                style="@style/textview16sp"
                android:layout_alignParentRight="true"
                android:layout_marginRight="10dp"
                android:text="#*wipedata*#"
                android:textColor="@color/purple"/>
            <TextView
                style="@style/textview16sp"
                android:layout_alignParentLeft="true"
                android:layout_centerVertical="true"
                android:layout_marginLeft="10dp"
                android:drawableLeft="@drawable/delete_data_small"
                android:drawablePadding="5dp"
                android:text="运程删除数据"
                android:textColor="@color/purple"/>

        </RelativeLayout>
    </LinearLayout>
</LinearLayout>
```

LostFindActivity.java 
```
public class LostFindActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        startSetUp1Activity();
    }

    private void startSetUp1Activity(){
        Intent intent = new Intent(LostFindActivity.this,Setup1Activity.class);
        startActivity(intent);
        finish();
    }
}
```

drawable/circle_purple.xml
```
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" 
    android:shape="oval">
    <solid android:color="#7D65FC"/>
</shape>
```

drawable/circle_white.xml
```
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" 
    android:shape="oval">
    <solid android:color="#FFFFFF"/>
</shape>
```

drawable/circle_purple_bg_selector.xml
```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android" >
    <item android:state_checked="true"  android:drawable="@drawable/circle_purple"/>
    <item android:state_checked="false"  android:drawable="@drawable/circle_white"/>

</selector>
```

layout/setup_radiogroup.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:gravity="center"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <RadioGroup
        android:layout_gravity="center"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        <RadioButton
            android:id="@+id/rb_first"
            android:layout_width="10dp"
            android:layout_height="10dp"
            android:button="@null"
            android:background="@drawable/circle_purple_bg_selector"/>
        <RadioButton
            android:id="@+id/rb_second"
            android:layout_width="10dp"
            android:layout_height="10dp"
            android:button="@null"
            android:layout_marginLeft="15dp"
            android:background="@drawable/circle_purple_bg_selector"/>
        <RadioButton
            android:id="@+id/rb_third"
            android:layout_width="10dp"
            android:layout_height="10dp"
            android:button="@null"
            android:layout_marginLeft="15dp"
            android:background="@drawable/circle_purple_bg_selector"/>
        <RadioButton
            android:id="@+id/rb_four"
            android:layout_width="10dp"
            android:layout_height="10dp"
            android:button="@null"
            android:layout_marginLeft="15dp"
            android:background="@drawable/circle_purple_bg_selector"/>
    </RadioGroup>

</LinearLayout>
```

anim/pre_in.xml
```
<?xml version="1.0" encoding="utf-8"?>
<!-- 显示上一步，上一个页面进来的效果 -->
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXDelta="-100%p" 
    android:toXDelta="0"
    android:fromYDelta="0"
    android:toYDelta="0"
    android:duration="500"
    android:repeatCount="0"
    >

</translate>
```

anim/pre_out.xml
```
<?xml version="1.0" encoding="utf-8"?>
<!-- 显示上一步，当前页面出去的效果 -->
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXDelta="0" 
    android:toXDelta="100%p"
    android:fromYDelta="0"
    android:toYDelta="0"
    android:duration="500"
    android:repeatCount="0"
    >

</translate>
```

anim/next_in.xml
```
<?xml version="1.0" encoding="utf-8"?>
<!-- 显示下一步，下一个页面进来的效果 -->
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXDelta="100%p" 
    android:toXDelta="0"
    android:fromYDelta="0"
    android:toYDelta="0"
    android:duration="500"
    android:repeatCount="0"
    >

</translate>
```

anim/next_out.xml
```
<?xml version="1.0" encoding="utf-8"?>
<!-- 显示下一步，当前页面出去的效果 -->
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXDelta="0" 
    android:toXDelta="-100%p"
    android:fromYDelta="0"
    android:toYDelta="0"
    android:duration="500"
    android:repeatCount="0"
    >

</translate>
```

BaseSetupActivity.java

```
public abstract class BaseSetUpActivity extends AppCompatActivity {
    public SharedPreferences sp;
    //手势识别类
    private GestureDetector mGestureDetector;
    //抽象方法 显示前一屏的activtiy
    public abstract void showNext();
    //抽象方法 显示后一
    public abstract void showPre();

    // 用手势识别器去识别触控事件
    @Override
    public boolean onTouchEvent(MotionEvent event){
        // 分析手势事件
        mGestureDetector.onTouchEvent ( event );
        return super.onTouchEvent ( event );
    }
    //开启新的activity并且关闭自己
    public void startActivityAndFinishSelf(Class<?> cls){
        Intent intent = new Intent ( this, cls );
        startActivity ( intent );
        finish ();
    }
    @Override
    protected void onCreate(Bundle savedInstancesState){
        super.onCreate ( savedInstancesState );
        sp = getSharedPreferences ( "config", MODE_PRIVATE );
        //把设置布局注释掉，布局由具体的子类加载，抽象类不加载
        //setContentView(R.layout.activity_base_set_up);
        mGestureDetector = new GestureDetector ( this,new GestureDetector.SimpleOnGestureListener (){
            // e1 代表手指第一次触摸屏幕的事件
            // e2 代表手指离开屏幕一瞬间的事件
            // velocityX 水平方向的速度 单位 pix/s
            // velocityY 竖直方向的速度

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                if (Math.abs ( velocityX )<200){
                    Toast.makeText ( getApplicationContext (),
                            "无效动作，移动太慢",Toast.LENGTH_SHORT).show ();
                    return true;
                }
                if ((e2.getRawX() - e1.getRawX ())>200){
                    // 从左向右滑动屏幕，显示上一个界面
                    showPre ();
                    overridePendingTransition ( R.anim.pre_in,
                            R.anim.pre_out);
                    return true;
                }
                if ((e1.getRawX () - e2.getRawX ())>200){
                    // 从右向左滑动屏幕，显示下一个界面
                    showNext ();
                    overridePendingTransition ( R.anim.next_in,
                            R.anim.next_out);
                    return true;
                }
                return super.onFling ( e1, e2, velocityX, velocityY );
            }
        } );
    }
}
```

activity_setup_1.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="100"
        android:orientation="vertical">
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="160dp"
            android:background="@color/purple">
            <ImageView
                android:id="@+id/imgv"
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_centerInParent="true"
                android:background="@drawable/add"/>
            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_below="@+id/imgv"
                android:layout_marginTop="10dp"
                android:gravity="center"
                android:text="手机防盗向导"
                android:textColor="@android:color/white"
                android:textSize="18sp"/>

        </RelativeLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@android:color/white"
            android:orientation="vertical">
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="0dp"
                android:layout_gravity="center"
                android:layout_weight="1"
                android:gravity="center_vertical"
                android:orientation="horizontal">
                <TextView
                    style="@style/textview16sp"
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:drawablePadding="5dp"
                    android:drawableTop="@drawable/sim_alarm_icon"
                    android:gravity="center"
                    android:text="SIM卡变更报警"
                    android:textColor="@color/purple"/>
                <TextView
                    style="@style/textview16sp"
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:drawablePadding="5dp"
                    android:drawableTop="@drawable/location_icon"
                    android:gravity="center"
                    android:text="GPS追踪"
                    android:textColor="@color/purple"/>

            </LinearLayout>
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="0dp"
                android:layout_gravity="center"
                android:layout_weight="1"
                android:gravity="center_vertical"
                android:orientation="horizontal">
                <TextView
                    style="@style/textview16sp"
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:drawablePadding="5dp"
                    android:drawableTop="@drawable/lock_screen_icon"
                    android:gravity="center"
                    android:text="远程锁屏"
                    android:textColor="@color/purple"/>
                <TextView
                    style="@style/textview16sp"
                    android:layout_width="0dp"
                    android:layout_weight="1"
                    android:drawablePadding="5dp"
                    android:drawableTop="@drawable/delete_data"
                    android:gravity="center"
                    android:text="远程删除数据"
                    android:textColor="@color/purple"/>
            </LinearLayout>

        </LinearLayout>

    </LinearLayout>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="55dp"
        android:layout_weight="10"
        android:background="@color/purple"
        android:gravity="center">
        <include layout="@layout/setup_radiogroup"/>
    </LinearLayout>
</LinearLayout>
```

Setup1Activity.java
```
public class Setup1Activity extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_setup_1 );
        ((RadioButton)findViewById ( R.id.rb_first )).setChecked ( true );
    }
    @Override
    public void showNext(){
        startActivityAndFinishSelf ( Setup2Activity.class );
    }
    @Override
    public void showPre(){
        Toast.makeText ( this, "当前页面已经是第一页", Toast.LENGTH_LONG ).show ();
    }
}
```

drawable/sim_bind_selector.xml
```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:drawable="@drawable/add" android:state_pressed="true"/>
    <item android:drawable="@drawable/add" android:state_enabled="false"/>
    <item android:drawable="@drawable/add"/>

</selector>
```

layout/activity_setup_2.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="100"
        android:background="@android:color/white"
        android:orientation="vertical">
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="160dp"
            android:background="@color/purple">
            <ImageView
                android:id="@+id/imgv"
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_centerInParent="true"
                android:background="@drawable/add"/>
            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_below="@+id/imgv"
                android:layout_marginTop="10dp"
                android:gravity="center"
                android:text="SIM卡绑定"
                android:textColor="@android:color/white"
                android:textSize="18sp"/>
        </RelativeLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="4"
            android:gravity="center_vertical"
            android:orientation="horizontal">
            <ImageView
                android:layout_width="60dp"
                android:layout_height="60dp"
                android:layout_gravity="center_vertical|left"
                android:layout_marginLeft="15dp"
                android:background="@drawable/recomand_icon"/>
            <TextView
                style="@style/textview16sp"
                android:layout_marginLeft="15dp"
                android:layout_marginRight="15dp"
                android:lineSpacingMultiplier="1.5"
                android:text="绑定SIM卡后，当再次重启手机时，若SIM卡信息发生变化，手机卫士会自动发送报警短信给安全号码！"
                android:textColor="@color/purple"
                android:textScaleX="1.1"/>

        </LinearLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="1"
            android:gravity="center">
            <Button
                android:id="@+id/btn_bind_sim"
                android:layout_width="180dp"
                android:layout_height="45dp"
                android:layout_gravity="center_horizontal"
                android:layout_marginBottom="20dp"
                android:background="@drawable/sim_bind_selector"
                android:gravity="center"/>

        </LinearLayout>

    </LinearLayout>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="55dp"
        android:layout_weight="10"
        android:background="@color/purple"
        android:gravity="center">
        <include layout="@layout/setup_radiogroup"/>

    </LinearLayout>
</LinearLayout>
```

Setup2Activity.java
```
public class Setup2Activity extends BaseSetUpActivity{
    @Override
    public void showNext() {
         startActivityAndFinishSelf(Setup3Activity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup1Activity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_2);
        //设置第2个圆点的颜色
        ((RadioButton)findViewById(R.id.rb_second)).setChecked(true);
    }
}
```

layout/activity_setup_3.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="100"
        android:background="@android:color/white"
        android:orientation="vertical">
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="160dp"
            android:background="@color/purple">
            <ImageView
                android:id="@+id/imgv"
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_centerInParent="true"
                android:background="@drawable/add"/>
            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_below="@+id/imgv"
                android:layout_marginTop="10dp"
                android:gravity="center"
                android:text="选择安全联系人"
                android:textColor="@android:color/white"
                android:textSize="18sp"/>

        </RelativeLayout>
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="55dp"
            android:layout_margin="15dp"
            android:gravity="center_vertical"
            android:orientation="horizontal">
            <EditText
                android:id="@+id/et_inputphone"
                android:layout_width="match_parent"
                android:layout_height="50dp"
                android:layout_weight="5"
                android:background="@drawable/coner_white_rec"
                android:drawableLeft="@drawable/contact_et_left_icon"
                android:hint="请输入安全号码"
                android:inputType="phone"
                android:textColorHint="@color/purple"/>
            <Button
                android:id="@+id/btn_addcontact"
                android:layout_width="55dp"
                android:layout_height="45dp"
                android:layout_marginLeft="10dp"
                android:layout_weight="1"
                android:background="@drawable/add"/>

        </LinearLayout>

    </LinearLayout>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="55dp"
        android:layout_weight="10"
        android:background="@color/purple"
        android:gravity="center">
        <include layout="@layout/setup_radiogroup"/>
    </LinearLayout>

</LinearLayout>
```

Setup3Activity.java
```
public class Setup3Activity extends BaseSetUpActivity{
    @Override
    public void showNext() {
        startActivityAndFinishSelf(Setup4Activity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup2Activity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_3);
        //设置第3个小圆点的颜色
        ((RadioButton)findViewById(R.id.rb_third)).setChecked(true);
    }
}

```

layout/activity_setup_4.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_weight="100"
        android:orientation="vertical">
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="4"
            android:background="@color/purple">
            <ImageView
                android:id="@+id/imgv"
                android:layout_width="80dp"
                android:layout_height="80dp"
                android:layout_centerInParent="true"
                android:background="@drawable/add"/>
            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_below="@+id/imgv"
                android:layout_marginTop="20dp"
                android:gravity="center"
                android:text="恭喜 ！设置完成"
                android:textColor="@android:color/white"
                android:textScaleX="1.1"
                android:textSize="18sp"/>

        </RelativeLayout>
        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:background="@android:color/white">
            <TextView
                android:id="@+id/tv_setup4_status"
                style="@style/textview16sp"
                android:layout_centerVertical="true"
                android:layout_marginLeft="10dp"
                android:drawableLeft="@drawable/security_phone"
                android:drawablePadding="10dp"
                android:textColor="@color/purple"/>
            <ToggleButton
                android:id="@+id/togglebtn_securityfunction"
                android:layout_width="70dp"
                android:layout_height="30dp"
                android:layout_alignParentRight="true"
                android:layout_centerVertical="true"
                android:layout_marginRight="27dp"
                android:background="@drawable/toggle_btn_bg_selector"
                android:textOff=""
                android:textOn=""/>

        </RelativeLayout>
    </LinearLayout>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="55dp"
        android:layout_weight="10"
        android:background="@color/purple"
        android:gravity="center">
        <include layout="@layout/setup_radiogroup"/>

    </LinearLayout>

</LinearLayout>
```

Setup4Activity.java
```
public class Setup4Activity extends BaseSetUpActivity{
    @Override
    public void showNext() {
        startActivityAndFinishSelf(LostFindActivity.class);
    }

    @Override
    public void showPre() {
        startActivityAndFinishSelf(Setup3Activity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_4);
        //设置第4个小圆点的颜色
        ((RadioButton)findViewById(R.id.rb_four)).setChecked(true);
    }
}
```

AndroidManifest.xml
```
<activity android:name=".m2theftguard.LostFindActivity"/>
<activity android:name=".m2theftguard.Setup1Activity"/>
<activity android:name=".m2theftguard.Setup2Activity"/>
<activity android:name=".m2theftguard.Setup3Activity"/>
<activity android:name=".m2theftguard.Setup4Activity"/>
```

## 效果

点击手机防盗按钮，输入密码，进入界面

![1](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180805221117.png)
![2](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180805221134.png)
![3](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180805221145.png)
![4](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180805221155.png)

## 手机防盗界面SIM卡绑定和设置安全联系人的业务逻辑实现

1 SIM卡绑定界面以及sim变更的处理逻辑
```
1.1 清单文件增加READ_PHONE_STATE权限
1.2 编写Setup2Activity的逻辑代码
1.3 在根包(cn.edu.gdmec.mobileguard)上创建App继承于Application
1.4 清单文件上配置App
1.5 创建m2theftguard/receiver包，在里面new-other-broadcast receiver名称为 BootCompleteReceiver，创建广播接受者，并编写代码。
1.6 清单文件配置receiver的信使过滤器，并且配置接受启动消息的权限。
1.7 清单文件增加SEND_SMS权限
```
2 设置安全选择联系人
```
2.1 图片
2.1.1contact_icon.png
2.1.2back.png
2.2 编写Setup3Activity
2.3 编写联系人条目布局文件item_list_contact_select.xml
2.4 values/style.xml 增加字体样式
2.5 创建m2theftguard/entity包，创建ContactInfo类
2.6 在m2theftguard/utils包下面，创建ContactInfoParser类，获取联系人信息。
2.7 创建m2theftguard/adapter包，创建ContactAdapter类继承于BaseAdapter，联系人列表的适配器
2.8 创建ContactSelectActivity
2.9 编写联系人列表布局文件activity_contact_select.xml
2.10 清单文件增加 READ_CONTACT权限 
```
3 UI测试点
```
3.1 不绑定sim无法下一步
3.2 点击『绑定SIM卡』后出现『SIM卡已绑定』
在『选择安全联系人』界面，点击加号可弹出当前设备中的联系人，选择联系人之后，可把联系人的电话记录到安全号码输入框中。
3.4 重启android模拟器之后，自动发送短信到安全联系人的号码
```

## 效果

![40](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180815005133.png)

## 结构图

![32](http://images.cnblogs.com/cnblogs_com/dashucoding/1260072/o_QQ%E6%88%AA%E5%9B%BE20180817152158.png)

清单文件修改

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="cn.edu.gdmec.android.dashumobile">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<!--手机防盗界面SIM卡绑定和设置安全联系人的业务逻辑实现-->
    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
    <uses-permission android:name="android.permission.READ_CONTACTS"/>
    <uses-permission android:name="android.permission.SEND_SMS"/>

    <application
        android:name=".App"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".SplashActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".m1home.HomeActivity"></activity>
        <activity android:name=".m2theftguard.LostFindActivity"/>
        <activity android:name=".m2theftguard.Setup1Activity"/>
        <activity android:name=".m2theftguard.Setup2Activity"/>
        <activity android:name=".m2theftguard.Setup3Activity"/>
        <activity android:name=".m2theftguard.Setup4Activity"/>
        <!--手机防盗界面SIM卡绑定和设置安全联系人的业务逻辑实现-->
        <receiver android:name=".m2theftguard.receiver.BootCompleteReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED"/>
            </intent-filter>
        </receiver>
        <activity android:name=".m2theftguard.ContactSelectActivity"></activity>
    </application>

</manifest>
```

Setup2Activity.java
```
public class Setup2Activity extends BaseSetUpActivity implements View.OnClickListener{
    private TelephonyManager mTelephonyManager;
    private Button mBindSIMBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_setup_2 );
        // 设置第2个小圆点的颜色
        ((RadioButton ) findViewById ( R.id.rb_second )).setChecked ( true );
        //获取电话管理器这个系统服务
        mTelephonyManager = (TelephonyManager) getSystemService ( TELEPHONY_SERVICE );
        //找到布局中的『sim卡绑定』按钮
        mBindSIMBtn = (Button) findViewById ( R.id.btn_bind_sim );
        mBindSIMBtn.setOnClickListener ( this );
        if (isBind()){
            mBindSIMBtn.setEnabled ( false );
        }else{
            mBindSIMBtn.setEnabled ( true );
        }
    }
    private boolean isBind(){
        //sp是父类BaseSetupActivity的属性，是SharedPreference，按ctrl+鼠标左键就能跳转到声明的位置
        String simString = sp.getString ( "sim", null );
        if (TextUtils.isEmpty ( simString )){
            return false;
        }
        return true;
    }
    @Override
    public void showNext(){
        if (!isBind ()){
            Toast.makeText ( this, "您还没有绑定SIM卡！", Toast.LENGTH_LONG ).show ();
            return;
        }
        startActivityAndFinishSelf ( Setup3Activity.class );
    }
    @Override
    public void showPre(){
        startActivityAndFinishSelf ( Setup1Activity.class );
    }
    @Override
    public void onClick(View view){
        switch (view.getId ()){
            case R.id.btn_bind_sim:
                // 绑定SIM卡
                bindSIM();
                break;
        }
    }

    //绑定sim卡
    private void bindSIM() {
        if (!isBind ()){
            //使用电话管理器服务来获取sim卡号
            @SuppressLint("MissingPermission") String simSerialNumber = mTelephonyManager.getSimSerialNumber ();
            //存储sim卡号
            SharedPreferences.Editor edit = sp.edit ();
            edit.putString ( "sim", simSerialNumber );
            edit.commit ();
            Toast.makeText ( this, "SIM卡绑定成功！", Toast.LENGTH_LONG ).show ();
            mBindSIMBtn.setEnabled ( false );
        }else{
            // 已经绑定，提醒用户
            Toast.makeText ( this, "SIM卡已经绑定！", Toast.LENGTH_LONG ).show ();
            mBindSIMBtn.setEnabled ( false );
        }
    }
}
```

App.java

```
public class App extends Application {

    @Override
    public void onCreate(){
        super.onCreate ();
     /*   //更新apk
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder (  );
            StrictMode.setVmPolicy ( builder.build () );
        }*/

        correctSIM();
    }

    public void correctSIM() {
        // 检查sim卡是否发生变化
        SharedPreferences sp = getSharedPreferences ( "config",
                Context.MODE_PRIVATE);
        // 获取防盗保护的状态
        boolean protecting = sp.getBoolean ( "protecting", true );
        if (protecting){
            // 得到绑定的sim卡串号
            String bindsim = sp.getString ( "sim", "" );
            // 得到手机现在的sim卡串号
            TelephonyManager tm = (TelephonyManager) getSystemService ( Context.TELEPHONY_SERVICE );
            // 为了测试在手机序列号上data 已模拟SIM卡被更换的情况
            String realsim = tm.getSimSerialNumber ();
            //因为虚拟机无法更换sim卡，所以使用虚拟机测试要有此代码，真机测试要注释这段代码。
            //realsim="999";
            //realsim = "999";
            if (bindsim.equals ( realsim )){
                Log.i ( "", "sim卡未发生变化，还是您的手机" );
            }else {
                Log.i ( "", "SIM卡变化了" );
                // 由于系统版本的原因，这里的发短信可能与其他手机版本不兼容
                String safenumber = sp.getString ( "safephone", "" );
                if (!TextUtils.isEmpty ( safenumber )){
                    SmsManager smsManager = SmsManager.getDefault ();
                    smsManager.sendTextMessage(safenumber, null,
                            "你的亲友手机的SIM卡已经被更换！", null, null);
                }
            }
        }
    }
}
```
BootCompleteReceiver.java
```
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ((App) (context.getApplicationContext())).correctSIM();
    }
}
```
Setup3Activity.java

```
public class Setup3Activity extends BaseSetUpActivity implements View.OnClickListener{
    private EditText mInputPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_setup_3 );
        // 设置第3个小圆点的颜色
        ((RadioButton )findViewById ( R.id.rb_third )).setChecked ( true );

        //((RadioButton )findViewById ( R.id.rb_third )).setChecked ( true );
        findViewById ( R.id.btn_addcontact ).setOnClickListener ( this );
        mInputPhone = (EditText)findViewById ( R.id.et_inputphone );
        String safephone = sp.getString ( "safephone", null );
        if (!TextUtils.isEmpty ( safephone )){
            mInputPhone.setText ( safephone );
        }
    }
    @Override
    public void showNext(){
        //判断文本输入框中是否有电话号码
        String safePhone = mInputPhone.getText ().toString ().trim ();
        if (TextUtils.isEmpty ( safePhone )){
            Toast.makeText ( this, "请输入安全号码", Toast.LENGTH_LONG ).show ();
            return;
        }
        SharedPreferences.Editor edit = sp.edit ();
        edit.putString ( "safephone", safePhone );
        edit.commit ();
        startActivityAndFinishSelf ( Setup4Activity.class );
    }
    @Override
    public void showPre(){
        startActivityAndFinishSelf ( Setup2Activity.class );
    }
    @Override
    public void onClick(View view){
        switch (view.getId ()){
            case R.id.btn_addcontact:
                //启动联系人选择activity并获取返回值
                startActivityForResult ( new Intent( this,ContactSelectActivity.class ), 0 );
                break;
        }
    }
    //获取被调用的activity的返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult ( requestCode, resultCode, data );
        if (data!=null){
            String phone = data.getStringExtra ( "phone" );
            mInputPhone.setText ( phone );
        }
    }
}
```
item_list_contact_select.xml
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#10000000"
    android:orientation="vertical">
    <View
        android:id="@+id/view1"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:layout_centerVertical="true"
        android:layout_margin="15dp"
        android:background="@drawable/contact_icon"/>
    <TextView
        android:id="@+id/tv_name"
        style="@style/textview16sp"
        android:layout_alignTop="@+id/view1"
        android:layout_toRightOf="@+id/view1"
        android:textColor="@color/purple"/>
    <TextView
        android:id="@+id/tv_phone"
        style="@style/textview14sp"
        android:layout_toRightOf="@+id/view1"
        android:layout_below="@+id/tv_name"
        android:layout_marginTop="10dp"
        android:textColor="@color/purple"/>
</RelativeLayout>
```
values/style.xml
```
<!--TextView 14sp-->
    <style name="textview14sp" parent="wrapcontent">
        <item name="android:textSize">14sp</item>
        <item name="android:gravity">center_vertical</item>
    </style>
</resources>
```
ContactInfo.java
```
public class ContactInfo {
    public String id;
    public String name;
    public String phone;
}
```

ContactInfoParser.java
```
public class ContactInfoParser {
    public static List<ContactInfo> getSystemContact(Context context){
        //获取内容解析者
        ContentResolver resolver = context.getContentResolver ();

        // 1. 查询raw_contacts表，把联系人的id取出来
        Uri uri = Uri.parse ( "content://com.android.contacts/raw_contacts" );
        Uri datauri = Uri.parse ( "content://com.android.contacts/data" );
        List<ContactInfo> infos = new ArrayList<ContactInfo>(  );
        Cursor cursor = resolver.query ( uri, new String[]{"contact_id"}, null, null, null );
        while (cursor.moveToNext ()){
            String id = cursor.getString ( 0 );
            if (id != null){
                System.out.println ("联系人id:"+id);
                ContactInfo info = new ContactInfo ();
                info.id = id;
                // 2. 根据联系人的id，查询data表，把这个id的数据取出来
                // 系统api 查询data表的时候 不是真正的查询data表 而是查询的data表的视图
                Cursor dataCursor = resolver.query ( datauri, new String[]{
                                "data1", "mimetype"},"raw_contact_id=?",
                        new String[]{id}, null);
                while (dataCursor.moveToNext ()){
                    String data1 = dataCursor.getString ( 0 );
                    String mimetype = dataCursor.getString ( 1 );
                    if ("vnd.android.cursor.item/name".equals ( mimetype )){
                        System.out.println ("姓名="+data1);
                        info.name = data1;
                    }else if ("vnd.android.cursor.item/phone_v2".equals ( mimetype )){
                        System.out.print ( "电话="+data1 );
                        info.phone = data1;
                    }
                }
                //如果姓名和手机都为空，则跳过该条数据
                if (TextUtils.isEmpty ( info.name ) && TextUtils.isEmpty ( info.phone ))
                    continue;
                infos.add ( info );
                dataCursor.close ();
            }
        }
        cursor.close ();
        return infos;
    }
    public static List<ContactInfo> getSimContacts(Context context) {
        Uri uri=Uri.parse ( "content://icc/adn" );
        List<ContactInfo> infos=new ArrayList<ContactInfo> ();
        Cursor mCursor=context.getContentResolver ().query ( uri, null, null, null, null );
        if (mCursor != null) {
            while (mCursor.moveToNext ()) {
                ContactInfo info=new ContactInfo ();
                // 取得联系人名字
                int nameFieldColumnIndex=mCursor.getColumnIndex ( "name" );
                info.name=mCursor.getString ( nameFieldColumnIndex );
                // 取得电话号码
                int numberFieldColumnIndex=mCursor.getColumnIndex ( "number" );
                info.phone=mCursor.getString ( numberFieldColumnIndex );
                infos.add ( info );
            }
        }
        mCursor.close ();
        return infos;
    }
}
```

ContactAdapter.java
```
public class ContactAdapter extends BaseAdapter {
    private List<ContactInfo> contactInfos;
    private Context context;
    public ContactAdapter(List<ContactInfo> contactInfos, Context context){
        super();
        this.contactInfos = contactInfos;
        this.context = context;
    }
    @Override
    public int getCount(){
        return contactInfos.size ();
    }
    @Override
    public Object getItem(int i){
        return contactInfos.get ( i );
    }
    @Override
    public long getItemId(int i){
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        ViewHolder holder = null;
        if (view == null){
            view = View.inflate ( context, R.layout.item_list_contact_select, null );
            holder = new ViewHolder();
            holder.mNameTV = (TextView)view.findViewById ( R.id.tv_name );
            holder.mPhoneTV = (TextView)view.findViewById ( R.id.tv_phone );
            view.setTag ( holder );
        }else {
            holder = (ViewHolder)view.getTag ();
        }
        holder.mNameTV.setText(contactInfos.get ( i ).name);
        holder.mPhoneTV.setText(contactInfos.get ( i ).phone);
        return view;
    }
    static class ViewHolder{
        TextView mNameTV;
        TextView mPhoneTV;
    }
}
```

ContactSelectActivity.java
```
public class ContactSelectActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView mListView;
    private ContactAdapter adapter;
    private List<ContactInfo> systemContacts;
    Handler mHandler = new Handler(  ){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 10:
                    if (systemContacts != null){
                        adapter = new ContactAdapter ( systemContacts,ContactSelectActivity.this );
                        mListView.setAdapter ( adapter );
                    }
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_contact_select );
        initView();
    }
    private void initView(){
        ((TextView) findViewById ( R.id.tv_title )).setText ( "选择联系人" );
        //补坑
        //findViewById ( R.id.rl_titlebar ).setBackgroundColor ( getResources ().getColor ( R.color.bright_purple ) );
        ImageView mLeftImgv = (ImageView)findViewById ( R.id.imgv_leftbtn );
        mLeftImgv.setOnClickListener ( this );
        mLeftImgv.setImageResource ( R.drawable.back );
        //设置导航栏颜色
        findViewById ( R.id.rl_titlebar ).setBackgroundColor ( getResources ().getColor ( R.color.purple ) );
        mListView = (ListView) findViewById ( R.id.lv_contact );
        new Thread (  ){
            public void run(){
                systemContacts = ContactInfoParser.getSystemContact ( ContactSelectActivity.this );
                systemContacts.addAll ( ContactInfoParser.getSimContacts ( ContactSelectActivity.this ) );
                mHandler.sendEmptyMessage ( 10 );
            };
        }.start ();
        mListView.setOnItemClickListener ( new AdapterView.OnItemClickListener (){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactInfo item=( ContactInfo ) adapter.getItem ( position );
                Intent intent=new Intent ();
                intent.putExtra ( "phone", item.phone );
                //补坑
                //intent.putExtra ( "name", item.name );
                setResult ( 0, intent );
                finish ();
            }
        } );
    }
    @Override
    public void onClick(View view){
        switch (view.getId ()){
            case R.id.imgv_leftbtn:
                finish ();
                break;
        }
    }
}
```
activity_contact_select.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <include layout="@layout/titlebar"/>
    <ListView
        android:id="@+id/lv_contact"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:cacheColorHint="#00000000"
        android:divider="#FFFFFF"
        android:dividerHeight="1dp"/>

</LinearLayout>
```
图片back.png,contact_icon.png,布局activity_contact_select.xml,item_list_contact_select.xml

## 手机防盗界面完善和短信防盗指令功能实现

1.完善手机防盗设置界面逻辑

- 1.1.编写Setup4Activity.java
- 1.2.编写LostFindActivity.java

2.编写短信防盗指令功能实现

- 2.1.创建raw资源目录：并把ylzs.mp3粘贴进去
- 2.2.创建xml资源目录：创建device_admin_sample.xml
- 2.3.创建m2theftguard/service包，创建GPSLocationService服务
- 2.4.编写广播接收者
- 2.4.1.MyDeviceAdminReceiver 成为系统管理器
- 2.4.2.SmsLostFindReceiver  监听防盗指令短信

3.修改HomeActivity.java激活系统管理器

测试点：

4.1.第一次启动提示『激活此设备管理员』

4.2.再次启动app，进入主界面，选择『手机防盗』，设置密码对话框，输入密码对话框，进入Setup1Activity，左划，进入Setup2Activity，点击『sim卡绑定』按钮，左划，进入Setup3Activity，输入安全联系人电话号码，左划进入Setup4Activity，点击防盗开启的开关按钮，点击后出现，『防盗没有开启』和『防盗已经开启』

4.3.再次启动app，进入『手机防盗』，输入密码后，出现『防盗保护已开启』，点击『重新进入向导』，出现『手机防盗向导』



