package com.gclue.kadai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class KadaiMainActivity extends Activity implements SensorEventListener,
		LocationListener {
	private MyView mView;
//	private CameraView mCamera;
	private boolean mRegisteredSensor;	//追加
	// Sensor Manager
	private SensorManager mSensorManager = null;
	private LocationManager lm;	//追加
	// Location Manager
	private LocationManager mLocationManager = null;
	private LinearLayout.LayoutParams arLayoutParams;
	private WebView webView;	//追加
	private LinearLayout arLayout;	//追加
	String dirtext;
	String[ ]a = {"S","南西","W","北西","N","北東","E","南東"};
	
	String yawtext;
	String yawtext1;
	String[ ]b = {"N","北東","E","南東","S","南西","W","北西"};
	
	String goal;
	float distance;
//	int yawqua;
	
	private GestureDetector mGDetector = null;
	private KadaiARView mARView = null;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Notification Barを消す
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Title Barを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CameraView mCamera = new CameraView(this);
		setContentView(mCamera);

		// クラスのインスタンスを生成
		mView = new MyView(this);
		// Vewに設定
//		setContentView(mView);
		addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT,+LayoutParams.WRAP_CONTENT));
		
		mCamera.setView(mView);
		
		// SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mRegisteredSensor = false;	//追加
		
//		if (sensors.size() > 0) {
//			Sensor s = sensors.get(0);
//			sensorManager.registerListener(this, s,
//			SensorManager.SENSOR_DELAY_UI);
//			Log.v("OnResume", "PASS");
//			if (locman != null){
//			locman.removeUpdates(this);
//			locman.isProviderEnabled(LocationManager.GPS_PROVIDER);
//			locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
//			location = locman.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//			}else{
//			Toast.makeText(this, "failed to get location from GPS", Toast.LENGTH_LONG).show();
//			}
//			if (locman.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//			locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
//			location = locman.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			} else {
//			Toast.makeText(this, "failed to get location from NETWORK", Toast.LENGTH_LONG).show();
//			}
//			super.onResume();
//			}
//			}
		// LocationManagerでGPSの値を取得するための設定
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 値が変化した際に呼び出されるリスナーの追加
			
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
			
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mView.setGps("" + location.getLatitude(), "" + location.getLongitude());

		//石巻工業
//		Location apiopostLocation = new Location("apiopost");
//		apiopostLocation.setLatitude(38.43792);
//		apiopostLocation.setLongitude(141.287221);
		// アピオ郵便局座標
//		apiopostLocation.setLatitude(37.52144);
//		apiopostLocation.setLongitude(139.916199);
		//自宅
		Location apiopostLocation = new Location("apiopost");
		apiopostLocation.setLatitude(38.423455);
		apiopostLocation.setLongitude(140.989182);
		//イトナブ
//		Location apiopostLocation = new Location("apiopost");
//		apiopostLocation.setLatitude(38.431619);
//		apiopostLocation.setLongitude(141.309406);
		//アエル
