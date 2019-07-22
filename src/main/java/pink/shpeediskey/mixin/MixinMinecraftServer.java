package pink.shpeediskey.mixin;

import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import static net.minecraft.server.MinecraftServer.getCurrentTimeMillis;

@Mixin(value = MinecraftServer.class, priority = 500)
public abstract class MixinMinecraftServer {

    @ModifyConstant(method = "run", constant = @Constant(longValue = 2000L))
    private long tickDelimiter$provideLongMaxToIgnoreLogger(final long incomingConstant) {
        return Long.MAX_VALUE;
    }

    @ModifyConstant(
        method = "run",
        constant = @Constant(longValue = 0L),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/server/MinecraftServer;timeOfLastWarning:J",
                opcode = Opcodes.PUTFIELD
            ),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;areAllPlayersAsleep()Z")
        )
    )
    private long tickDelimiter$ignoreTimeRunningBackwardsCheck(final long zeroLong) {
        return Long.MIN_VALUE;
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;areAllPlayersAsleep()Z"))
    private boolean tickDelimiter$ignorePlayerSleeping(final WorldServer worldServer) {
        return true; // We always return true to cause a tick to be called
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V"))
    private void tickDelimiter$doNotThreadSleep(final long value) {
        // Do nothing, we've redirected the sleeping call
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;startProfiling:Z"))
    private boolean tickDelimiter$ignoreProfiling(final MinecraftServer self) {
        return false; // Always false for profiling
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V"))
    private void tickDelimiter$ignoreProfilingStart(final Profiler profiler, final String name) {
        // do nothing
    }

    @ModifyConstant(method = "tick", constant = @Constant(longValue = 5000000000L))
    private long tickDelimiter$lowerValueToIgnoreServerResponse() {
        return Long.MAX_VALUE; // prevents the if statement to send a server response
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Profiler;endSection()V"))
    private void tickDelimiter$ignoreEndSection(final Profiler profiler) {
        // do nothing
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Snooper;isSnooperRunning()Z"))
    private boolean tickDelimiter$ignoreSnooperAsWell(final Snooper snooper) {
        return true;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/Snooper;addMemoryStatsToSnooper()V"))
    private void tickDelimiter$ignoreMemorySnooper(final Snooper snooper) {
        // do nothing
    }
}
