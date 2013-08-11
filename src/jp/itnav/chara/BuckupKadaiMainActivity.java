package jp.itnav.chara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
//import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
//import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationRequest;

public class BuckupKadaiMainActivity extends Activity implements SensorEventListener {
	// location
	private LocationClient locationClient = null;
	// private TextView locationStatus;
	private LocationCallback locationCallback = new LocationCallback();
	private Location lastLocation;
	public static boolean isAppForeground = false;
	private Dialog errorDialog;

	private static final String TAG = "KadaiMainActivity";
	private static final int LOCATION_UPDATES_INTERVAL = 20000; 
	// Setting 60 sec interval for location updates
	private static final int ERROR_DIALOG_ON_CREATE_REQUEST_CODE = 4055;
	private static final int ERROR_DIALOG_ON_RESUME_REQUEST_CODE = 4056;

	private MyView mView;
	private boolean mRegisteredSensor; // �ǉ�
	// Sensor Manager
	private SensorManager mSensorManager = null;
	private Sensor mAccelerometer; // �����x�Z���T20130526
	private Sensor mMagneticField; // ���C�Z���T20130526

	// private LocationManager lm; //�ǉ�
	// Location Manager
	// private LocationManager mLocationManager = null;
	// private LinearLayout.LayoutParams arLayoutParams;
	// private WebView webView; //�폜20130526
	// private LinearLayout arLayout; //�ǉ�
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

	String goal;
	float distance;

	// private GestureDetector mGDetector = null;

	private Location location;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Notification Bar������
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Title Bar������
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		checkGooglePlayServiceAvailability(ERROR_DIALOG_ON_CREATE_REQUEST_CODE);

		// CameraView mCamera = new CameraView(this);
		// setContentView(mCamera);

		Intent intent = getIntent();
		int param = intent.getIntExtra("ID", 0);

		// �N���X�̃C���X�^���X�𐶐�
		mView = new MyView(this, param);