//		Location apiopostLocation = new Location("apiopost");
//		apiopostLocation.setLatitude(38.26229);
//		apiopostLocation.setLongitude(140.881017);

		float distance = location.distanceTo(apiopostLocation);
		mView.setDistance(distance);
		
		mView.setGoal(""+ goal);
		if (distance < 20){
			goal="登場";			
//		バイブレータを動作させる
//			vibrator.vibrate(1000);
		}else{
			goal="隠れているのは・・・";
		}
		
		float direction = location.bearingTo(apiopostLocation);
		mView.setDirection(direction);

		mView.setDirtext("" + dirtext);
		
		if (direction <= -157.5) {
			dirtext=a[0];
		} else if (direction > -157.5 && direction < -112.5) {
			dirtext = a[1];
		} else if (direction >= -112.5 && direction <= -67.5) {
			dirtext = a[2];
		} else if (direction > -67.5 && direction < -22.5) {
			dirtext = a[3];
		} else if (direction >= -22.5 && direction <= 22.5) {
			dirtext = a[4];
		} else if (direction > 22.5 && direction < 67.5) {
			dirtext = a[5];
		} else if (direction >= 67.5 && direction <= 112.5) {
			dirtext = a[6];
		} else if (direction > 112.5 && direction < 157.5) {
			dirtext = a[7];
		} else if (direction >= 157.5) {
			dirtext = a[0];
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		// TODO Auto-generated method stub
//		Log.i("SURFACE","SensorChanged()");
		if(sensorEvent.sensor.getType()==Sensor.TYPE_ORIENTATION){
//			Log.i("SURFACE","yaw"+sensorEvent.values[0]);
//			Log.i("SURFACE","pitch"+sensorEvent.values[1]);
//			Log.i("SURFACE","roll"+sensorEvent.values[2]);
			mView.setOrientation(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);
//			//WebViewの位置を移動
//			arLayoutParams.setMargins((int)sensorEvent.values[0]*10, 100, 10, 10);
//			//Layoutを更新
//			arLayout.updateViewLayout(webView, arLayoutParams);
		}else if (sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
//			mView.onAcclerometerChanged((int)sensorEvent.values[0],(int)sensorEvent.values[1],(int)sensorEvent.values[2]);
		}
			if(sensorEvent.values[0] <= 22.5 ){
//				yawqua = 0;
				yawtext = b[0];
				yawtext1 = b[1];
			}
			else if(sensorEvent.values[0] >22.5 && sensorEvent.values[0] < 67.5){
				yawtext = b[1];
				yawtext1 = b[2];
				Log.i("SUEFACE", "yaw"+sensorEvent.values[0]);
			}
			else if(sensorEvent.values[0] >=67.5 && sensorEvent.values[0] <= 112.5){
				yawtext = b[2];
				yawtext1 = b[3];
			}
			else if(sensorEvent.values[0] >112.5 && sensorEvent.values[0] < 157.5){
				yawtext = b[3];
				yawtext1 = b[4];
			}
			else if(sensorEvent.values[0] >= 157.5 && sensorEvent.values[0] <= 202.5 ){
				yawtext = b[4];
				yawtext1 = b[5];
			}
			else if(sensorEvent.values[0] >202.5 && sensorEvent.values[0] < 247.5){
				yawtext = b[5];
				yawtext1 = b[6];
			}
			else if(sensorEvent.values[0] > 247.5 && sensorEvent.values[0] < 292.5 ){
				yawtext = b[6];
				yawtext1 = b[7];
			}
			else if(sensorEvent.values[0] >292.5 && sensorEvent.values[0] < 337.5){
				yawtext = b[7];
				yawtext1 = b[0];
			}
			else if(sensorEvent.values[0] >= 337.5){
				yawtext = b[0];
				yawtext1 = b[1];
			}
			mView.setOrientation("" + yawtext);
	}		
	@Override	//追加
	protected void onResume(){
		super.onResume();
		
	// Sensorの取得とリスナーへの登録
		List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			mSensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_FASTEST);
		}	
		
		List< Sensor >sensors_accelerometer = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		
		if(sensors_accelerometer.size() > 0){
			Sensor sensor_accelerometer = sensors_accelerometer.get(0);
			mRegisteredSensor = mSensorManager.registerListener(this, sensor_accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		}
	}
	
	@Override	//追加
	protected void onStop(){
		super.onStop();
	}
	
	public void onDestroy() {
		super.onDestroy();
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
		}
		if (mLocationManager != null) {
			mLocationManager.removeUpdates(this);
		}
	}
	
}
/**
 * CameraView
 */
class CameraView extends SurfaceView implements SurfaceHolder.Callback{
//	Context mContext;	//追加
	
	/**
	 * Cameraのインスタンスを格納する変数
	 */
	private Camera mCamera;
	private Context context;
	
