package jp.itnav.chara;

//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;

import android.location.Location;
import android.media.MediaPlayer;
//import android.net.Uri;
import android.os.Bundle;
//import android.os.Environment;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
//import android.graphics.Matrix;
import android.graphics.Paint;
//import android.graphics.Rect;
//import android.hardware.Camera;
//import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;
//import android.util.Log;
import android.view.Display;
//import android.view.MotionEvent;
import android.view.Surface;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
//import android.webkit.WebView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class KadaiMainActivity extends Activity implements SensorEventListener {
	private LocationClient locationClient = null;
	private LocationCallback locationCallback = new LocationCallback();
	private Location lastLocation;
	public static boolean isAppForeground = false;
	private Dialog errorDialog;

	private static final int LOCATION_UPDATES_INTERVAL = 20000; 
	// Setting 2sec interval for location updates
	private static final int ERROR_DIALOG_ON_CREATE_REQUEST_CODE = 4055;
	private static final int ERROR_DIALOG_ON_RESUME_REQUEST_CODE = 4056;

	private MyView mView;
	private boolean mRegisteredSensor;
	// Sensor Manager
	private SensorManager mSensorManager = null;
	private Sensor mAccelerometer; // 加速度センサ20130526
	private Sensor mMagneticField; // 磁気センサ20130526

	String dirtext;
	String[] a = { "S", "南西", "W", "北西", "N", "北東", "E", "南東" };

	String yawtext;
	// String yawtext1;
	// String[ ]b = {"N","北東","E","南東","S","南西","W","北西"};
	
	// イトナブ　大郷 アエル 北上　滝沢　石川　青山　六本木
	double[] c = { 38.431619, 38.423256, 38.271751, 39.305079, 39.800261,
			36.662764, 35.664625, 35.667338, 38.26229 };
	double[] d = { 141.309406, 140.989287, 140.87009, 141.119523, 141.137205,
			136.739514, 139.711681, 139.728584, 140.881017 };
	int param;

	String goal;
	float distance;
