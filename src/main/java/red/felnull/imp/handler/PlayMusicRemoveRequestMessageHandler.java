package red.felnull.imp.handler;

import net.minecraftforge.fml.network.NetworkEvent;
import red.felnull.imp.data.PlayMusicManeger;
import red.felnull.imp.packet.PlayMusicRemoveRequestMessage;

import java.util.function.Supplier;

public class PlayMusicRemoveRequestMessageHandler {
    public static void reversiveMessage(PlayMusicRemoveRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
        PlayMusicManeger.instance().removePlayMusic(message.name);
    }
}