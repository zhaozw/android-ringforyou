package com.zgy.ringforu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.zgy.ringforu.RingForU;
import com.zgy.ringforu.config.ConfigCanstants;
import com.zgy.ringforu.config.MainConfig;
import com.zgy.ringforu.tools.smslightscreen.SmsLightScreenUtil;
import com.zgy.ringforu.util.MainUtil;
import com.zgy.ringforu.util.PhoneUtil;
import com.zgy.ringforu.util.StringUtil;

public class MsgReceiver extends BroadcastReceiver {

	private static final String TAG = "MsgReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		MainConfig mainConfig = MainConfig.getInstance();
		String strAllNumsImportant = mainConfig.getImporantNumbers();
		String strAllNumsSms = mainConfig.getInterceptSmsNumbers();

		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
			SmsMessage[] msg = null;

			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdusObj = (Object[]) bundle.get("pdus");
				msg = new SmsMessage[pdusObj.length];
				int mmm = pdusObj.length;
				for (int i = 0; i < mmm; i++)
					msg[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			}

			// String msgTxt = "";
			// int msgLength = msg.length;
			// for (int i = 0; i < msgLength; i++) {
			// msgTxt += msg[i].getMessageBody();
			// }

			// ��÷����˺���
			String getFromNum = "";
			for (SmsMessage currMsg : msg) {
				getFromNum = currMsg.getDisplayOriginatingAddress();
			}
			if (RingForU.DEBUG)
				Log.v(TAG, getFromNum);
			getFromNum = StringUtil.getRidofSpeciall(getFromNum);
			if (strAllNumsImportant != null && strAllNumsImportant.contains(getFromNum)) {
				if (RingForU.DEBUG)
					Log.e(TAG, "check screen light");
				SmsLightScreenUtil.checkSmsLightScreenOn(context);// ������Ļ���
				if (MainUtil.isEffective(context)) {
					PhoneUtil.turnUpMost(context);
				} else {
					if (RingForU.DEBUG)
						Log.v(TAG, "not effective!");
				}
			} else if (strAllNumsSms != null && strAllNumsSms.contains(getFromNum)) {
				// ���ζ��ţ�
				switch (MainConfig.getInstance().getInterceptSmsStyle()) {
				case ConfigCanstants.STYLE_INTERCEPT_SMS_SLIENT:
					if (RingForU.DEBUG)
						Log.e(TAG, "hide sms slient!");
					// ��������
					PhoneUtil.turnDownThenUp(context);
					break;
				case ConfigCanstants.STYLE_INTERCEPT_SMS_DISRECEIVE:
					if (RingForU.DEBUG)
						Log.e(TAG, "hide sms abort broadcast!");
					// ���洢����
					abortBroadcast();
					setResultData(null);
					break;
				default:
					break;
				}
			} else {
				if (RingForU.DEBUG)
					Log.e(TAG, "check screen light");
				SmsLightScreenUtil.checkSmsLightScreenOn(context);// ������Ļ���
			}
		}
	}

}
