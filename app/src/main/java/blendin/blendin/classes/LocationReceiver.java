/*
* Interface that assures that PostActivity and NewPostActivity implement recieveLocation()
* to recieve location from LocationRequester
*/

package blendin.blendin.classes;

import android.location.Location;

public interface LocationReceiver {

    void receiveLocation(Location location);

}
