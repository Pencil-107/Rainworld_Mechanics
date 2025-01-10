package pencil.mechanics.rain;

import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import pencil.mechanics.ConfigValues;

public class RainManager {

    public static  boolean raining = false;
    float rainBuildup = ConfigValues.rainTimer;

    void rainKill () {
        rainBuildup = ConfigValues.rainTimer;

    }

    public void load (World world) {

        if (raining) {
            world.setRainGradient(1);
            if (rainBuildup > 0) {
                rainBuildup--;
            } else if (rainBuildup <= 0) {
                rainKill();
            }
        }
    }

}
