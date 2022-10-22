package dev.felnull.imp.client.music.media;

import com.google.gson.JsonObject;
import dev.felnull.imp.client.lava.LavaPlayerManager;
import dev.felnull.imp.client.util.NetEaseCloudMusicUtils;
import dev.felnull.imp.music.resource.ImageInfo;
import dev.felnull.imp.music.resource.MusicSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class NetEaseCloudMusicMedia implements MusicMedia {
    private static final Component MEDIA_NAME = Component.translatable("imp.loaderType.neteasecloudmusic");
    private static final Component NECM_ENTER_TEXT = Component.translatable("imp.text.enterText.neteasecloudmusic");

    @Override
    public Component getMediaName() {
        return MEDIA_NAME;
    }

    @Override
    public Component getEnterText() {
        return NECM_ENTER_TEXT;
    }

    @Override
    public ResourceLocation getIcon() {
        return null;
    }

    @Override
    public boolean isSearchable() {
        return true;
    }

    @Override
    public List<MusicMediaResult> search(String searchText) {
        try {
            List<MusicMediaResult> ret = new ArrayList<>();
            var sr = NetEaseCloudMusicUtils.getSearchSongs(searchText);

            for (JsonObject jo : sr) {
                ret.add(createResult(jo, String.valueOf(jo.get("id").getAsInt()), jo.get("dt").getAsLong()));
            }

            return ret;
        } catch (IOException | URISyntaxException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public MusicMediaResult load(String sourceName) throws Exception {
        var lm = LavaPlayerManager.getInstance();
        var otrack = lm.loadTrack(NetEaseCloudMusicUtils.getMp3Url(sourceName));
        if (otrack.isEmpty() || otrack.get().getInfo().isStream)
            return null;

        var sj = NetEaseCloudMusicUtils.getSongJson(sourceName);
        return createResult(sj, sourceName, otrack.get().getDuration());
    }

    private MusicMediaResult createResult(JsonObject songJson, String songId, long duration) {
        var naa = NetEaseCloudMusicUtils.getNameAndArtist(songJson);
        return new MusicMediaResult(new MusicSource("netease_cloud_music", songId, duration), new ImageInfo(ImageInfo.ImageType.NETEASE_CLOUD_MUSIC_PICTURE, songId), naa.getLeft(), String.join(", ", naa.getRight()));
    }
}