		// Vew�ɐݒ�
		addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT,
				+LayoutParams.WRAP_CONTENT));

		// mCamera.setView(mView);

		// SensorManager
		this.mSensorManager = (SensorManager) this
				.getSystemService(SENSOR_SERVICE);// 20130526this�ǉ�
		this.mAccelerometer = this.mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// 20130525
		this.mMagneticField = this.mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);// 20130526
		mRegisteredSensor = false; // �ǉ�

		// 20130527�폜
		// if (sensors.size() > 0) {
		// Sensor s = sensors.get(0);
		// sensorManager.registerListener(this, s,
		// SensorManager.SENSOR_DELAY_UI);
		// Log.v("OnResume", "PASS");
		// if (locman != null){
		// locman.removeUpdates(this);
		// locman.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// locman.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 0,0,this);
		// location = locman.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// }else{
		// Toast.makeText(this, "failed to get location from GPS",
		// Toast.LENGTH_LONG).show();
		// }
		// if (locman.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
		// locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// 10000, 10, this);
		// location =
		// locman.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		// } else {
		// Toast.makeText(this, "failed to get location from NETWORK",
		// Toast.LENGTH_LONG).show();
		// }
		// super.onResume();
		// }
		// }
		// LocationManager��GPS�̒l���擾���邽�߂̐ݒ�
		// lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// �l���ω������ۂɌĂяo����郊�X�i�[�̒ǉ�
		// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
	}

	// @Override
	// public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
	// mView.setGps("" + location.getLatitude(), "" + location.getLongitude());
	// Intent intent = getIntent();
	// int param =intent.getIntExtra("ID",0);
	// �Ί��H��
	// Location apiopostLocation = new Location("apiopost");
	// apiopostLocation.setLatitude(38.43792);
	// apiopostLocation.setLongitude(141.287221);
	// Location imglocation = new Location("apiopost");
	// imglocation.setLatitude(c[param]);
	// imglocation.setLongitude(d[param]);

	// float distance = location.distanceTo(imglocation);
	// mView.setDistance(distance);

	// mView.setGoal(""+ goal);
	// if (distance < 40){
	// goal="�o��";
	// }else{
	// goal="�B��Ă���̂́E�E�E";
	// }

	// float direction = location.bearingTo(imglocation);
	// mView.setDirection(direction);
	// Log.i("onLocationC", "����p�x:" + (direction));
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
	// }

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
		// ��20130527
		// if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
		// // Log.i("SURFACE","yaw"+event.values[0]);
		// // Log.i("SURFACE","pitch"+sensorEvent.values[1]);
		// // Log.i("SURFACE","roll"+sensorEvent.values[2]);
		// mView.setOrientation(event.values[0],event.values[1],event.values[2]);
		// }
		// else if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
		// mView.onAcclerometerChanged((int)sensorEvent.values[0],(int)sensorEvent.values[1],(int)sensorEvent.values[2]);
		// }
		// �V
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

			// this.lowPassFilter(inorientation, outorientation);

			// �f�O���[�p�ɕϊ�����
			float degreeDir = (float) Math.toDegrees(inorientation[0]);
			float degreeY = (float) Math.toDegrees(inorientation[1]);

			// Log.i("onSensorChanged", "degY:" + degreeY);
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

	// static final float ALPHA = 0.3f;
	// void lowPassFilter( float[] input, float[] output ) {
	// for (int i = 0; i < input.length; i++) {
	// output[i] = output[i] + ALPHA * (input[i] - output[i]);
	// }
	// }
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
		// locationStatus = (TextView) findViewById(R.id.locationText);
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
		// Log.v(KadaiMainActivity.TAG, "LocationChanged == @"
		// +location.getLatitude() + "," + location.getLongitude());
		// locationStatus.setText("Location changed @" + location.getLatitude()
		// + "," + location.getLongitude());
		lastLocation = location;

		mView.setGps("" + location.getLatitude(), "" + location.getLongitude());

		Intent intent = getIntent();
		int param = intent.getIntExtra("ID", 0);
		Location imglocation = new Location("apiopost");
		imglocation.setLatitude(c[param]);
		imglocation.setLongitude(d[param]);

		// mView.setParam(param);

		float distance = location.distanceTo(imglocation);
		mView.setDistance(distance);
		float direction = location.bearingTo(imglocation);
		// Log.v(KadaiMainActivity.TAG, "direction == @" +direction);
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
		// Log.v(KadaiMainActivity.TAG, "dirtext == @" +dirtext);
		mView.setGoal("" + goal);
		if (distance < 30) {
			goal = "�o��";
			CameraView mCamera = new CameraView(this);// cameraview�ύX
			setContentView(mCamera);// cameraview�ύX
			mCamera.setView(mView);// cameraview�ύX
			addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT,
					+LayoutParams.WRAP_CONTENT));
			setPrefer(param);
			
		} else {
			goal = "�B��Ă���̂́E�E�E";
		}
		// progressDialog.show();
		// getData(location);
	}

	private void setPrefer(int param) {
		// TODO Auto-generated method stub
		String keys[]={"�Ί�","�勽","�A�G��","�k��","���","�ΐ�","�R","�Z�{��"};
		String place[]={"kumaMon���Q�b�g","�勽","�A�G��","�k��","���","�ΐ�","�R","�Z�{��"};
		
		SharedPreferences prefer = getSharedPreferences("getchara", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefer.edit();
		
		editor.putString(keys[param], place[param]);
		editor.commit();
	}

	// private void getData(Location location){
	// //API
	// //type
	// //http://www.miraikioku.com/docs/api/serch_kioku
	// //
	// String apiUrl = miraiKiokuUrl +"?" +"type=photo" +"&" +"thumb-size=100c"+
	// "&" + "location-radius=40" + "&" +
	// "location="+String.valueOf(location.getLatitude())
	// + "," + String.valueOf(location.getLongitude());
	// new AccessAPItask().execute(apiUrl);
	// }
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
			// Intent intent = getIntent();
			// int param =intent.getIntExtra("ID",0);
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
			// mView.setGoal(""+ goal);
			// if (distance < 40){
			// goal="�o��";
			// }else{
			// goal="�B��Ă���̂́E�E�E";
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

		// Sensor�̎擾�ƃ��X�i�[�ւ̓o�^(���j20130526
		// List<Sensor> sensors =
		// mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		// if (sensors.size() > 0) {
		// Sensor sensor = sensors.get(0);
		// mSensorManager.registerListener(this,
		// sensor,SensorManager.SENSOR_DELAY_FASTEST);
		// }
		// List< Sensor >sensors_accelerometer =
		// mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		// if(sensors_accelerometer.size() > 0){
		// Sensor sensor_accelerometer = sensors_accelerometer.get(0);
		// mRegisteredSensor = mSensorManager.registerListener(this,
		// sensor_accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		// }
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
	// �ǉ�
	protected void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
		}
		// if (mLocationManager != null) {
		// mLocationManager.removeUpdates(this);
		// }
	}
}

