package com.example.comkostiuk.accelerometer.upnp;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UDN;

/**
 * Created by comkostiuk on 14/04/2017.
 */

public class Service {
    private AndroidUpnpService upnpService;
    private UDN udnAccelerometer;
    private ServiceConnection serviceConnection;


    public Service() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                upnpService = (AndroidUpnpService) service;

                LocalService<AccelerometerController> accelerometerControllerLocalService = getAccelerometerService();

                if (accelerometerControllerLocalService == null) {
                    try {
                        udnAccelerometer = new SaveUdn().getUdn();
                        LocalDevice remoteDevice = AccelerometerDevice.createDevice(udnAccelerometer);

                        upnpService.getRegistry().addDevice(remoteDevice);

                    } catch (Exception ex) {
                        System.err.println("Creating Android remote controller device failed !!!" );
                        ex.printStackTrace();
                        return;
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                    upnpService = null;
            }
        };
    }

    public LocalService<AccelerometerController> getAccelerometerService() {
        if (upnpService == null)
            return null;

        LocalDevice remoteDevice;
        if ((remoteDevice = upnpService.getRegistry().getLocalDevice(udnAccelerometer, true)) == null)
            return null;

        return (LocalService<AccelerometerController>)
                remoteDevice.findService(new UDAServiceType("AccelerometerService", 1));
    }

    public ServiceConnection getService() {
        return serviceConnection;
    }

    public UDN getUdnAccelerometer() {
        return udnAccelerometer;
    }
}
