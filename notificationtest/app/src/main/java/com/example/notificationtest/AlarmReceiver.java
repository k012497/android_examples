package com.example.notificationtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private Boolean switchOnOff;


    @Override
    public void onReceive(Context context, Intent intent) {

        switchOnOff = intent.getBooleanExtra("switch", false);
        Intent sIntent = new Intent(context, AlarmService.class);

        if (switchOnOff == true) {
            Log.d("리시버", "on 색휘");
            //         Oreo(26) 버전 이후부터는 Background 에서 실행을 금지하기 때문에 Foreground 에서 실행해야 함
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(sIntent);
            } else {
                context.startService(sIntent);
            }
        } else {
            Log.d("리시버", "off 색휘");
            context.stopService(intent);
        }

//        switch (intent.getAction()){
//            case "on":
//                Log.d("getAction", "on");
//
//
//                // Oreo(26) 버전 이후부터는 Background 에서 실행을 금지하기 때문에 Foreground 에서 실행해야 함
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context.startForegroundService(sIntent);
//                } else {
//                    context.startService(sIntent);
//                }
//                break;
//
//            case "off":
//                Log.d("getAction", "off");
//                context.stopService(intent);
//                break;
//        }
    }
}