/**
 * CameraView
 */
class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	// Context mContext; //�ǉ�

	/**
	 * Camera�̃C���X�^���X���i�[����ϐ�
	 */
	private Camera mCamera;
	private Context context;

	/**
	 * MyView�̃C���X�^���X���i�[����ϐ�
	 */
	private View mView;

	CameraView(Context context) { // �ύX
		super(context);
		this.context = context;
		// mContext = context; //�ǉ�
		// SurfaceHolder mHolder = getHolder(); //�ǉ�
		// mHolder.addCallback(this); //�ǉ�
		// mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //�ǉ�
		setDrawingCacheEnabled(true);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * MyView���󂯓n��
	 * 
	 * @param mView
	 *            MyView
	 */
	public void setView(View mView) {
		this.mView = mView;
	}

	/**
	 * Surface�ɕω����������ꍇ�ɌĂ΂��
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Log.i("CAMERA", "surfaceChaged");

		// int num;
		// ��ʐݒ�
		Camera.Parameters parameters = mCamera.getParameters();
		// parameters.setPreviewSize(width, height);
		List<Size> params = parameters.getSupportedPictureSizes();
		int wid = params.get(0).width;
		int hei = params.get(0).height;
		for (Size s : params) {
			if (wid < s.width) {
				wid = s.width;
				hei = s.height;
			}
		}
		List<Size> params2 = parameters.getSupportedPreviewSizes();
		int wid2 = params2.get(0).width;
		int hei2 = params2.get(0).height;
		for (Size s : params2) {
			if (wid2 < s.width) {
				wid = s.width;
				hei = s.height;
			}
		}
		mCamera.setDisplayOrientation(90);
		parameters.setPictureSize(wid, hei);
		parameters.setPreviewSize(wid2, hei2);
		mCamera.setParameters(parameters);

		// �v���r���[�\�����J�n
		mCamera.startPreview();
	}

	/**
	 * Surface���������ꂽ�ۂɌĂ΂��
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// �J������Open
		// Log.i("DEBUG", "Camera open1");
		mCamera = Camera.open();
		// Log.i("DEBUG", "Camera open2");

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e) { // �ύX
		// mCamera.release();
		// Log.i("DEBUG", "Camera open3");
		// mCamera = null;
		}
	}

	/**
	 * Surface���j�����ꂽ�ꍇ�ɌĂ΂��
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Log.i("CAMERA", "surfaceDestroyed");

		// �J������Close
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			setDrawingCacheEnabled(false);
			setDrawingCacheEnabled(true);

			// mCamera.takePicture(null, null, new Camera.PictureCallback() {
			mCamera.takePicture(new Camera.ShutterCallback() {
				@Override
				public void onShutter() {
				}
			}, null, new Camera.PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					// Log.i("DEBUG", "onTouch");

					// �v���r���[�B�e��L���ɂ���
					mView.setDrawingCacheEnabled(true);
					Bitmap viewBitmap = Bitmap.createBitmap(mView
							.getDrawingCache());
					// �v���r���[�B�e�𖳌��ɂ���
					mView.setDrawingCacheEnabled(false);

					// �X�P�[�����擾
					int scale = getScale(data, viewBitmap.getWidth(),
							viewBitmap.getHeight());

					// Bitmap�������̃I�v�V�������쐬
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = scale;

					// �J�����摜����X�P�[���̒l��ݒ肵����ԂŃJ�����摜��Bitmap�`���Ŋi�[
					Bitmap myBitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length, options);

					Matrix matrix = new Matrix();
					matrix.postRotate(90);

					// �擾�����摜�T�C�Y�ɂ����T�C�Y��Bitmap���쐬(�����`�悳��Ă��Ȃ�)
					// Bitmap tmpBitmap =
					// Bitmap.createBitmap(myBitmap.getWidth(),myBitmap.getHeight(),
					// Bitmap.Config.ARGB_8888);
					Bitmap tmpBitmap = Bitmap.createBitmap(myBitmap, 0, 0,
							myBitmap.getWidth(), myBitmap.getHeight(), matrix,
							true);

					// tmpBitmap����L�����o�X���쐬
					Canvas canvas = new Canvas(tmpBitmap);

					// �쐬����Canvas��MyView�̃v���r���[�ƃJ�����̉摜���͂������
					// canvas.drawBitmap(myBitmap,null,new Rect(0, 0,
					// myBitmap.getWidth(), myBitmap.getHeight()), null);
					canvas.drawBitmap(viewBitmap, null, new Rect(0, 0,
							viewBitmap.getWidth(), viewBitmap.getHeight()),
							null);

					// sd�J�[�h�ɕۑ�
					saveBitmapToSd(tmpBitmap);

					// �v���r���[�\�����ĊJ
					mCamera.startPreview();

				}
			});
		}
		setDrawingCacheEnabled(false);
		return false;
	}

	/**
	 * �X�P�[�����擾����
	 * 
	 * @param data
	 *            �k�ڂ𒲍�����摜�f�[�^
	 * @param width
	 *            �ύX���鉡�̃T�C�Y
	 * @param height
	 *            �ύX����c�̃T�C�Y
	 * @return �ύX�������T�C�Y�̃X�P�[���̒l
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
	 * Bitmap�摜��sd�J�[�h�ɕۑ�
	 * 
	 * @param mBitmap
	 *            Bitmap�f�[�^
	 */
	public void saveBitmapToSd(Bitmap mBitmap) {
		try {
			// sdcard�t�H���_���w��
			// File root = Environment.getExternalStorageDirectory();
			File root = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/DCIM/");

			// ���t�Ńt�@�C�������쐬�@
			Date mDate = new Date();
			SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

			// �ۑ������J�n
			FileOutputStream fos = null;
			fos = new FileOutputStream(new File(root, fileName.format(mDate)
					+ ".jpg"));

			// jpeg�ŕۑ�
			mBitmap.compress(CompressFormat.JPEG, 100, fos);

			// �ۑ������I��
			fos.close();

		} catch (Exception e) {
			// Log.e("Error", "" + e.toString());
		}
		this.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
				.parse("file://" + Environment.getExternalStorageDirectory())));
	}

}

