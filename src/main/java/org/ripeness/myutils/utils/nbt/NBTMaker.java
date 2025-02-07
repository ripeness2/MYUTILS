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
