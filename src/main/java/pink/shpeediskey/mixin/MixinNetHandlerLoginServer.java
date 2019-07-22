package pink.shpeediskey.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.network.NetHandlerLoginServer;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = NetHandlerLoginServer.class, priority = 500)
public abstract class MixinNetHandlerLoginServer {

    @Shadow private int connectionTimer;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void vanilla$update(CallbackInfo ci) {
        this.connectionTimer--;
    }
}