/**
 * �I�[�o�[���C�`��p�̃N���X
 */
class MyView extends View {
	// private double mLat;
	// private double mLon;

	// private int mCurX;
	// private int mCurY;

	// Roll
	private float roll;
	// int mRoll = Integer.parseInt(roll);
	// Yaw
	private float yaw;
	// Pitch
	private float pitch;
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

	private int param;
	private String zzm;
	private String kuma;
	private float gap;

	// /**
	// * �l��n��
	// */
	// public void setParam(int param) {
	// this.param = param;
	// invalidate();
	// }

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
		// String []chara = new String []{"zzm","kuma"};

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

		// //WebView�i�ǉ��j
		// webView = new WebView(this);
		// webView.loadUrl("http://ishiko.myswan.ne.jp/");
		// //Web�p��LayoutParams�i�ǉ��j
		// arLayoutParams = new LinearLayout.LayoutParams(200,200);
		// arLayoutParams.setMargins(0,0,0,0);
		// //WebWiew�𒣂�t����Layout�i�ǉ��j
		// arLayout = new LinearLayout(this);
		// arLayout.addView(myBitmap,arLayoutParams);
		// WebView�𒣂�t����Layout����ʂɒ���t���i�ǉ��j
		// addContentView(arLayout,new
		// LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	/**
	 * �l��n��
	 */
	// public void setOrientation(float yaw, float pitch, float roll) {
	// this.yaw = yaw;
	// this.pitch = pitch;
	// this.roll = roll;
	// invalidate();
	// }
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
		// Log.v("widthPixels",String.valueOf(displayMetrics.widthPixels));
		// Log.v("heightPixels",String.valueOf(displayMetrics.heightPixels));

