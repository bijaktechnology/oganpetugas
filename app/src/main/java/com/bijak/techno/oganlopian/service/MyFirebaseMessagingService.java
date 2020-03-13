/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bijak.techno.oganlopian.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.activity.DepanActivity;
import com.bijak.techno.oganlopian.activity.HomeActivity;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.bijak.techno.oganlopian.util.Constants.LOG_TAG;
import static com.bijak.techno.oganlopian.util.Constants.MESSAGE_TEXT;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        super.onMessageReceived(remoteMessage);
        Log.d(LOG_TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(LOG_TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(LOG_TAG, "FCM Data Message: " + remoteMessage.getData());
        Log.d(LOG_TAG,"FCM Notification Body: "+ remoteMessage.getNotification().getBody());
        Log.i("jumlah", String.valueOf(remoteMessage.getData().size()));
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("body");
        String badge = data.get("badge");
        /*Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setSmallIcon(R.mipmap.samkes)
                .setNumber(remoteMessage.getData().size())
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(0, notification);*/
        sendNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"),data);
    }
    private void sendNotification(String messageTitle,String messageBody,Map<String,String> data) {
        Intent intent = new Intent(this, DepanActivity.class);
        intent.putExtra("fcmData",data.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {500,500,500,500,500};

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.BLUE,1,1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
