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
	private Sensor mAccelerometer; // �����x�Z���T20130526
	private Sensor mMagneticField; // ���C�Z���T20130526

	String dirtext;
	String[] a = { "S", "�쐼", "W", "�k��", "N", "�k��", "E", "�쓌" };

	String yawtext;
	// String yawtext1;
	// String[ ]b = {"N","�k��","E","�쓌","S","�쐼","W","�k��"};
	
	// �C�g�i�u�@�勽 �A�G�� �k��@���@�ΐ�@�R�@�Z�{��
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

		// Notification Bar������
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Title Bar������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		checkGooglePlayServiceAvailability(ERROR_DIALOG_ON_CREATE_REQUEST_CODE);

		Intent intent = getIntent();
		param = intent.getIntExtra("ID", 0);

		// �N���X�̃C���X�^���X�𐶐�
		mView = new MyView(this, param);
		// Vew�ɐݒ�
		addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				+LayoutParams.WRAP_CONTENT));

		// SensorManager
		this.mSensorManager = (SensorManager) this
				.getSystemService(SENSOR_SERVICE);// 20130526this�ǉ�
		this.mAccelerometer = this.mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 20130525
		this.mMagneticField = this.mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);// 20130526
		mRegisteredSensor = false; // �ǉ�

	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	// �����x�Z���T�̒l
	private float[] mAccelerometerValue = new float[3];
	// ���C�Z���T�̒l
	private float[] mMagneticFieldValue = new float[3];
	// ���C�Z���T�̍X�V�����񂾂�
	private boolean mValidMagneticFiled = false;

	@Override
	public void onSensorChanged(SensorEvent event) { // 20130526sensorEvent��event�ɕύX
		// TODO Auto-generated method stub
		// �Z���T�[���Ƃ̏���
		switch (event.sensor.getType()) {
		// �����x�Z���T�[
		case Sensor.TYPE_ACCELEROMETER:
			// clone�Ŕz�񂪃R�s�[�ł����Ⴄ�񂾂ˁB�ւ�
			this.mAccelerometerValue = event.values.clone();
			break;
		// ���C�Z���T�[
		case Sensor.TYPE_MAGNETIC_FIELD:
			this.mMagneticFieldValue = event.values.clone();
			this.mValidMagneticFiled = true;
			break;
		}
		// �l���X�V���ꂽ�p�x���o���������ł���
		if (this.mValidMagneticFiled) {
			// ���ʂ��o�����߂̕ϊ��s��
			float[] rotate = new float[16]; // �X�΍s��H
			float[] inclination = new float[16]; // ��]�s��
			// ���܂����ƕϊ��s�������Ă����炵��
			SensorManager.getRotationMatrix(rotate, inclination,
					this.mAccelerometerValue, this.mMagneticFieldValue);
			// ���������߂�
			float[] inorientation = new float[3];
			this.getOrientation(rotate, inorientation);

//			float[] outorientation = new float[3];

			// �f�O���[�p�ɕϊ�����
			float degreeDir = (float) Math.toDegrees(inorientation[0]);
			float degreeY = (float) Math.toDegrees(inorientation[1]);

			
			// event.values��degreeDir��
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
	// ��ʂ���]���Ă��邱�Ƃ��l�������p�̎��o��
	public void getOrientation(float[] rotate, float[] out) {

		// �f�B�X�v���C�̉�]���������߂�(�c�����Ƃ��������Ƃ�)
		Display disp = this.getWindowManager().getDefaultDisplay();
		// ���R�����g�����߂ɂ�API���x����8�ɂ���K�v������
		int dispDir = disp.getRotation();
		// ��ʉ�]���ĂȂ��ꍇ�͂��̂܂�
		if (dispDir == Surface.ROTATION_0) {
			SensorManager.getOrientation(rotate, out);
			// ��]���Ă���
		} else {
			float[] outR = new float[16];

			// 90�x��]
			if (dispDir == Surface.ROTATION_90) {
				SensorManager.remapCoordinateSystem(rotate,
						SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
				// 180�x��]
			} else if (dispDir == Surface.ROTATION_180) {
				float[] outR2 = new float[16];

				SensorManager
						.remapCoordinateSystem(rotate, SensorManager.AXIS_Y,
								SensorManager.AXIS_MINUS_X, outR2);
				SensorManager.remapCoordinateSystem(outR2,
						SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
				// 270�x��]
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
			goal = "�o��";
			setPrefer(param);	
			showCamera(param);
		} else {
			goal = "�B��Ă���̂́E�E�E";
		}
	}

	private void setPrefer(int param) {
		// TODO Auto-generated method stub
		String keys[]={"�Ί�","�勽","�A�G��","�k��","���","�ΐ�","�R","�Z�{��"};
		String place[]={"Zun�q���Q�b�g","�勽","�A�G��","�k��","���","�ΐ�","�R","�Z�{��"};
		
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
			// ��荂�����x���K�v�ȏꍇ�͉��L�̕����g�p���܂�
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
		// ��荂�����x���K�v�ȏꍇ�͉��L�̕����g�p���܂�
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationClient.requestLocationUpdates(request, locationCallback);
	}

	// ////////////////////////////////////////////////////////////
	// �|�[�Y
	@Override
	protected void onPause() {
		super.onPause();

		// ���X�i�[�̓o�^����
		this.mSensorManager.unregisterListener(this);

		// indicate the application is in background
		isAppForeground = false;

		if (locationClient.isConnected()) {
			locationClient.removeLocationUpdates(locationCallback);
			locationClient.disconnect();
		}
	}

	// ////////////////////////////////////////////////////////////
	// �ĊJ
	@Override
	protected void onResume() {
		super.onResume();

		// ���X�i�[�̓o�^
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
		// �C���e���g�̃C���X�^���X����
		Intent intent2 = new Intent(this, CameraActivity.class);
		intent2.putExtra("ID",param);
		// ����ʂ̃A�N�e�B�r�e�B�N��
		startActivity(intent2);
	}
}

/**
 * �I�[�o�[���C�`��p�̃N���X
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
	private String dirtext = "���蒆";
	// gaol
	private String goal = "Search Charactor";
	// �摜���i�[����ϐ�
	private Bitmap myBitmap, myBitmap2;
	// �T�E���h�Đ��f�[�^��ێ�����B
	private MediaPlayer mp;
	// X�������ʒu
	private float xp;
	// y�����ʒu
	private float yp;

//	private int param;
//	private String zzm;
//	private String kuma;
	private float gap;

	// �o�C�u���[�^�I�u�W�F�N�g��ێ�
	Vibrator vibrator;

	// private WebView webView; //�ǉ�
	// private LinearLayout.LayoutParams arLayoutParams;
	// private LinearLayout arLayout; //�ǉ�

	/**
	 * �R���X�g���N�^
	 * 
	 * @param context�R���e�L�X�g
	 */
	public MyView(Context context, int param) {
		super(context);
		setFocusable(true);

		// �o�C�u���[�^��p�ӂ���
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		// Resource�C���X�^���X�̐���
		Resources res = this.getContext().getResources();
		// �摜�̓ǂݍ���(res/drawable/gclue_logo.gif)
		if (param == 3 || param == 4) {
			myBitmap = BitmapFactory.decodeResource(res, R.drawable.soba);
		} else if (param == 1 || param == 2 || param == 3) {
			myBitmap = BitmapFactory.decodeResource(res, R.drawable.kuma);
		} else {
			myBitmap = BitmapFactory.decodeResource(res, R.drawable.zzm);
		}
		// �T�E���h�f�[�^��ǂݍ���(res/raw/pon.mp3)
		mp = MediaPlayer.create(context, R.raw.powerup02);
	}

	/**
	 * �l��n��
	 */
	public void setYaw(String yawtext) {
		this.yawtext = yawtext;
		// this.yawtext1= yawtext1;
		invalidate();
	}
	/**
	 * �l��n��(degreeDir)
	 */
	public void setdegree(float degreeDir) {
		this.degreeDir = degreeDir;

		invalidate();
	}
	/**
	 * �l��n��(degreeY)
	 */
	public void setdegreeY(float degreeY) {
		this.degreeY = degreeY;
		invalidate();
	}
	/**
	 * �l��n���iGPS)
	 */
	public void setGps(String lat, String lon) {
		this.lat = lat;
		this.lon = lon;
		invalidate();
	}
	/**
	 * �l��n��(distance)
	 */
	public void setDistance(float distance) {
		this.distance = distance;
		invalidate();
	}
	/**
	 * �l��n��(direction)
	 */
	public void setDirection(float direction) {
		this.direction = direction;
		invalidate();
	}
	/**
	 * �l��n��(dirtext)
	 */
	public void setDirtext(String dirtext) {
		this.dirtext = dirtext;
		invalidate();
	}
	/**
	 * �l��n��(goal)
	 */
	public void setGoal(String goal) {
		this.goal = goal;
		invalidate();
	}
	/**
	 * �`�揈�����s��
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		WindowManager wm = (WindowManager) this.getContext().getSystemService(
				Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		// �w�i�F��ݒ�
		if (goal.equals("�o��")) {
			canvas.drawColor(Color.TRANSPARENT);
		} else {
			canvas.drawColor(0xffccff44);
		}
		// �`�悷�邽�߂̐��̐F��ݒ�
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
		// Bitmap�C���[�W�̕`��
		if (goal.equals("�o��")) {

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
			// Log.i("Bitmap", "����p�x:" + (direction));
			// Log.i("Bitmap", "�����p�x:" + (degreeDir));
		}

		// ���̍Đ�
//		if (goal.equals("�o��")) {
//			// ���̍Đ��J�n�ʒu��0�~���Z�J���h�̈ʒu�ɐݒ肷��
//			// mp.seekTo(0);
//			// ���̍Đ����J�n����
//			// mp.start();
//		} else {
//			// �����~����
//			mp.stop();
//
//			// ��x�Đ���stop()���Ă���Ăщ����Đ�����ꍇ�ɂ́Aprepare()���Ăяo���K�v������
//			try {
//				mp.prepare();
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		// ������`��
		if (goal.equals("�o��")) {
			canvas.drawText("���܂���I" + goal, 10, 900, subPaint);
			
			
		} else {
			canvas.drawText("" + yawtext, displayMetrics.widthPixels / 2 - 20,
					displayMetrics.heightPixels / 2 - 310, mainPaint);
			canvas.drawText("�ڕW�܂ł̋���:" + (int) distance + " m",
					displayMetrics.widthPixels / 8,
					displayMetrics.heightPixels * 15 / 16, mainPaint);
			canvas.drawText("�ڕW�̕���:" + dirtext, 10, 50, mainPaint);
			drawRadar(canvas, displayMetrics, mainPaint, subPaint);
		}
		// �o�C�u���[�^�𓮍삳����
		if (goal.equals("�o��")) {
			vibrator.cancel();
		} else if (dirtext.equals(yawtext)) {
			vibrator.vibrate(10);
		}
	}
	
	private void drawRadar(Canvas canvas, DisplayMetrics displayMetrics,
			Paint mainPaint, Paint subPaint) {
		// �~��`��
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