		// display();
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

			// myBitmap2= Bitmap.createScaledBitmap(myBitmap,200,200, false);
			// myBitmap2=
			// Bitmap.createScaledBitmap(myBitmap,(int)(100*(30-distance)/30+100),(int)(100*(30-distance)/30+100),
			// false);
			// canvas.drawBitmap(myBitmap2,xp,yp,mainPaint);
			// Log.i("Bitmap","degreeY:" + degreeY);
			// Log.i("Bitmap", "YP:" + yp);

			// canvas.drawBitmap(myBitmap,100+(float)((int)(direction-degreeDir)*0.4)*10,100-pitch*5,mainPaint);
			// Log.i("Bitmap", "����p�x:" + (direction));
			// Log.i("Bitmap", "�����p�x:" + (degreeDir));

			// //WebView�̈ʒu���ړ�
			// arLayoutParams.setMargins(100, 100, 10, 10);
		}

		// ���̍Đ�
		if (goal.equals("�o��")) {
			// ���̍Đ��J�n�ʒu��0�~���Z�J���h�̈ʒu�ɐݒ肷��
			// mp.seekTo(0);
			// ���̍Đ����J�n����
			// mp.start();
		} else {
			// �����~����
			mp.stop();

			// ��x�Đ���stop()���Ă���Ăщ����Đ�����ꍇ�ɂ́Aprepare()���Ăяo���K�v������
			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// ������`��
		if (goal.equals("�o��")) {
			canvas.drawText("���܂���I" + goal, 10, 900, subPaint);
		} else {
			// canvas.drawText("" + yaw, 10, 10, mainPaint);
			// canvas.drawText("" + roll, 10, 30, mainPaint);
			// canvas.drawText("" + pitch, 10, 50, mainPaint);
			// if (yawtext.equals("N") || yawtext.equals("E") ||
			// yawtext.equals("S") || yawtext.equals("W")){
			canvas.drawText("" + yawtext, displayMetrics.widthPixels / 2 - 20,
					displayMetrics.heightPixels / 2 - 310, mainPaint);
			// Log.i("Kadai", "yawtext="+yawtext);
			// canvas.drawText(""+ yowtext1, 250, 159, mainPaint);
			// }
			// if (yawtext1.equals("N") || yawtext1.equals("E") ||
			// yawtext1.equals("S") || yawtext1.equals("W")){
			// canvas.drawText("" + yawtext1, 280, 170, mainPaint);
			// }
			// canvas.drawText("" + lat, 10, 100, mainPaint);
			// canvas.drawText("" + lon, 10, 120, mainPaint);

			canvas.drawText("�ڕW�܂ł̋���:" + (int) distance + " m",
					displayMetrics.widthPixels / 8,
					displayMetrics.heightPixels * 15 / 16, mainPaint);
			// canvas.drawText("" + direction, 10, 180, mainPaint);

			canvas.drawText("�ڕW�̕���:" + dirtext, 10, 50, mainPaint);
			// canvas.drawText("" + goal, 10, 800, mainPaint);

			drawRadar(canvas, displayMetrics, mainPaint, subPaint);
		}

		// �o�C�u���[�^�𓮍삳����
		if (goal.equals("�o��")) {
			vibrator.cancel();
		} else if (dirtext.equals(yawtext)) {
			vibrator.vibrate(10);
		}
	}

	// public void onAcclerometerChanged(int dx,int dy,int dz){ //�ǉ�
	// mDx = dx;
	// mDy = dy;
	// mDz = dz;
	// invalidate();
	// }

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