package org.ripeness.myutils.utils.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.ripeness.myutils.muc;

import java.net.URL;
import java.util.*;

@SuppressWarnings("UnusedReturnValue")
public class ItemBuilder {
    private final ItemStack item;
    private ItemMeta meta;

    /**
     * Verilen iki item'ın config'deki tanımlı özelliklere göre eşleşip eşleşmediğini kontrol eder.
     * İlk item (checkItem) ikinci item'ın (configItem) tanımlı özelliklerine sahip olmalıdır.
     *
     * @param checkItem  Kontrol edilecek item
     * @param configItem Config'de tanımlı referans item
     * @return boolean Eşleşme durumu
     */
    public static boolean matchItems(ItemStack checkItem, ItemStack configItem) {
        // Null kontrolü
        if (checkItem == null || configItem == null) {
            return false;
        }

        // Tip kontrolü - Bu her zaman kontrol edilmeli
        if (checkItem.getType() != configItem.getType()) {
            return false;
        }

        // ItemMeta kontrolü
        ItemMeta checkMeta = checkItem.getItemMeta();
        ItemMeta configMeta = configItem.getItemMeta();

        // Config item'ında meta yoksa sadece tip kontrolü yeterli
        if (configMeta == null) {
            return true;
        }

        // Display name kontrolü
        if (configMeta.hasDisplayName()) {
            if (!checkMeta.hasDisplayName()) {
                return false;
            }
            if (!checkMeta.getDisplayName().equals(configMeta.getDisplayName())) {
                return false;
            }
        }

        // Lore kontrolü
        if (configMeta.hasLore()) {
            if (!checkMeta.hasLore()) {
                return false;
            }
            if (!checkMeta.getLore().equals(configMeta.getLore())) {
                return false;
            }
        }

        // Custom Model Data kontrolü
        if (configMeta.hasCustomModelData()) {
            if (!checkMeta.hasCustomModelData()) {
                return false;
            }
            if (checkMeta.getCustomModelData() != configMeta.getCustomModelData()) {
                return false;
            }
        }

        // Enchantment kontrolü
        for (Map.Entry<Enchantment, Integer> enchant : configMeta.getEnchants().entrySet()) {
            if (!checkMeta.hasEnchant(enchant.getKey())) {
                return false;
            }
            if (checkMeta.getEnchantLevel(enchant.getKey()) != enchant.getValue()) {
                return false;
            }
        }

        return true;
    }

    public static boolean isSameItem(ItemStack item1, ItemStack item2) {
        boolean result = true;
        if (item1 == null || item2 == null) return false;
        if (item1.getType() != item2.getType()) result = false;

        ItemMeta meta1 = item1.getItemMeta();
        ItemMeta meta2 = item2.getItemMeta();

        if (meta1 == null || meta2 == null) return false;

        // Temel özellikleri karşılaştır

        if (!Objects.deepEquals(meta1.getDisplayName(), meta2.getDisplayName())) result = false;
        if (!Objects.deepEquals(meta1.getLore(), meta2.getLore())) result = false;

        // Custom Model Data kontrolü
        if (meta1.hasCustomModelData() != meta2.hasCustomModelData()) result = false;
        if (meta1.hasCustomModelData() && meta1.getCustomModelData() != meta2.getCustomModelData()) result = false;

        if (!Objects.deepEquals(ItemBuilder.hasGlowingEffect(item1), ItemBuilder.hasGlowingEffect(item2)))
            result = false;


//        if (meta1 instanceof SkullMeta && meta2 instanceof SkullMeta) {
//            SkullMeta skull1 = (SkullMeta) meta1;
//            SkullMeta skull2 = (SkullMeta) meta2;
//
//            PlayerProfile profile1 = skull1.getOwnerProfile();
//            PlayerProfile profile2 = skull2.getOwnerProfile();
////            Bukkit.broadcastMessage("head start");
//
////            Bukkit.broadcastMessage(profile2.getTextures().getSkin().toString());
////            Bukkit.broadcastMessage(profile1.getTextures().getSkin().toString());
//
//            if (profile1 != null && profile2 != null) {
////                Bukkit.broadcastMessage("head test");
//                result = !Objects.equals(profile1.getTextures().getSkin(), profile2.getTextures().getSkin());
//            } else return false;
//    }

        return result;
    }

