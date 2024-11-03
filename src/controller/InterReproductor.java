
package controller;

import model.Cancion;

public interface InterReproductor {
    
    void timeReproduction(double time);
    void timeDuration(double duration);
    void getCancion(Cancion cancion);
    void finishAudio();
    
}
