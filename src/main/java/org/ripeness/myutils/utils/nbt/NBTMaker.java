package org.ripeness.myutils.utils.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.ripeness.myutils.utils.RPNSItems;

import javax.annotation.Nullable;

public class NBTMaker {

    NBTItem itemnbt = null;
    ItemStack item = null;
    boolean ismerge = false;
    String compound = null;

    public NBTMaker(ItemStack item) {
        this.item = item;
        this.itemnbt = RPNSItems.papi.getNBT(item);
    }

    public NBTMaker(ItemStack item, boolean ismerge) {
        this.item = item;
        this.ismerge = ismerge;
        this.itemnbt = RPNSItems.papi.getNBT(item);
    }

    public NBTMaker(ItemStack item, boolean ismerge, String comp) {
        this.item = item;
        this.ismerge = ismerge;
        this.itemnbt = RPNSItems.papi.getNBT(item);
        this.compound = comp;
    }

    public NBTMaker setString(String key, String value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setString(key, value);
            } else itemnbt.getOrCreateCompound(compound).setString(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setBoolean(String key, boolean bool) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setBoolean(key, bool);
            } else itemnbt.getOrCreateCompound(compound).setBoolean(key, bool);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setInteger(String key, int integer) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setInteger(key, integer);
            } else itemnbt.getOrCreateCompound(compound).setInteger(key, integer);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setDouble(String key, double value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setDouble(key, value);
            } else itemnbt.getOrCreateCompound(compound).setDouble(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setByte(String key, byte value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setByte(key, value);
            } else itemnbt.getOrCreateCompound(compound).setByte(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setByteArray(String key, byte[] value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setByteArray(key, value);
            } else itemnbt.getOrCreateCompound(compound).setByteArray(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setObject(String key, Object value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setObject(key, value);
            } else itemnbt.getOrCreateCompound(compound).setObject(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setFloat(String key, float value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setFloat(key, value);
            } else itemnbt.getOrCreateCompound(compound).setFloat(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setItemStack(String key, ItemStack value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setItemStack(key, value);
            } else itemnbt.getOrCreateCompound(compound).setItemStack(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setLong(String key, long value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setLong(key, value);
            } else itemnbt.getOrCreateCompound(compound).setLong(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }

    public NBTMaker setShort(String key, short value) {
        if (itemnbt != null && item != null) {
            if (compound == null) {
                itemnbt.setShort(key, value);
            } else itemnbt.getOrCreateCompound(compound).setShort(key, value);
            if (ismerge) itemnbt.mergeNBT(item);
        }
        return this;
    }


    public NBTMaker setCompound(@Nullable String comp) {
        compound = comp;
        return this;
    }

    public NBTMaker removeNBT(String key) {
        if (itemnbt != null && item != null) {
            itemnbt.removeKey(key);
        } else itemnbt.getOrCreateCompound(compound).removeKey(key);
        if (ismerge) itemnbt.mergeNBT(item);
        return this;
    }

    public NBTCompound getOrCreateCompound() {
        return itemnbt.getOrCreateCompound(compound);
    }

    public NBTCompound getCompound() {
        return itemnbt.getCompound(compound);
    }

    public NBTItem getNBTItem() {
        return itemnbt;
    }

    public NBTMaker mergeNBT() {
        itemnbt.mergeNBT(item);
        return this;
    }

    public ItemStack getItem() {
        return item.clone();
    }

}
