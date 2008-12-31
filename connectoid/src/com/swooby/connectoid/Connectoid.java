package com.swooby.connectoid;

import android.app.Activity;
import android.os.Bundle;

import org.apache.log4j.Logger;


public class Connectoid extends Activity {
	
	protected static Logger logger = Logger.getLogger(Connectoid.class);
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}