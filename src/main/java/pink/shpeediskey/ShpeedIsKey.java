package pink.shpeediskey;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "shpeediskey",
        name = "ShpeedIsKey",
        description = "Just a lil plugin to speed stuff up :P"
)
public class ShpeedIsKey {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }

}
