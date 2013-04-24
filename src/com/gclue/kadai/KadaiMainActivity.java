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
	private boolean mRegisteredSensor;	//�ǉ�
	// Sensor Manager
	private SensorManager mSensorManager = null;
	private LocationManager lm;	//�ǉ�
	// Location Manager
	private LocationManager mLocationManager = null;
	private LinearLayout.LayoutParams arLayoutParams;
	private WebView webView;	//�ǉ�
	private LinearLayout arLayout;	//�ǉ�
	String dirtext;
	String[ ]a = {"S","�쐼","W","�k��","N","�k��","E","�쓌"};
	
	String yawtext;
	String yawtext1;
	String[ ]b = {"N","�k��","E","�쓌","S","�쐼","W","�k��"};
	
	String goal;
	float distance;
//	int yawqua;
	
	private GestureDetector mGDetector = null;
	private KadaiARView mARView = null;
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Notification Bar������
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Title Bar������
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CameraView mCamera = new CameraView(this);
		setContentView(mCamera);

		// �N���X�̃C���X�^���X�𐶐�
		mView = new MyView(this);
		// Vew�ɐݒ�
//		setContentView(mView);
		addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT,+LayoutParams.WRAP_CONTENT));
		
		mCamera.setView(mView);
		
		// SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mRegisteredSensor = false;	//�ǉ�
		
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
		// LocationManager��GPS�̒l���擾���邽�߂̐ݒ�
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// �l���ω������ۂɌĂяo����郊�X�i�[�̒ǉ�
			
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
			
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mView.setGps("" + location.getLatitude(), "" + location.getLongitude());

		//�Ί��H��
//		Location apiopostLocation = new Location("apiopost");
//		apiopostLocation.setLatitude(38.43792);
//		apiopostLocation.setLongitude(141.287221);
		// �A�s�I�X�֋Ǎ��W
//		apiopostLocation.setLatitude(37.52144);
//		apiopostLocation.setLongitude(139.916199);
		//����
		Location apiopostLocation = new Location("apiopost");
		apiopostLocation.setLatitude(38.423455);
		apiopostLocation.setLongitude(140.989182);
		//�C�g�i�u
//		Location apiopostLocation = new Location("apiopost");
//		apiopostLocation.setLatitude(38.431619);
//		apiopostLocation.setLongitude(141.309406);
		//�A�G��
