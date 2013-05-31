package com.zgy.ringforu.tools.signalreconnect;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zgy.ringforu.tools.disablegprs.DisableGprsService;


public class SignalReconnectService extends Service {

	
	private static final String TAG = "SignalReconnectService";
	
	private TelephonyManager mTelephonyMgr;
	private SignalStrengthListener myListenter;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.e(TAG, "SignalReconnectService is start!");
		if(SignalReconnectUtil.isSignalReconnectOn()) {
			mTelephonyMgr = (TelephonyManager) getSystemService(DisableGprsService.TELEPHONY_SERVICE);
			myListenter = new SignalStrengthListener();
			mTelephonyMgr.listen(myListenter, PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
		} else {
			stopSelf();
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "SignalReconnectService is destroy!");
		if(mTelephonyMgr != null && myListenter != null) {
			mTelephonyMgr.listen(myListenter, PhoneStateListener.LISTEN_NONE);
			mTelephonyMgr = null;
			myListenter = null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

 
	
	private class SignalStrengthListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthChanged(int asu) {
			// TODO Auto-generated method stub
			super.onSignalStrengthChanged(asu);
			if(asu < 10) {
				SignalReconnectUtil.doSignalReconnect(SignalReconnectService.this);
			}
			Log.e(TAG, "asu = " + asu);
		}

		
		
	}

}