package com.swooby.connectoid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Connect extends Activity {

	private static final String TAG = "Connect";

	private static final int DIALOG_OK_MESSAGE = 1;
	
	private EditText mComputer;
	private EditText mUsername;
	private EditText mPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "->onCreate");

		super.onCreate(savedInstanceState);
		
		// Landscape looks bad w/ title
		// TODO(pv): How to get the size proper w/o title
		//requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.connect);

		mComputer = (EditText)findViewById(R.id.edit_computer);
		mUsername = (EditText)findViewById(R.id.edit_username);
		mPassword = (EditText)findViewById(R.id.edit_password);
		
		// TODO(pv): Set above values to any previous or active value(s)

		Button buttonConnect = (Button)findViewById(R.id.button_connect);
		buttonConnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("computer", mComputer.getText().toString());
				bundle.putString("username", mUsername.getText().toString());
				bundle.putString("password", mPassword.getText().toString());
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		Button buttonCancel = (Button)findViewById(R.id.button_cancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});

		Log.i(TAG, "<-onCreate");
	}
}