    public static boolean hasGlowingEffect(ItemStack itemStack) {
        return itemStack.getEnchantments().containsKey(Enchantment.KNOCKBACK);
    }

    public static boolean hasGlowingEffectAll(ItemStack itemStack) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (itemStack.getEnchantments().containsKey(enchantment)) {
                return true;
            }
        }
        return false;
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack is) {
        this.item = is;
        this.meta = is.getItemMeta();
    }

    public ItemBuilder(Material material, short sh) {
        this.item = new ItemStack(material, 1, sh);
        this.meta = item.getItemMeta();
    }

    public ItemMeta getItemMeta() {
        return this.meta;
    }

    public ItemBuilder setColor(Color color) {
        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            leatherMeta.setColor(color);
        }
        return this;
    }

    public ItemBuilder setType(Material mat) {
        item.setItemMeta(meta);
        item.setType(mat);
        meta = item.getItemMeta();
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            Iterator<Enchantment> var4 = meta.getEnchants().keySet().iterator();
            while (var4.hasNext()) {
                Enchantment enchantment = var4.next();
                meta.removeEnchant(enchantment);
            }
        }
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        if (data != -1) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    public ItemBuilder setHead(String owner) {
        if (owner == null) return this;
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(owner);
        }
        return this;
    }

    public ItemBuilder setHeadTextureValue(String value) {
        if (!(meta instanceof SkullMeta) || value == null) return this;

        SkullMeta skullMeta = (SkullMeta) meta;
        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();

            byte[] decodedBytes = Base64.getDecoder().decode(value);
            String decodedString = new String(decodedBytes);
            JsonObject jsonObject = new Gson().fromJson(decodedString, JsonObject.class);
            String url = jsonObject.getAsJsonObject("textures")
                    .getAsJsonObject("SKIN")
                    .get("url").getAsString();

            textures.setSkin(new URL(url));
            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        if (name != null && meta != null) {
            meta.setDisplayName(name);
        }
        return this;
    }

    public ItemBuilder setItemStack(ItemStack stack) {
        if (stack != null) {
            this.item.setItemMeta(stack.getItemMeta());
            this.item.setAmount(stack.getAmount());
            this.item.setData(stack.getData());
        }
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (lore != null && meta != null) {
            meta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder setLore(String lore) {
        if (lore != null && meta != null) {
            ArrayList<String> loreList = new ArrayList<>();
            loreList.add(lore);
            meta.setLore(loreList);
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder setBasePotionData(PotionData pdata) {
        if (pdata == null) return this;
        if (meta instanceof PotionMeta) {
            ((PotionMeta) meta).setBasePotionData(pdata);
        }
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        if (color == null) return this;
        if (meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);
        }
        return this;
    }

    public ItemBuilder setPotionColor(Color color) {
        if (color == null) return this;
        if (meta instanceof PotionMeta) {
            ((PotionMeta) meta).setColor(color);
        }
        return this;
    }

    public ItemBuilder applyReplaceDisplay(List<muc.replaceData> replaceDataList) {
        if (replaceDataList == null) return this;
        if (meta != null && meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            for (muc.replaceData rd : replaceDataList)
                displayName = displayName.replace(rd.getOldChar(), rd.getNewChar());
            meta.setDisplayName(displayName);
        }
        return this;
    }

    public ItemBuilder applyReplaceLores(List<muc.replaceData> replaceDataList) {
        if (replaceDataList == null) return this;
        if (meta != null && meta.hasLore()) {
            List<String> lores = meta.getLore();
            if (lores != null) {
                for (int i = 0; i < lores.size(); i++) {
                    String line = lores.get(i);
                    for (muc.replaceData rd : replaceDataList)
                        line = line.replace(rd.getOldChar(), rd.getNewChar());
                    lores.set(i, line);
                }
                meta.setLore(lores);
            }
        }
        return this;
    }

    public ItemBuilder applyAllReplaces(List<muc.replaceData> replaceDataList) {
        if (replaceDataList == null) return this;
        applyReplaceLores(replaceDataList);
        applyReplaceDisplay(replaceDataList);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item.clone();
    }
}
