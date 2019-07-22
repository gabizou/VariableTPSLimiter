package pink.shpeediskey.mixin;

import net.minecraft.crash.CrashReport;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ReportedException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.minecraft.server.MinecraftServer.getCurrentTimeMillis;

@Mixin(value = MinecraftServer.class, priority = 500)
public abstract class MixinMinecraftServer {
    @Shadow public abstract boolean init();
    @Shadow @Final private final ServerStatusResponse statusResponse = new ServerStatusResponse();
    @Shadow public abstract void applyServerIconToResponse(ServerStatusResponse statusResponse2);
    @Shadow private String motd;
    @Shadow private boolean serverRunning = true;
    @Shadow protected abstract void finalTick(CrashReport crashReport);
    @Shadow public abstract CrashReport addServerInfoToCrashReport(CrashReport crashReport);
    @Shadow public abstract File getDataDirectory();
    @Shadow private boolean serverStopped;
    @Shadow public abstract void systemExitNow();
    @Shadow protected abstract void stopServer();
    @Shadow private boolean serverIsRunning;

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * @author Me / Mogang?
     * @reason Uuuhhhhh Cuz I need it
     */
    @Overwrite
    public void run()
    {
        try
        {
            if (this.init())
            {
                long currentTime = getCurrentTimeMillis();
                long i = 0L;
                this.statusResponse.setServerDescription(new TextComponentString(this.motd));
                this.statusResponse.setVersion(new ServerStatusResponse.Version("1.12.2", 340));
                this.applyServerIconToResponse(this.statusResponse);

                while (this.serverRunning)
                {
                    long k = getCurrentTimeMillis();
                    long j = k - currentTime;
                    i += j;
                    currentTime = k;
                    this.tick();
                    this.serverIsRunning = true;
                }
            }
            else
            {
                this.finalTick((CrashReport)null);
            }
        }
        catch (Throwable throwable1)
        {
            LOGGER.error("Encountered an unexpected exception", throwable1);
            CrashReport crashreport = null;

            if (throwable1 instanceof ReportedException)
            {
                crashreport = this.addServerInfoToCrashReport(((ReportedException)throwable1).getCrashReport());
            }
            else
            {
                crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
            }

            File file1 = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.saveToFile(file1))
            {
                LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
            }
            else
            {
                LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.finalTick(crashreport);
        }
        finally
        {
            try
            {
                this.serverStopped = true;
                this.stopServer();
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Exception stopping the server", throwable);
            }
            finally
            {
                this.systemExitNow();
            }
        }
    }


    @Shadow private int tickCounter;
    @Shadow public final Profiler profiler = new Profiler();
    @Shadow public abstract void updateTimeLightAndEntities();
    @Shadow public final long[] tickTimeArray = new long[100];
    @Shadow private PlayerList playerList;
    @Shadow protected abstract void saveAllWorlds(boolean isSilent);

    @Shadow private int serverPort;

    /**
     * @author Me / Mogang?
     * @reason Uuuhhhhh Cuz I need it
     */
    @Overwrite
    protected void tick()
    {
        long i = System.nanoTime();
        ++this.tickCounter;
        this.updateTimeLightAndEntities();
        if (this.tickCounter % 900 == 0)
        {
            this.playerList.saveAllPlayerData();
            this.saveAllWorlds(true);
            System.out.println("saved the world");
        }
        this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - i;

    }
}