//	private Location location;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Notification Barを消す
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Title Barを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		checkGooglePlayServiceAvailability(ERROR_DIALOG_ON_CREATE_REQUEST_CODE);

		Intent intent = getIntent();
		param = intent.getIntExtra("ID", 0);

		// クラスのインスタンスを生成
		mView = new MyView(this, param);
		// Vewに設定
		addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				+LayoutParams.WRAP_CONTENT));

		// SensorManager
		this.mSensorManager = (SensorManager) this
				.getSystemService(SENSOR_SERVICE);// 20130526this追加
		this.mAccelerometer = this.mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 20130525
		this.mMagneticField = this.mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);// 20130526
		mRegisteredSensor = false; // 追加

	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	// 加速度センサの値
	private float[] mAccelerometerValue = new float[3];
	// 磁気センサの値
	private float[] mMagneticFieldValue = new float[3];
	// 磁気センサの更新がすんだか
	private boolean mValidMagneticFiled = false;

	@Override
	public void onSensorChanged(SensorEvent event) { // 20130526sensorEventをeventに変更
		// TODO Auto-generated method stub
		// センサーごとの処理
		switch (event.sensor.getType()) {
		// 加速度センサー
		case Sensor.TYPE_ACCELEROMETER:
			// cloneで配列がコピーできちゃうんだね。へえ
			this.mAccelerometerValue = event.values.clone();
			break;
		// 磁気センサー
		case Sensor.TYPE_MAGNETIC_FIELD:
			this.mMagneticFieldValue = event.values.clone();
			this.mValidMagneticFiled = true;
			break;
		}
		// 値が更新された角度を出す準備ができた
		if (this.mValidMagneticFiled) {
			// 方位を出すための変換行列
			float[] rotate = new float[16]; // 傾斜行列？
			float[] inclination = new float[16]; // 回転行列
			// うまいこと変換行列を作ってくれるらしい
			SensorManager.getRotationMatrix(rotate, inclination,
					this.mAccelerometerValue, this.mMagneticFieldValue);
			// 方向を求める
			float[] inorientation = new float[3];
			this.getOrientation(rotate, inorientation);

//			float[] outorientation = new float[3];

			// デグリー角に変換する
			float degreeDir = (float) Math.toDegrees(inorientation[0]);
			float degreeY = (float) Math.toDegrees(inorientation[1]);

			
			// event.valuesをdegreeDirに
			if (degreeDir <= -157.5) {
				yawtext = a[0];
				// yawtext1 = b[1];
				// Log.i("SUEFACE", "yaw"+event.values[0]);
			} else if (degreeDir > -157.5 && degreeDir < -112.5) {
				yawtext = a[1];
			} else if (degreeDir >= -112.5 && degreeDir <= -67.5) {
				yawtext = a[2];
			} else if (degreeDir > -67.5 && degreeDir < -22.5) {
				yawtext = a[3];
			} else if (degreeDir >= -22.5 && degreeDir <= 22.5) {
				yawtext = a[4];
			} else if (degreeDir > 22.5 && degreeDir < 67.5) {
				yawtext = a[5];
			} else if (degreeDir >= 67.5 && degreeDir <= 112.5) {
				yawtext = a[6];
			} else if (degreeDir > 112.5 && degreeDir < 157.5) {
				yawtext = a[7];
			} else if (degreeDir >= 157.5) {
				yawtext = a[0];
			}
			// Log.i("SensorCH", "yawtext"+yawtext);
			mView.setYaw("" + yawtext);
			mView.setdegree(degreeDir);
			mView.setdegreeY(degreeY);
		}
	}

	// ////////////////////////////////////////////////////////////
	// 画面が回転していることを考えた方角の取り出し
	public void getOrientation(float[] rotate, float[] out) {

		// ディスプレイの回転方向を求める(縦もちとか横持ちとか)
		Display disp = this.getWindowManager().getDefaultDisplay();
		// ↓コレを使うためにはAPIレベルを8にする必要がある
		int dispDir = disp.getRotation();
		// 画面回転してない場合はそのまま
		if (dispDir == Surface.ROTATION_0) {
			SensorManager.getOrientation(rotate, out);
			// 回転している
		} else {
			float[] outR = new float[16];

			// 90度回転
			if (dispDir == Surface.ROTATION_90) {
				SensorManager.remapCoordinateSystem(rotate,
						SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
				// 180度回転
			} else if (dispDir == Surface.ROTATION_180) {
				float[] outR2 = new float[16];

				SensorManager
						.remapCoordinateSystem(rotate, SensorManager.AXIS_Y,
								SensorManager.AXIS_MINUS_X, outR2);
				SensorManager.remapCoordinateSystem(outR2,
						SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
				// 270度回転
			} else if (dispDir == Surface.ROTATION_270) {
				SensorManager.remapCoordinateSystem(outR,
						SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_MINUS_X,
						outR);
			}
			SensorManager.getOrientation(outR, out);
		}
	}
	
	private void init() {
		// Initialize Location Client
		if (locationClient == null) {
			locationClient = new LocationClient(this, locationCallback,
					locationCallback);
			// Log.v(KadaiMainActivity.TAG, "Location Client connect");
			if (!(locationClient.isConnected() || locationClient.isConnecting())) {
				locationClient.connect();
			}
		}
	}

	private void checkGooglePlayServiceAvailability(int requestCode) {
		// Query for the status of Google Play services on the device
		int statusCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());
		if (statusCode == ConnectionResult.SUCCESS) {
			init();
		} else {
			if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
				errorDialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
						this, requestCode);
				errorDialog.show();
			} else {
				// Handle unrecoverable error
			}
		}
	}
	private void handleLocation(Location location) {
		
		lastLocation = location;
		mView.setGps("" + location.getLatitude(), "" + location.getLongitude());

//		Intent intent = getIntent();
//		int param = intent.getIntExtra("ID", 0);
		Location imglocation = new Location("apiopost");
		imglocation.setLatitude(c[param]);
		imglocation.setLongitude(d[param]);

		float distance = location.distanceTo(imglocation);
		mView.setDistance(distance);
		float direction = location.bearingTo(imglocation);
		mView.setDirection(direction);

		if (direction <= -157.5) {
			dirtext = a[0];
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
		mView.setDirtext("" + dirtext);
		mView.setGoal("" + goal);
		if (distance < 200) {
			goal = "登場";
			setPrefer(param);	
			showCamera(param);
		} else {
			goal = "隠れているのは・・・";
		}
	}

	private void setPrefer(int param) {
		// TODO Auto-generated method stub
		String keys[]={"石巻","大郷","アエル","北上","滝沢","石川","青山","六本木"};
		String place[]={"Zun子をゲット","大郷","アエル","北上","滝沢","石川","青山","六本木"};
		
		SharedPreferences prefer = getSharedPreferences("getchara", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefer.edit();
		
		editor.putString(keys[param], place[param]);
		editor.commit();
	}
	private class LocationCallback implements ConnectionCallbacks,
			OnConnectionFailedListener, LocationListener {

		@Override
		public void onConnected(Bundle connectionHint) {
			// Log.v(KadaiMainActivity.TAG,"Location Client Connected");
			Location currentLocation = locationClient.getLastLocation();
			if (currentLocation != null) {
				handleLocation(currentLocation);
			}
			LocationRequest request = LocationRequest.create();
			request.setInterval(LOCATION_UPDATES_INTERVAL);
			// request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			// より高い精度が必要な場合は下記の方を使用します
			request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationClient.requestLocationUpdates(request, locationCallback);
		}

		@Override
		public void onDisconnected() {
			// Log.v(KadaiMainActivity.TAG,"Location Client Disconnected");
		}

		@Override
		public void onConnectionFailed(ConnectionResult result) {
			// Log.v(KadaiMainActivity.TAG,"Location Client connection failed");
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (location == null) {
				// Log.v(KadaiMainActivity.TAG,
				// "onLocationChanged: location == null");
				return;
			}
			if (lastLocation != null
					&& lastLocation.getLatitude() == location.getLatitude()
					&& lastLocation.getLongitude() == location.getLongitude()) {
				return;
				}
			handleLocation(location);
			// mView.setGps("" + location.getLatitude(), "" +
			// location.getLongitude());
			//
			// Location imglocation = new Location("apiopost");
			// imglocation.setLatitude(c[param]);
			// imglocation.setLongitude(d[param]);
			// float distance = location.distanceTo(imglocation);
			// mView.setDistance(distance);
			// float direction = location.bearingTo(imglocation);
			// mView.setDirection(direction);
			// mView.setDirtext("" + dirtext);
			// if (direction <= -157.5) {
			// dirtext=a[0];
			// } else if (direction > -157.5 && direction < -112.5) {
			// dirtext = a[1];
			// } else if (direction >= -112.5 && direction <= -67.5) {
			// dirtext = a[2];
			// } else if (direction > -67.5 && direction < -22.5) {
			// dirtext = a[3];
			// } else if (direction >= -22.5 && direction <= 22.5) {
			// dirtext = a[4];
			// } else if (direction > 22.5 && direction < 67.5) {
			// dirtext = a[5];
			// } else if (direction >= 67.5 && direction <= 112.5) {
			// dirtext = a[6];
			// } else if (direction > 112.5 && direction < 157.5) {
			// dirtext = a[7];
			// } else if (direction >= 157.5) {
			// dirtext = a[0];
			// }
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (resultCode) {
			case ERROR_DIALOG_ON_CREATE_REQUEST_CODE:
				init();
				break;
			case ERROR_DIALOG_ON_RESUME_REQUEST_CODE:
				restartLocationClient();
				break;
			}
		}
	}

	private void restartLocationClient() {
		if (!(locationClient.isConnected() || locationClient.isConnecting())) {
			locationClient.connect();// Somehow it becomes connected here
			return;
		}
		LocationRequest request = LocationRequest.create();
		request.setInterval(LOCATION_UPDATES_INTERVAL);
		// request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		// より高い精度が必要な場合は下記の方を使用します
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationClient.requestLocationUpdates(request, locationCallback);
	}

	// ////////////////////////////////////////////////////////////
	// ポーズ
	@Override
	protected void onPause() {
		super.onPause();

		// リスナーの登録解除
		this.mSensorManager.unregisterListener(this);

		// indicate the application is in background
		isAppForeground = false;

		if (locationClient.isConnected()) {
			locationClient.removeLocationUpdates(locationCallback);
			locationClient.disconnect();
		}
	}

	// ////////////////////////////////////////////////////////////
	// 再開
	@Override
	protected void onResume() {
		super.onResume();

		// リスナーの登録
		this.mSensorManager.registerListener(this, this.mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		this.mSensorManager.registerListener(this, this.mMagneticField,
				SensorManager.SENSOR_DELAY_NORMAL);

		isAppForeground = true;
		checkGooglePlayServiceAvailability(ERROR_DIALOG_ON_RESUME_REQUEST_CODE);
		init();
		restartLocationClient();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
		}
	}
	private void showCamera(int param){
		// TODO Auto-generated method stub
		// インテントのインスタンス生成
		Intent intent2 = new Intent(this, CameraActivity.class);
		intent2.putExtra("ID",param);
		// 次画面のアクティビティ起動
		startActivity(intent2);
	}
}

/**
 * オーバーレイ描画用のクラス
 */
class MyView extends View {
	// private int mCurX;
	// private int mCurY;
	// Yaw
//	private float yaw;
	// Pitch
//	private float pitch;
	private float degreeDir;
	private float degreeY;
	// YawText
	private String yawtext;
	// private String yawtext1;
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
	// gaol
	private String goal = "Search Charactor";
	// 画像を格納する変数
	private Bitmap myBitmap, myBitmap2;
	// サウンド再生データを保持する。
	private MediaPlayer mp;
	// X軸方向位置
	private float xp;
	// y方向位置
	private float yp;

//	private int param;
//	private String zzm;
//	private String kuma;
	private float gap;

	// バイブレータオブジェクトを保持
	Vibrator vibrator;

	// private WebView webView; //追加
	// private LinearLayout.LayoutParams arLayoutParams;
	// private LinearLayout arLayout; //追加

	/**
	 * コンストラクタ
	 * 
	 * @param contextコンテキスト
	 */
	public MyView(Context context, int param) {
		super(context);
		setFocusable(true);

		// バイブレータを用意する
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		// Resourceインスタンスの生成
		Resources res = this.getContext().getResources();
		// 画像の読み込み(res/drawable/gclue_logo.gif)
		if (param == 3 || param == 4) {
			myBitmap = BitmapFactory.decodeResource(res, R.drawable.soba);
		} else if (param == 1 || param == 2 || param == 3) {
			myBitmap = BitmapFactory.decodeResource(res, R.drawable.kuma);
		} else {
			myBitmap = BitmapFactory.decodeResource(res, R.drawable.zzm);
		}
		// サウンドデータを読み込む(res/raw/pon.mp3)
		mp = MediaPlayer.create(context, R.raw.powerup02);
	}

	/**
	 * 値を渡す
	 */
	public void setYaw(String yawtext) {
		this.yawtext = yawtext;
		// this.yawtext1= yawtext1;
		invalidate();
	}
	/**
	 * 値を渡す(degreeDir)
	 */
	public void setdegree(float degreeDir) {
		this.degreeDir = degreeDir;

		invalidate();
	}
	/**
	 * 値を渡す(degreeY)
	 */
	public void setdegreeY(float degreeY) {
		this.degreeY = degreeY;
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
	 * 値を渡す(direction)
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

		WindowManager wm = (WindowManager) this.getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		// 背景色を設定
		if (goal.equals("登場")) {
			canvas.drawColor(Color.TRANSPARENT);
		} else {
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

		Paint charaPaint = new Paint();
		mainPaint.setStyle(Paint.Style.STROKE);
		mainPaint.setARGB(255, 0, 0, 0);
		mainPaint.setTextSize(35);
		mainPaint.setStrokeWidth(4);

		// xp = (float) ((xp
		// +(0.3*(xp-(displayMetrics.widthPixels)*(direction-degreeDir+45)/90)))/1.3);
		// staticfinalfloatALPHA = 0.15f;
		// void lowPassFilter( float[] input, float[] output ) {
		// for (int i = 0; i < input.length; i++) {
		// output[i] = output[i] + ALPHA * (input[i] - output[i]);
		// }
		// }
		// canvas.drawBitmap(myBitmap,(displayMetrics.widthPixels)/2,(displayMetrics.heightPixels)/2,mainPaint);
		// Log.v("Bitmap","xp:" + xp);

		yp = (yp + (displayMetrics.heightPixels) * (-degreeY) / 90 - 400) / 2;
		// Bitmapイメージの描画
		if (goal.equals("登場")) {

			gap = direction - degreeDir;

			if (gap > -90 && gap < 90) {
				xp = (float) ((xp + (displayMetrics.widthPixels) * (gap + 45)
						/ 90) / 2);
				canvas.drawBitmap(myBitmap, xp, yp, charaPaint);
			} else if (gap >= -360 && gap < -270) {
				xp = (float) ((xp + (displayMetrics.widthPixels) * (gap + 405)
						/ 90) / 2);
				canvas.drawBitmap(myBitmap, xp, yp, charaPaint);
			} else if (gap > 270 && gap <= 360) {
				xp = (float) ((xp + (displayMetrics.widthPixels) * (gap - 315)
						/ 90) / 2);
				canvas.drawBitmap(myBitmap, xp, yp, charaPaint);
			} else {
			}

			// canvas.drawBitmap(myBitmap,100+(float)((int)(direction-degreeDir)*0.4)*10,100-pitch*5,mainPaint);
			// Log.i("Bitmap", "相手角度:" + (direction));
			// Log.i("Bitmap", "自分角度:" + (degreeDir));
		}

		// 音の再生
//		if (goal.equals("登場")) {
//			// 音の再生開始位置を0ミリセカンドの位置に設定する
//			// mp.seekTo(0);
//			// 音の再生を開始する
//			// mp.start();
//		} else {
//			// 音を停止する
//			mp.stop();
//
//			// 一度再生をstop()してから再び音を再生する場合には、prepare()を呼び出す必要がある
//			try {
//				mp.prepare();
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		// 文字を描画
		if (goal.equals("登場")) {
			canvas.drawText("くまもん！" + goal, 10, 900, subPaint);
			
			
		} else {
			canvas.drawText("" + yawtext, displayMetrics.widthPixels / 2 - 20,
					displayMetrics.heightPixels / 2 - 310, mainPaint);
			canvas.drawText("目標までの距離:" + (int) distance + " m",
					displayMetrics.widthPixels / 8,
					displayMetrics.heightPixels * 15 / 16, mainPaint);
			canvas.drawText("目標の方向:" + dirtext, 10, 50, mainPaint);
			drawRadar(canvas, displayMetrics, mainPaint, subPaint);
		}
		// バイブレータを動作させる
		if (goal.equals("登場")) {
			vibrator.cancel();
		} else if (dirtext.equals(yawtext)) {
			vibrator.vibrate(10);
		}
	}
	
	private void drawRadar(Canvas canvas, DisplayMetrics displayMetrics,
			Paint mainPaint, Paint subPaint) {
		// 円を描画
		canvas.drawCircle(displayMetrics.widthPixels / 2,
				displayMetrics.heightPixels / 2, 500, mainPaint);
		canvas.drawCircle(displayMetrics.widthPixels / 2,
				displayMetrics.heightPixels / 2, 300, mainPaint);
		canvas.drawCircle(displayMetrics.widthPixels / 2,
				displayMetrics.heightPixels / 2, 200, mainPaint);
		canvas.drawCircle(displayMetrics.widthPixels / 2,
				displayMetrics.heightPixels / 2, 100, mainPaint);
		canvas.drawRect(displayMetrics.widthPixels / 2 - 10,
				displayMetrics.heightPixels / 2 - 10,
				displayMetrics.widthPixels / 2 + 10,
				displayMetrics.heightPixels / 2 + 10, mainPaint);
		canvas.drawLine(displayMetrics.widthPixels / 2 - 220,
				displayMetrics.heightPixels / 4,
				displayMetrics.widthPixels / 2 - 10,
				displayMetrics.heightPixels / 2 - 10, mainPaint);
		canvas.drawLine(displayMetrics.widthPixels / 2 + 220,
				displayMetrics.heightPixels / 4,
				displayMetrics.widthPixels / 2 + 10,
				displayMetrics.heightPixels / 2 - 10, mainPaint);
		canvas.drawCircle(
				(distance * (float) Math.sin(Math.toRadians(direction
						- (degreeDir))))
						/ 1 + displayMetrics.widthPixels / 2,
				(-distance * (float) Math.cos(Math.toRadians(direction
						- (degreeDir))))
						/ 1 + displayMetrics.heightPixels / 2, 15, subPaint);
	}
}