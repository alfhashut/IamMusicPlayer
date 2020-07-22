package red.felnull.imp.block;

import net.minecraft.util.IStringSerializable;

public enum CassetteDeckStates implements IStringSerializable {
    NONE("none"), RECORD("record"), PLAY("play"), STOP("stop"), DELETE("delete"), COPY("copy");

    private final String name;

    CassetteDeckStates(String string) {
        this.name = string;
    }

    @Override
    public String func_176610_l() {

        return name;
    }

}