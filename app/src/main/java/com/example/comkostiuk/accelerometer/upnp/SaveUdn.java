package com.example.comkostiuk.accelerometer.upnp;

import android.os.Build;
import android.os.Environment;

import org.fourthline.cling.model.types.UDN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

import xdroid.toaster.Toaster;

/**
 * Created by mkostiuk on 14/06/2017.
 */

public class SaveUdn {

        public UDN getUdn() throws IOException {

            UDN ret;
            File fi;

            if (Build.BRAND.toString().equals("htc_europe"))
                fi = new File("/mnt/emmc/AndroidAccelerometer/udn.txt");
            else
                fi = new File(Environment.getExternalStorageDirectory().getPath() + "/AndroidAccelerometer/udn.txt");

            if (!fi.exists()) {
                fi.createNewFile();
                fi.setWritable(true);
            }

            InputStream is = new FileInputStream(fi);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();


            if ((line != null) || (line == "")) {
                ret = UDN.valueOf(line);
            } else {
                ret = new UDN(UUID.randomUUID());
                OutputStream o = new FileOutputStream(fi);
                o.write(ret.toString().getBytes());
            }
            Toaster.toast("UDN: " + ret.toString());
            return ret;
        }

}