	/**
	 * MyViewのインスタンスを格納する変数
	 */
	private View mView;
	CameraView(Context context){	//変更
		super(context);
		this.context = context;
//		mContext = context;	//追加
//		SurfaceHolder mHolder = getHolder();	//追加
//		mHolder.addCallback(this);	//追加
//		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);	//追加
		setDrawingCacheEnabled(true);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	/**
	 * MyViewを受け渡す
	 * @param mView MyView
	 */
	public void setView(View mView) {
		this.mView = mView;
	}
 
	
	/**
	 * Surfaceに変化があった場合に呼ばれる
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
		Log.i("CAMERA", "surfaceChaged");
		 
		//画面設定
		Camera.Parameters parameters =mCamera.getParameters();
//		parameters.setPreviewSize(width, height);
		List<Size> params = parameters.getSupportedPictureSizes();
		int wid = params.get(0).width;
		int hei = params.get(0).height;
		for (Size s:params){
			if(wid < s.width){
				wid = s.width;
				hei = s.height;
			}
		}
		List<Size> params2 = parameters.getSupportedPreviewSizes();
		int wid2 = params2.get(0).width;
		int hei2 = params2.get(0).height;
		for (Size s:params2){
			if(wid2 < s.width){
				wid = s.width;
				hei = s.height;
			}
		}
		mCamera.setDisplayOrientation(90);
		parameters.setPictureSize(wid, hei);
		parameters.setPreviewSize(wid2, hei2);
		mCamera.setParameters(parameters);
		
		//プレビュー表示を開始
		mCamera.startPreview();
	}
	/**
	 * Surfaceが生成された際に呼ばれる
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder){
		
		//カメラをOpen
		Log.i("DEBUG", "Camera open1");
		mCamera = Camera.open();
		Log.i("DEBUG", "Camera open2");

		try{
			mCamera.setPreviewDisplay(holder);
		}catch(Exception e){	//変更
//			mCamera.release();
			Log.i("DEBUG", "Camera open3");
//			mCamera = null;
		}
	}
	/**
	 * Surfaceが破棄された場合に呼ばれる
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder){
		Log.i("CAMERA", "surfaceDestroyed");
		
		//カメラをClose
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			setDrawingCacheEnabled(false);
			setDrawingCacheEnabled(true);
 
			
//			mCamera.takePicture(null, null, new Camera.PictureCallback() {
			mCamera.takePicture(new Camera.ShutterCallback() {
				@Override
				public void onShutter() {
				}
			}, null, new Camera.PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					Log.i("DEBUG", "onTouch");
					
					// プレビュー撮影を有効にする
					mView.setDrawingCacheEnabled(true);
					Bitmap viewBitmap = Bitmap.createBitmap(mView.getDrawingCache());
					// プレビュー撮影を無効にする
					mView.setDrawingCacheEnabled(false);
					
					// スケールを取得
					int scale = getScale(data, viewBitmap.getWidth(),viewBitmap.getHeight());
					
					// Bitmap生成時のオプションを作成
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = scale;
					
					// カメラ画像からスケールの値を設定した状態でカメラ画像をBitmap形式で格納
					Bitmap myBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
					
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
 
					// 取得した画像サイズにあうサイズのBitmapを作成(何も描画されていない)
//					Bitmap tmpBitmap = Bitmap.createBitmap(myBitmap.getWidth(),myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
					Bitmap tmpBitmap = Bitmap.createBitmap(myBitmap,0,0,myBitmap.getWidth(),myBitmap.getHeight(),matrix,true);
					
					// tmpBitmapからキャンバスを作成
					Canvas canvas = new Canvas(tmpBitmap);
 
					// 作成したCanvasにMyViewのプレビューとカメラの画像をはりつけ合成
//					canvas.drawBitmap(myBitmap,null,new Rect(0, 0, myBitmap.getWidth(), myBitmap.getHeight()), null);
					canvas.drawBitmap(viewBitmap, null, new Rect(0, 0,viewBitmap.getWidth(), viewBitmap.getHeight()),null);
					
					// sdカードに保存
					saveBitmapToSd(tmpBitmap);
	
					// プレビュー表示を再開
					mCamera.startPreview();
	
				}
			});
		}
		setDrawingCacheEnabled(false);
		return false;
	}
	/**
	 * スケールを取得する
	 * 
	 * @param data
	 *            縮尺を調査する画像データ
	 * @param width
	 *            変更する横のサイズ
	 * @param height
	 *            変更する縦のサイズ
	 * @return 変更したいサイズのスケールの値
	 */
	public int getScale(byte[] data, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
 
		int scaleW = options.outWidth / width + 1;
		int scaleH = options.outHeight / height + 1;
 
		int scale = Math.max(scaleW, scaleH);
		options.inJustDecodeBounds = false;
 
		return scale;
	}
	
	/**
	 * Bitmap画像をsdカードに保存
	 * @param mBitmap Bitmapデータ
	 */
	public void saveBitmapToSd(Bitmap mBitmap) {
	 try {
	 // sdcardフォルダを指定
//	 File root = Environment.getExternalStorageDirectory();
	File root = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/");

	 // 日付でファイル名を作成　
	 Date mDate = new Date();
	 SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

	 // 保存処理開始
	 FileOutputStream fos = null;
	 fos = new FileOutputStream(new File(root, fileName.format(mDate) + ".jpg"));

	 // jpegで保存
	 mBitmap.compress(CompressFormat.JPEG, 100, fos);
	 
	 
	 // 保存処理終了
	 fos.close();
	 
	 } 
	 catch (Exception e) {
	 Log.e("Error", "" + e.toString());
	 }
	 this.context.sendBroadcast(
			    new Intent(
			        Intent.ACTION_MEDIA_MOUNTED,
			        Uri.parse( "file://" + Environment.getExternalStorageDirectory() )
			    )
			);
	}
	
}

/**
 * オーバーレイ描画用のクラス
 */
class MyView extends View {
//	private int mDx;
//	private int mDy;
//	private int mDz;
	
//	private int mYaw;
//	private int mRoll;
//	private int mPitch;
	
//	private double mLat;
//	private double mLon;
	
//	private int mCurX;
//	private int mCurY;
	
