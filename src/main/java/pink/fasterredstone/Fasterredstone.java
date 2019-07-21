 package pink.fasterredstone;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "fasterredstone",
        name = "Fasterredstone"
)
public class Fasterredstone {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }
}
