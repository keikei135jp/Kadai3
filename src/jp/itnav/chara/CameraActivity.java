package jp.itnav.chara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import android.hardware.SensorEventListener;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class CameraActivity extends Activity {

	private MyView mView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		int param = intent.getIntExtra("ID", 0);
		
		// クラスのインスタンスを生成
		mView = new MyView(this, param);
		// Vewに設定
		addContentView(mView, new LayoutParams(LayoutParams.WRAP_CONTENT,
						+LayoutParams.WRAP_CONTENT));
	}
	
}
/**
 * CameraView
 */
class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	
	// Context mContext; //追加


	/**
	 * Cameraのインスタンスを格納する変数
	 * MyViewのインスタンスを格納する変数
	 */
	private Camera mCamera;
	private Context context;
	private View mView;

	CameraView(Context context) { // 変更
		super(context);
		this.context = context;
		// mContext = context; //追加
		// SurfaceHolder mHolder = getHolder(); //追加
		// mHolder.addCallback(this); //追加
		// mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //追加
		setDrawingCacheEnabled(true);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	/**
	 * MyViewを受け渡す
	 * @param mViewMyView
	 */
	public void setView(View mView) {
		this.mView = mView;
	}

	/**
	 * Surfaceに変化があった場合に呼ばれる
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		// Log.i("CAMERA", "surfaceChaged");

		// int num;
		// 画面設定
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

		// プレビュー表示を開始
		mCamera.startPreview();
	}

	/**
	 * Surfaceが生成された際に呼ばれる
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// カメラをOpen
		// Log.i("DEBUG", "Camera open1");
		mCamera = Camera.open();
		// Log.i("DEBUG", "Camera open2");

		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception e) { // 変更
		// mCamera.release();
		// Log.i("DEBUG", "Camera open3");
		// mCamera = null;
		}
	}

	/**
	 * Surfaceが破棄された場合に呼ばれる
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Log.i("CAMERA", "surfaceDestroyed");

		// カメラをClose
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

					// プレビュー撮影を有効にする
					mView.setDrawingCacheEnabled(true);
					Bitmap viewBitmap = Bitmap.createBitmap(mView.getDrawingCache());
					// プレビュー撮影を無効にする
					mView.setDrawingCacheEnabled(false);

					// スケールを取得
					int scale = getScale(data, viewBitmap.getWidth(),
							viewBitmap.getHeight());

					// Bitmap生成時のオプションを作成
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = scale;

					// カメラ画像からスケールの値を設定した状態でカメラ画像をBitmap形式で格納
					Bitmap myBitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length, options);

					Matrix matrix = new Matrix();
					matrix.postRotate(90);

					// 取得した画像サイズにあうサイズのBitmapを作成(何も描画されていない)
					// Bitmap tmpBitmap =
					// Bitmap.createBitmap(myBitmap.getWidth(),myBitmap.getHeight(),
					// Bitmap.Config.ARGB_8888);
					Bitmap tmpBitmap = Bitmap.createBitmap(myBitmap, 0, 0,
							myBitmap.getWidth(), myBitmap.getHeight(), matrix,
							true);

					// tmpBitmapからキャンバスを作成
					Canvas canvas = new Canvas(tmpBitmap);

					// 作成したCanvasにMyViewのプレビューとカメラの画像をはりつけ合成
					// canvas.drawBitmap(myBitmap,null,new Rect(0, 0,
					// myBitmap.getWidth(), myBitmap.getHeight()), null);
					canvas.drawBitmap(viewBitmap, null, new Rect(0, 0,
							viewBitmap.getWidth(), viewBitmap.getHeight()),
							null);

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
	 * @param data 縮尺を調査する画像データ
	 * @param width変更する横のサイズ
	 * @param height 変更する縦のサイズ
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
			// File root = Environment.getExternalStorageDirectory();
			File root = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/DCIM/");

			// 日付でファイル名を作成　
			Date mDate = new Date();
			SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMdd_HHmmss");

			// 保存処理開始
			FileOutputStream fos = null;
			fos = new FileOutputStream(new File(root, fileName.format(mDate)
					+ ".jpg"));

			// jpegで保存
			mBitmap.compress(CompressFormat.JPEG, 100, fos);

			// 保存処理終了
			fos.close();

		} catch (Exception e) {
			// Log.e("Error", "" + e.toString());
		}
		this.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
				.parse("file://" + Environment.getExternalStorageDirectory())));
	}
}

/**
 * オーバーレイ描画用のクラス
 */
class MView extends View {
	

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
	private String lat; // Lat
	private String lon; // Lon
	private float distance; // distance
	private float direction;// direction
	private String dirtext = "測定中";// dirText
	private String goal = "Search Charactor";// gaol
	private Bitmap myBitmap, myBitmap2; // 画像を格納する変数
	private MediaPlayer mp;// サウンド再生データを保持する。
	private float xp; // X軸方向位置
	private float yp; // y方向位置
	private int param;
	private String zzm;
	private String kuma;
	private float gap;
	// /**
	// * 値を渡す
	// */
	// public void setParam(int param) {
	// this.param = param;
	// invalidate();
	// }

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
	public MView(Context context, int param) {
		super(context);
		setFocusable(true);
		// String []chara = new String []{"zzm","kuma"};

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

		// //WebView（追加）
		// webView = new WebView(this);
		// webView.loadUrl("http://ishiko.myswan.ne.jp/");
		// //Web用のLayoutParams（追加）
		// arLayoutParams = new LinearLayout.LayoutParams(200,200);
		// arLayoutParams.setMargins(0,0,0,0);
		// //WebWiewを張り付けるLayout（追加）
		// arLayout = new LinearLayout(this);
		// arLayout.addView(myBitmap,arLayoutParams);
		// WebViewを張り付けたLayoutを画面に張り付け（追加）
		// addContentView(arLayout,new
		// LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	/**
	 * 値を渡す
	 */
	// public void setOrientation(float yaw, float pitch, float roll) {
	// this.yaw = yaw;
	// this.pitch = pitch;
	// this.roll = roll;
	// invalidate();
	// }
	/**
	 * 値を渡す
	 * 値を渡す(degreeDir)
	 * 値を渡す(degreeY)
	 * 値を渡す（GPS)
	 * 値を渡す(distance)
	 * 値を渡す(direction)
	 * 値を渡す(dirtext)
	 * 値を渡す(goal)
	 */
	public void setYaw(String yawtext) {
		this.yawtext = yawtext;
		// this.yawtext1= yawtext1;
		invalidate();
	}
	public void setdegree(float degreeDir) {
		this.degreeDir = degreeDir;
		invalidate();
	}
	public void setdegreeY(float degreeY) {
		this.degreeY = degreeY;
		invalidate();
	}
	public void setGps(String lat, String lon) {
		this.lat = lat;
		this.lon = lon;
		invalidate();
	}
	public void setDistance(float distance) {
		this.distance = distance;
		invalidate();
	}
	public void setDirection(float direction) {
		this.direction = direction;
		invalidate();
	}
	public void setDirtext(String dirtext) {
		this.dirtext = dirtext;
		invalidate();
	}
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
		// Log.v("widthPixels",String.valueOf(displayMetrics.widthPixels));
		// Log.v("heightPixels",String.valueOf(displayMetrics.heightPixels));

		// display();
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

			// myBitmap2= Bitmap.createScaledBitmap(myBitmap,200,200, false);
			// myBitmap2=
			// Bitmap.createScaledBitmap(myBitmap,(int)(100*(30-distance)/30+100),(int)(100*(30-distance)/30+100),
			// false);
			// canvas.drawBitmap(myBitmap2,xp,yp,mainPaint);
			// Log.i("Bitmap","degreeY:" + degreeY);
			// Log.i("Bitmap", "YP:" + yp);

			// canvas.drawBitmap(myBitmap,100+(float)((int)(direction-degreeDir)*0.4)*10,100-pitch*5,mainPaint);
			// Log.i("Bitmap", "相手角度:" + (direction));
			// Log.i("Bitmap", "自分角度:" + (degreeDir));

			// //WebViewの位置を移動
			// arLayoutParams.setMargins(100, 100, 10, 10);
		}

		// 音の再生
		if (goal.equals("登場")) {
			// 音の再生開始位置を0ミリセカンドの位置に設定する
			// mp.seekTo(0);
			// 音の再生を開始する
			// mp.start();
		} else {
			// 音を停止する
			mp.stop();

			// 一度再生をstop()してから再び音を再生する場合には、prepare()を呼び出す必要がある
			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 文字を描画
		if (goal.equals("登場")) {
			canvas.drawText("くまもん！" + goal, 10, 900, subPaint);
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

			canvas.drawText("目標までの距離:" + (int) distance + " m",
					displayMetrics.widthPixels / 8,
					displayMetrics.heightPixels * 15 / 16, mainPaint);
			// canvas.drawText("" + direction, 10, 180, mainPaint);

			canvas.drawText("目標の方向:" + dirtext, 10, 50, mainPaint);
			// canvas.drawText("" + goal, 10, 800, mainPaint);

			drawRadar(canvas, displayMetrics, mainPaint, subPaint);
		}

		// バイブレータを動作させる
		if (goal.equals("登場")) {
			vibrator.cancel();
		} else if (dirtext.equals(yawtext)) {
			vibrator.vibrate(10);
		}
	}

	// public void onAcclerometerChanged(int dx,int dy,int dz){ //追加
	// mDx = dx;
	// mDy = dy;
	// mDz = dz;
	// invalidate();
	// }

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