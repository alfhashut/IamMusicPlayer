package red.felnull.imp.client.music.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import red.felnull.imp.IamMusicPlayer;
import red.felnull.imp.api.client.IMPClientRegistry;
import red.felnull.imp.client.music.MusicEngine;
import red.felnull.imp.client.music.player.IMusicPlayer;
import red.felnull.imp.client.music.subtitle.IMusicSubtitle;
import red.felnull.imp.client.music.subtitle.SubtitleLoaderThread;
import red.felnull.imp.client.music.subtitle.SubtitleSystem;
import red.felnull.imp.music.info.MusicPlayInfo;
import red.felnull.imp.music.resource.MusicSource;
import red.felnull.imp.packet.MusicResponseMessage;
import red.felnull.otyacraftengine.util.IKSGPacketUtil;

import java.util.UUID;

public class MusicLoaderThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(MusicLoaderThread.class);
    private final UUID uuid;
    private final MusicSource location;
    private final long startPosition;
    private final long startTime;
    private final boolean autPlay;
    private final MusicPlayInfo autPlayInfo;
    private boolean stop;

    public MusicLoaderThread(UUID uuid, MusicSource location, long startPosition, boolean autPlay, MusicPlayInfo autPlayInfo) {
        this.setName("Music Loader Thread: " + location.getIdentifier());
        this.uuid = uuid;
        this.location = location;
        this.startPosition = startPosition;
        this.autPlay = autPlay;
        this.autPlayInfo = autPlayInfo;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (!IMPClientRegistry.isLoaderContains(location.getLoaderName())) {
            LOGGER.error("Non existent music loader: " + location.getLoaderName());
            if (stop || autPlay)
                return;
            IKSGPacketUtil.sendToServerPacket(new MusicResponseMessage(MusicResponseMessage.Type.READY_FAILURE, uuid, 0));
            return;
        }

        IMusicLoader loader = IMPClientRegistry.getLoader(location.getLoaderName());

        IMusicPlayer player = null;
        long startTime = System.currentTimeMillis();
        try {
            player = loader.createMusicPlayer(location);
            player.ready(startPosition);
        } catch (Exception ex) {
            LOGGER.error("Failed to load music: " + location.getIdentifier(), ex);
            if (!stop && !autPlay)
                IKSGPacketUtil.sendToServerPacket(new MusicResponseMessage(MusicResponseMessage.Type.READY_FAILURE, uuid, 0));
            if (player != null)
                player.destroy();
            return;
        }

        long eqTime = System.currentTimeMillis() - startTime;

        IMusicSubtitle subtitle = IamMusicPlayer.CONFIG.subtitleSystem == SubtitleSystem.OFF ? null : loader.createMusicSubtitle(player, location);

        if (subtitle != null) {
            SubtitleLoaderThread slt = new SubtitleLoaderThread(uuid, location, subtitle, () -> stop);
            slt.start();
        }

        if (!stop && !autPlay)
            IKSGPacketUtil.sendToServerPacket(new MusicResponseMessage(MusicResponseMessage.Type.READY_COMPLETE, uuid, eqTime));
        if (stop)
            return;
        MusicEngine.getInstance().musicPlayers.put(uuid, new MusicEngine.MusicPlayingEntry(player, null));
        MusicEngine.getInstance().loaders.remove(uuid);
        if (autPlay && autPlayInfo != null)
            MusicEngine.getInstance().play(uuid, eqTime, autPlayInfo);
    }

    public void stopped() {
        stop = true;
    }

    public MusicSource getLocation() {
        return location;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public long getCurrentDelayStartPosition() {
        return startPosition + (System.currentTimeMillis() - startTime);
    }

    public MusicPlayInfo getAutPlayInfo() {
        return autPlayInfo;
    }
}
