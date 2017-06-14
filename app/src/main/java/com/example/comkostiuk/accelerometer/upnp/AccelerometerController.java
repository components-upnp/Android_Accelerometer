package com.example.comkostiuk.accelerometer.upnp;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

import java.beans.PropertyChangeSupport;

/**
 * Created by comkostiuk on 14/04/2017.
 */


/*Description du service UPnP offert par le composant Accelerometer
* */
@UpnpService(
        serviceId =  @UpnpServiceId("AccelerometerService"),
        serviceType = @UpnpServiceType(value = "AccelerometerService", version = 1)
)
public class AccelerometerController {

    private final PropertyChangeSupport propertyChangeSupport;

    public AccelerometerController() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(name = "Direction",
    sendEvents = true,
    datatype = "String")
    private String direction = "AUCUN";

    @UpnpAction
    public void setDirection(@UpnpInputArgument(name = "NewDirectionValue") String newDirectionValue) {
        String directionOldValue = direction;

        direction = newDirectionValue;

        getPropertyChangeSupport().firePropertyChange("Direction", directionOldValue, direction);
    }


    @UpnpAction(out = @UpnpOutputArgument(name = "ResultDirection"))
    public String getDirection() {
        return direction;
    }
}