//		Location apiopostLocation = new Location("apiopost");
//		apiopostLocation.setLatitude(38.26229);
//		apiopostLocation.setLongitude(140.881017);

		float distance = location.distanceTo(apiopostLocation);
		mView.setDistance(distance);
		
		mView.setGoal(""+ goal);
		if (distance < 20){
			goal="�o��";			
//		�o�C�u���[�^�𓮍삳����
//			vibrator.vibrate(1000);
		}else{
			goal="�B��Ă���̂́E�E�E";
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
//			//WebView�̈ʒu���ړ�
//			arLayoutParams.setMargins((int)sensorEvent.values[0]*10, 100, 10, 10);
//			//Layout���X�V
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
	@Override	//�ǉ�
	protected void onResume(){
		super.onResume();
		
	// Sensor�̎擾�ƃ��X�i�[�ւ̓o�^
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
	
	@Override	//�ǉ�
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
//	Context mContext;	//�ǉ�
	
	/**
	 * Camera�̃C���X�^���X���i�[����ϐ�
	 */
	private Camera mCamera;
	private Context context;
	
	/**
	 * MyView�̃C���X�^���X���i�[����ϐ�
	 */
	private View mView;
	CameraView(Context context){	//�ύX
		super(context);
		this.context = context;
//		mContext = context;	//�ǉ�
//		SurfaceHolder mHolder = getHolder();	//�ǉ�
//		mHolder.addCallback(this);	//�ǉ�
//		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);	//�ǉ�
		setDrawingCacheEnabled(true);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	/**
	 * MyView���󂯓n��
	 * @param mView MyView
	 */
	public void setView(View mView) {
		this.mView = mView;
	}
 
	
	/**
	 * Surface�ɕω����������ꍇ�ɌĂ΂��
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
		Log.i("CAMERA", "surfaceChaged");
		 
		//��ʐݒ�
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
		
		//�v���r���[�\�����J�n
		mCamera.startPreview();
	}
	/**
	 * Surface���������ꂽ�ۂɌĂ΂��
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder){
		
		//�J������Open
		Log.i("DEBUG", "Camera open1");
		mCamera = Camera.open();
		Log.i("DEBUG", "Camera open2");

		try{
			mCamera.setPreviewDisplay(holder);
		}catch(Exception e){	//�ύX
//			mCamera.release();
			Log.i("DEBUG", "Camera open3");
//			mCamera = null;
		}
	}
	/**
	 * Surface���j�����ꂽ�ꍇ�ɌĂ΂��
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder){
		Log.i("CAMERA", "surfaceDestroyed");
		
		//�J������Close
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
					
					// �v���r���[�B�e��L���ɂ���
					mView.setDrawingCacheEnabled(true);
					Bitmap viewBitmap = Bitmap.createBitmap(mView.getDrawingCache());
					// �v���r���[�B�e�𖳌��ɂ���
					mView.setDrawingCacheEnabled(false);
					
					// �X�P�[�����擾
					int scale = getScale(data, viewBitmap.getWidth(),viewBitmap.getHeight());
					
					// Bitmap�������̃I�v�V�������쐬
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = scale;
					
					// �J�����摜����X�P�[���̒l��ݒ肵����ԂŃJ�����摜��Bitmap�`���Ŋi�[
					Bitmap myBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
					
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
 
					// �擾�����摜�T�C�Y�ɂ����T�C�Y��Bitmap���쐬(�����`�悳��Ă��Ȃ�)
//					Bitmap tmpBitmap = Bitmap.createBitmap(myBitmap.getWidth(),myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
					Bitmap tmpBitmap = Bitmap.createBitmap(myBitmap,0,0,myBitmap.getWidth(),myBitmap.getHeight(),matrix,true);
					
					// tmpBitmap����L�����o�X���쐬
					Canvas canvas = new Canvas(tmpBitmap);
 
					// �쐬����Canvas��MyView�̃v���r���[�ƃJ�����̉摜���͂������
//					canvas.drawBitmap(myBitmap,null,new Rect(0, 0, myBitmap.getWidth(), myBitmap.getHeight()), null);
					canvas.drawBitmap(viewBitmap, null, new Rect(0, 0,viewBitmap.getWidth(), viewBitmap.getHeight()),null);
					
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
	 * @param mBitmap Bitmap�f�[�^
	 */
	public void saveBitmapToSd(Bitmap mBitmap) {
	 try {
	 // sdcard�t�H���_���w��
//	 File root = Environment.getExternalStorageDirectory();
	File root = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/");

	 // ���t�Ńt�@�C�������쐬�@
	 Date mDate = new Date();
	 SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

	 // �ۑ������J�n
	 FileOutputStream fos = null;
	 fos = new FileOutputStream(new File(root, fileName.format(mDate) + ".jpg"));

	 // jpeg�ŕۑ�
	 mBitmap.compress(CompressFormat.JPEG, 100, fos);
	 
	 
	 // �ۑ������I��
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
 * �I�[�o�[���C�`��p�̃N���X
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
	private String yawtext = "���蒆";
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
	private String dirtext = "���蒆";
	//gaol
	private String goal="Search Charactor";
	//�摜���i�[����ϐ�
	private Bitmap myBitmap;
	//�T�E���h�Đ��f�[�^��ێ�����B
	private MediaPlayer mp;
	
	// �o�C�u���[�^�I�u�W�F�N�g��ێ�
	Vibrator vibrator;
	
//	private WebView webView;	//�ǉ�
//	private LinearLayout.LayoutParams arLayoutParams;
//	private LinearLayout arLayout;	//�ǉ�
	
	/**
	 * �R���X�g���N�^
	 * 
	 * @param context�R���e�L�X�g
	 */
	public MyView(Context context) {
		super(context);
		setFocusable(true);
		
		// �o�C�u���[�^��p�ӂ���
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		
		//Resource�C���X�^���X�̐���
		Resources res = this.getContext().getResources();
		//�摜�̓ǂݍ���(res/drawable/gclue_logo.gif)
		myBitmap = BitmapFactory.decodeResource(res,R.drawable.kuma);
		
		//�T�E���h�f�[�^��ǂݍ���(res/raw/pon.mp3)
		mp = MediaPlayer.create(context, R.raw.powerup02 );	
		
//		//WebView�i�ǉ��j
//		webView = new WebView(this);
//		webView.loadUrl("http://ishiko.myswan.ne.jp/");
//		//Web�p��LayoutParams�i�ǉ��j
//		arLayoutParams = new LinearLayout.LayoutParams(200,200);
//		arLayoutParams.setMargins(0,0,0,0);
//		//WebWiew�𒣂�t����Layout�i�ǉ��j
//		arLayout = new LinearLayout(this);
//		arLayout.addView(myBitmap,arLayoutParams);
		//WebView�𒣂�t����Layout����ʂɒ���t���i�ǉ��j
//		addContentView(arLayout,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	/**
	 * �l��n��
	 */
	public void setOrientation(float yaw, float pitch, float roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		invalidate();
	}
	/**
	 * �l��n��
	 */
	public void setOrientation(String yawtext) {
		this.yawtext = yawtext;
		this.yawtext1= yawtext1;
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
	 * �l��n��(Distance)
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
		
		// �w�i�F��ݒ�
		if(goal.equals("�o��")){
			canvas.drawColor(Color.TRANSPARENT);
		}else{
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
		
		//Bitmap�C���[�W�̕`��
		if(goal.equals("�o��")){
		canvas.drawBitmap(myBitmap,100+(direction-(yaw-180))*5,100-pitch*5,mainPaint);
		
//		//WebView�̈ʒu���ړ�
//		arLayoutParams.setMargins(100, 100, 10, 10);
//		//Layout���X�V
//		arLayout.updateViewLayout(webView, arLayoutParams);
//		vibrator.vibrate(1000);
		}
		
		//���̍Đ�
		if(goal.equals("�o��")){
			//���̍Đ��J�n�ʒu��0�~���Z�J���h�̈ʒu�ɐݒ肷��
//			mp.seekTo(0);
//			���̍Đ����J�n����
//			mp.start();
		}else{
			//�����~����
			mp.stop();
			
			//��x�Đ���stop()���Ă���Ăщ����Đ�����ꍇ�ɂ́Aprepare()���Ăяo���K�v������
			try {
				mp.prepare();
			} catch ( IllegalStateException e ) {
				e.printStackTrace();
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		// ������`��
		if(goal.equals("�o��")){
		
		canvas.drawText("���܂���I" + goal, 10, 900, subPaint);
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

		canvas.drawText("�ڕW�܂ł̋���:" + (int)distance +" m", 10, 600, mainPaint);
//		canvas.drawText("" + direction, 10, 180, mainPaint);

		canvas.drawText("�ڕW�̕���:" + dirtext, 10, 50, mainPaint);
		canvas.drawText("" + goal, 10, 800, mainPaint);
		
		// �~��`��
		canvas.drawCircle( 250, 300, 200, mainPaint );
		canvas.drawCircle( 250, 300, 100, mainPaint );
		canvas.drawRect(240, 290, 260, 310, mainPaint);
		canvas.drawLine(110, 160, 240, 290, mainPaint);
		canvas.drawLine(390, 160, 260, 290, mainPaint);
		canvas.drawCircle((-distance*(float)Math.sin(Math.toRadians(direction-(yaw-180))))/1+250, 
				(distance*(float)Math.cos(Math.toRadians(direction-(yaw-180))))/1+300, 10, subPaint);
		}
		
		//�o�C�u���[�^�𓮍삳����
		if(dirtext.equals(yawtext)){
		vibrator.vibrate(10);
		}
	}
//	public void onAcclerometerChanged(int dx,int dy,int dz){	//�ǉ�
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