	// Roll
	private float roll;
//	int mRoll = Integer.parseInt(roll);
	// Yaw
	private float yaw;
	// Pitch
	private float pitch;
	// YawText
	private String yawtext = "測定中";
	private String yawtext1;
	// Lat
	private String lat;
	// Lon
	private String lon;
	// distance
	private float distance;
	// direction
	private float direction;
	// dirText
	private String dirtext = "測定中";
	//gaol
	private String goal="Search Charactor";
	//画像を格納する変数
	private Bitmap myBitmap;
	//サウンド再生データを保持する。
	private MediaPlayer mp;
	
	// バイブレータオブジェクトを保持
	Vibrator vibrator;
	
//	private WebView webView;	//追加
//	private LinearLayout.LayoutParams arLayoutParams;
//	private LinearLayout arLayout;	//追加
	
	/**
	 * コンストラクタ
	 * 
	 * @param contextコンテキスト
	 */
	public MyView(Context context) {
		super(context);
		setFocusable(true);
		
		// バイブレータを用意する
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		
		//Resourceインスタンスの生成
		Resources res = this.getContext().getResources();
		//画像の読み込み(res/drawable/gclue_logo.gif)
		myBitmap = BitmapFactory.decodeResource(res,R.drawable.kuma);
		
		//サウンドデータを読み込む(res/raw/pon.mp3)
		mp = MediaPlayer.create(context, R.raw.powerup02 );	
		
//		//WebView（追加）
//		webView = new WebView(this);
//		webView.loadUrl("http://ishiko.myswan.ne.jp/");
//		//Web用のLayoutParams（追加）
//		arLayoutParams = new LinearLayout.LayoutParams(200,200);
//		arLayoutParams.setMargins(0,0,0,0);
//		//WebWiewを張り付けるLayout（追加）
//		arLayout = new LinearLayout(this);
//		arLayout.addView(myBitmap,arLayoutParams);
		//WebViewを張り付けたLayoutを画面に張り付け（追加）
//		addContentView(arLayout,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	/**
	 * 値を渡す
	 */
	public void setOrientation(float yaw, float pitch, float roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		invalidate();
	}
	/**
	 * 値を渡す
	 */
	public void setOrientation(String yawtext) {
		this.yawtext = yawtext;
		this.yawtext1= yawtext1;
		invalidate();
	}
	/**
	 * 値を渡す（GPS)
	 */
	public void setGps(String lat, String lon) {
		this.lat = lat;
		this.lon = lon;
		invalidate();
	}
	/**
	 * 値を渡す(distance)
	 */
	public void setDistance(float distance) {
		this.distance = distance;
		invalidate();
	}
	/**
	 * 値を渡す(Distance)
	 */
	public void setDirection(float direction) {
		this.direction = direction;
		invalidate();
	}
	/**
	 * 値を渡す(dirtext)
	 */
	public void setDirtext(String dirtext) {
		this.dirtext = dirtext;
		invalidate();
	}	
	/**
	 * 値を渡す(goal)
	 */
	public void setGoal(String goal) {
		this.goal = goal;
		invalidate();
	}

	/**
	 * 描画処理を行う
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// 背景色を設定
		if(goal.equals("登場")){
			canvas.drawColor(Color.TRANSPARENT);
		}else{
			canvas.drawColor(0xffccff44);
		}
		
		// 描画するための線の色を設定
		Paint mainPaint = new Paint();
		mainPaint.setStyle(Paint.Style.STROKE);
		mainPaint.setARGB(255, 0, 0, 0);
		mainPaint.setTextSize(35);
		mainPaint.setStrokeWidth(4);
		Paint subPaint = new Paint();
		subPaint.setStyle(Paint.Style.FILL);
		subPaint.setARGB(255, 255, 0, 0);
		subPaint.setTextSize(40);
		
		//Bitmapイメージの描画
		if(goal.equals("登場")){
		canvas.drawBitmap(myBitmap,100+(direction-(yaw-180))*5,100-pitch*5,mainPaint);
		
//		//WebViewの位置を移動
//		arLayoutParams.setMargins(100, 100, 10, 10);
//		//Layoutを更新
//		arLayout.updateViewLayout(webView, arLayoutParams);
//		vibrator.vibrate(1000);
		}
		
		//音の再生
		if(goal.equals("登場")){
			//音の再生開始位置を0ミリセカンドの位置に設定する
//			mp.seekTo(0);
//			音の再生を開始する
//			mp.start();
		}else{
			//音を停止する
			mp.stop();
			
			//一度再生をstop()してから再び音を再生する場合には、prepare()を呼び出す必要がある
			try {
				mp.prepare();
			} catch ( IllegalStateException e ) {
				e.printStackTrace();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		// 文字を描画
		if(goal.equals("登場")){
		
		canvas.drawText("くまもん！" + goal, 10, 900, subPaint);
		}else{
//		canvas.drawText("" + yaw, 10, 10, mainPaint);
//		canvas.drawText("" + roll, 10, 30, mainPaint);
//		canvas.drawText("" + pitch, 10, 50, mainPaint);
//		if (yawtext.equals("N") || yawtext.equals("E") || yawtext.equals("S") || yawtext.equals("W")){
		canvas.drawText("" + yawtext, 230, 120, mainPaint);
//		canvas.drawText(""+ yowtext1, 250, 159, mainPaint);
//		}
//		if (yawtext1.equals("N") || yawtext1.equals("E") || yawtext1.equals("S") || yawtext1.equals("W")){
//		canvas.drawText("" + yawtext1, 280, 170, mainPaint);
//		}
//		canvas.drawText("" + lat, 10, 100, mainPaint);
//		canvas.drawText("" + lon, 10, 120, mainPaint);

		canvas.drawText("目標までの距離:" + (int)distance +" m", 10, 600, mainPaint);
//		canvas.drawText("" + direction, 10, 180, mainPaint);

		canvas.drawText("目標の方向:" + dirtext, 10, 50, mainPaint);
		canvas.drawText("" + goal, 10, 800, mainPaint);
		
		// 円を描画
		canvas.drawCircle( 250, 300, 200, mainPaint );
		canvas.drawCircle( 250, 300, 100, mainPaint );
		canvas.drawRect(240, 290, 260, 310, mainPaint);
		canvas.drawLine(110, 160, 240, 290, mainPaint);
		canvas.drawLine(390, 160, 260, 290, mainPaint);
		canvas.drawCircle((-distance*(float)Math.sin(Math.toRadians(direction-(yaw-180))))/1+250, 
				(distance*(float)Math.cos(Math.toRadians(direction-(yaw-180))))/1+300, 10, subPaint);
		}
		
		//バイブレータを動作させる
		if(dirtext.equals(yawtext)){
		vibrator.vibrate(10);
		}
	}
//	public void onAcclerometerChanged(int dx,int dy,int dz){	//追加
//		mDx = dx;
//		mDy = dy;
//		mDz = dz;
//		invalidate();
//	}
//	public void onGpsChanged(double lat,double lon){
//		mLat = lat;
//		mLon = lon;
//		invalidate();
//	}
}