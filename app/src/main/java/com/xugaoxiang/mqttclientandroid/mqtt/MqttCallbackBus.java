package com.xugaoxiang.mqttclientandroid.mqtt;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

/**
 * 使用EventBus分发事件
 */
public class MqttCallbackBus implements MqttCallback {
    private static final String TAG = MqttCallbackBus.class.getCanonicalName();

    @Override
    public void connectionLost(Throwable cause) {
        Log.e(TAG,cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.d(TAG,topic + "====" + message.toString());
        EventBus.getDefault().post(message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
