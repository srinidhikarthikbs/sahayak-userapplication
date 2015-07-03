package com.rvce.rvce8thmile.first;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;
import java.util.Locale;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
    String res;
    Bundle extras;
    private TextToSpeech mTts;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {

        /*mTts = new TextToSpeech(this,
                (TextToSpeech.OnInitListener) this  // OnInitListener
        );*/
        mTts=new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                Log.v(TAG, "oninit");
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTts.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.v(TAG, "Language is not available.");
                    } else {

                        //sayHello("hi");

                    }
                } else {
                    Log.v(TAG, "Could not initialize TextToSpeech.");
                }

            }
        });
        mTts.setSpeechRate(0.5f);
        Log.v(TAG, "oncreate_service");

        super.onCreate();
		extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					Log.i(TAG,"Working... " + (i + 1) + "/5 @ "+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

                if(extras.containsKey("notify"))
                    if(extras.getString("notify").equals("true")){
                        sendNotification(extras.toString());
                        handletexttospeech(extras);
                    }
                    else sendNotification(bundle2string(extras));


                Log.i(TAG, "Received: " + extras.toString());




			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

    public static String bundle2string(Bundle bundle) {
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }



    public void handletexttospeech(Bundle extras)
    {
        sayHello("Your bus is about to come! your bus number is "+extras.getString("busno"));
    }

	private void sendNotification(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);


		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(extras.getString("fromwhom"))
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		//mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		Log.d(TAG, "Notification sent successfully.");
	}

    private void sayHello(String str) {
        mTts.speak(str,
                TextToSpeech.QUEUE_FLUSH,
                null);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }





}
