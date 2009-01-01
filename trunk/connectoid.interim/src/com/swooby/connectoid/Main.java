package com.swooby.connectoid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class Main extends Activity {

	private static final String TAG = "Main";

	private static final int CONNECT_ACTIVITY = 1;

	private static final int CONNECT_ID = Menu.FIRST;
	private static final int DISCONNECT_ID = Menu.FIRST+1;

	private MyView mView;

	private boolean connected = false;
	private String versionName = "unknown";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "->onCreate");

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mView = new MyView(this);
		setContentView(mView);
		mView.requestFocus();

		initialize();

		if (!connected) {
			showConnect();
		}

		Log.i(TAG, "<-onCreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, CONNECT_ID, 0, R.string.menu_connect);
		menu.add(0, DISCONNECT_ID, 0, R.string.menu_disconnect);
		// TODO(pv): *FULL* Screen (no status bar even)
		// TODO(pv): Screen capture
		return result;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case CONNECT_ID:
			showConnect();
			return true;
		case DISCONNECT_ID:
			doDisconnect();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		Bundle data = intent.getExtras();
		switch (requestCode) {
		case CONNECT_ACTIVITY:
			if (resultCode == RESULT_OK) {
				String computer = data.getString("computer");
				String username = data.getString("username");
				String password = data.getString("password");
				doConnect(computer, username, password);
			} else {
				doDisconnect();
			}
			break;
		}
	}

	private void initialize() {
		try {
			PackageManager pm = getPackageManager();
			String packageName = getPackageName();
			PackageInfo pi = pm.getPackageInfo(packageName, 0);
			versionName = pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String java = System.getProperty("java.specification.version");
		String os = System.getProperty("os.name");
		String osver = System.getProperty("os.version");
		Log.i(TAG, "Connectiod version is " + versionName);
		Log.i(TAG, "Java version is " + java);
		Log.i(TAG, "Operating System is " + os + " version " + osver);
		
		// TODO(pv): Fill the screen to a unique pattern
		mView.clear();
	}

	private void showConnect() {
		Intent intent = new Intent(this, Connect.class);
		startActivityForResult(intent, CONNECT_ACTIVITY);
	}

	private void doConnect(String computer, String username, String password) {
		// TODO(pv): Connect...
		connected = true;
		mView.clear();
	}

	private void doDisconnect() {
		// TODO(pv): Disconnect...
		connected = false;
		mView.clear();
	}

	// TODO(pv): Improved responsiveness via threading?
	// http://code.google.com/android/kb/commontasks.html#threading
	// http://code.google.com/android/toolbox/responsiveness.html

	// Need handler for callbacks to the UI thread
	final Handler mHandler = new Handler();

	// Create runnable for posting
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			updateResultsInUi();
		}
	};

	protected void startLongRunningOperation() {
		// Fire off a thread to do some work that we shouldn't do directly in
		// the UI thread
		Thread t = new Thread() {
			public void run() {
				// mResults = doSomethingExpensive();
				mHandler.post(mUpdateResults);
			}
		};
		t.start();
	}

	private void updateResultsInUi() {
		// Back in the UI thread -- update our UI elements based on the data in
		// mResults
	}

	
	public class MyView extends View {
		private static final int FADE_ALPHA = 0x06;
		private static final int MAX_FADE_STEPS = 256 / FADE_ALPHA + 4;
		private Bitmap mBitmap;
		private Canvas mCanvas;
		private final Rect mRect = new Rect();
		private final Paint mPaint;
		private final Paint mFadePaint;
		private boolean mCurDown;
		private int mCurX;
		private int mCurY;
		private float mCurPressure;
		private float mCurSize;
		private int mCurWidth;
		private int mFadeSteps = MAX_FADE_STEPS;

		public MyView(Context c) {
			super(c);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setARGB(255, 255, 255, 255);
			mFadePaint = new Paint();
			mFadePaint.setDither(true);
			mFadePaint.setARGB(FADE_ALPHA, 0, 0, 0);
		}

			
		public void clear() {
			clear(0xff, 0, 0, 0);
		}
		
		public void clear(int a, int r, int g, int b) {
			if (mCanvas != null) {
				mPaint.setARGB(a, r, g, b);
				mCanvas.drawPaint(mPaint);
				invalidate();
				mFadeSteps = MAX_FADE_STEPS;
			}
		}

		public void fade() {
			if (mCanvas != null && mFadeSteps < MAX_FADE_STEPS) {
				mCanvas.drawPaint(mFadePaint);
				invalidate();
				mFadeSteps++;
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			int curW = mBitmap != null ? mBitmap.getWidth() : 0;
			int curH = mBitmap != null ? mBitmap.getHeight() : 0;
			if (curW >= w && curH >= h) {
				return;
			}

			if (curW < w)
				curW = w;
			if (curH < h)
				curH = h;

			Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
					Bitmap.Config.RGB_565);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (mBitmap != null) {
				newCanvas.drawBitmap(mBitmap, 0, 0, null);
			}
			mBitmap = newBitmap;
			mCanvas = newCanvas;
			mFadeSteps = MAX_FADE_STEPS;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (mBitmap != null) {
				canvas.drawBitmap(mBitmap, 0, 0, null);
			}
		}

		@Override
		public boolean onTrackballEvent(MotionEvent event) {
			boolean oldDown = mCurDown;
			mCurDown = true;
			int N = event.getHistorySize();
			int baseX = mCurX;
			int baseY = mCurY;
			final float scaleX = event.getXPrecision();
			final float scaleY = event.getYPrecision();
			for (int i = 0; i < N; i++) {
				// Log.i("TouchPaint", "Intermediate trackball #" + i
				// + ": x=" + event.getHistoricalX(i)
				// + ", y=" + event.getHistoricalY(i));
				drawPoint(baseX + event.getHistoricalX(i) * scaleX, baseY
						+ event.getHistoricalY(i) * scaleY, event
						.getHistoricalPressure(i), event.getHistoricalSize(i));
			}
			// Log.i("TouchPaint", "Trackball: x=" + event.getX()
			// + ", y=" + event.getY());
			drawPoint(baseX + event.getX() * scaleX, baseY + event.getY()
					* scaleY, event.getPressure(), event.getSize());
			mCurDown = oldDown;
			return true;
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			int action = event.getAction();
			mCurDown = action == MotionEvent.ACTION_DOWN
					|| action == MotionEvent.ACTION_MOVE;
			int N = event.getHistorySize();
			for (int i = 0; i < N; i++) {
				// Log.i("TouchPaint", "Intermediate pointer #" + i);
				drawPoint(event.getHistoricalX(i), event.getHistoricalY(i),
						event.getHistoricalPressure(i), event
								.getHistoricalSize(i));
			}
			drawPoint(event.getX(), event.getY(), event.getPressure(), event
					.getSize());
			return true;
		}

		private void drawPoint(float x, float y, float pressure, float size) {
			// Log.i("TouchPaint", "Drawing: " + x + "x" + y + " p="
			// + pressure + " s=" + size);
			mCurX = (int) x;
			mCurY = (int) y;
			mCurPressure = pressure;
			mCurSize = size;
			mCurWidth = (int) (mCurSize * (getWidth() / 3));
			if (mCurWidth < 1)
				mCurWidth = 1;
			if (mCurDown && mBitmap != null) {
				int pressureLevel = (int) (mCurPressure * 255);
				mPaint.setARGB(pressureLevel, 255, 255, 255);
				mCanvas.drawCircle(mCurX, mCurY, mCurWidth, mPaint);
				mRect.set(mCurX - mCurWidth - 2, mCurY - mCurWidth - 2, mCurX
						+ mCurWidth + 2, mCurY + mCurWidth + 2);
				invalidate(mRect);
			}
			mFadeSteps = 0;
		}
	}
}