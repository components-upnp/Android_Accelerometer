package com.example.comkostiuk.accelerometer.main;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.comkostiuk.accelerometer.upnp.AccelerometerController;
import com.example.comkostiuk.accelerometer.xml.GenerateurXml;

import org.fourthline.cling.model.meta.LocalService;

import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * Created by mkostiuk on 14/06/2017.
 */

public class SensorListener implements SensorEventListener {

    private GenerateurXml gen;
    private LocalService<AccelerometerController> accelerometerService;
    private String udn;
    private boolean emit;
    private String commande, commandeAucun;

    public SensorListener(LocalService<AccelerometerController> a, String u) {
        accelerometerService = a;
        gen = new GenerateurXml();
        udn = u;
        emit = true;
        try {
            commandeAucun = gen.getDocXml(udn, Direction.AUCUN.toString());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Direction direction = Direction.AUCUN;


        if (event.values[0] < -4.5 ){
            direction = Direction.DROITE;
            System.out.println("DROITE");
        }
        else if (event.values[0] > 4.5 ){
            direction = Direction.GAUCHE;
            System.out.println("GAUCHE");
        }

            /*if (event.values[1] > 4.5 && (direction != Direction.AUCUN) ){
                direction = Direction.BAS;
                System.out.println("BAS");
            }
            else if ((event.values[1] < -4.5 && (direction != Direction.AUCUN)) ){
                direction = Direction.HAUT;
                System.out.println("HAUT");
            }*/

        //On envoie la nouvelle direction, en ajoutant un timer afin de ne pas le saturer.
        //On aura au maximum un événement toute les secondes.
        //On envoie un événement avec une direction "AUCUN" afin d'opérer un changement de valeur,
        //dans le cas contraire on ne pourrait pas faire deux fois la même direction.
        if ((accelerometerService != null) && emit && (direction != Direction.AUCUN)) {
            emit = false;
            try {
                commande = gen.getDocXml(udn, direction.toString());
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
            accelerometerService.getManager().getImplementation().setDirection(commande);
            accelerometerService.getManager().getImplementation().setDirection(commandeAucun);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    emit = true;
                }
            },1000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.err.println("Changement précision!!!!");
    }
}
