package org.ripeness.myutils.utils;

import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.ripeness.myutils.utils.inventoryies.inventoryutil;
import org.ripeness.myutils.utils.items.ItemBuilder;

import javax.annotation.Nullable;
import java.util.*;

import static org.ripeness.myutils.utils.RPNSItems.papi.applyPAPI;
import static org.ripeness.myutils.utils.chat.tt.rcc;

public class RPNSItems {


    public static class config {

        public static ItemStack getItemInConfig(String loc, FileConfiguration cf, @Nullable OfflinePlayer pl) {
            Material type = null;
            String display = "";
            List<String> lores = null;
            int cmodeldata = -1;
            boolean isGlow = false;
            String headvalue = null;
            String headowner = null;
            int amount = 1;
            Map<String, String> nbties = new HashMap<>();
            List<String> consolecmds = new ArrayList<>();
            List<String> playercmds = new ArrayList<>();


            ConfigurationSection cs = cf.getConfigurationSection(loc);
            if (cs != null) {
                if (cs.isString("type")) {
                    type = Material.getMaterial(Objects.requireNonNull(cs.getString("type")));
                }
                if (cs.isString("display")) {
                    display = rcc(applyPAPI(pl, cs.getString("display", "")));
                }
                if (cs.isList("lores")) {
                    List<String> ex = new ArrayList<>();
                    for (String s : cs.getStringList("lores")) {
                        ex.add(rcc(applyPAPI(pl, s)));
                    }
                    lores = ex;
                }
                if (cs.isList("lore")) {
                    List<String> ex = new ArrayList<>();
                    for (String s : cs.getStringList("lore")) {
                        ex.add(rcc(applyPAPI(pl, s)));
                    }
                    lores = ex;
                }
                if (cs.isInt("modeldata")) {
                    cmodeldata = cs.getInt("modeldata");
                }
                if (cs.isBoolean("glow")) {
                    isGlow = cs.getBoolean("glow");
                }
                if (cs.isString("headValue")) {
                    headvalue = cs.getString("headValue");
                }

                if (cs.isString("headOwner")) {
                    headowner = applyPAPI(pl, cs.getString("headOwner"));
                }

                if (cs.isInt("amount")) {
                    amount = cs.getInt("amount");
                    if (amount > 64) amount = 64;
                    if (amount < 1) amount = 1;
                }


                if (type != null) {
                    ItemBuilder ib = new ItemBuilder(type);
                    if (!Objects.requireNonNull(display).isEmpty()) ib.setDisplayName(display);
                    ib.setLore(lores);
                    ib.setAmount(amount);
                    if (type.equals(Material.PLAYER_HEAD) && headvalue != null && !headvalue.isEmpty()) {
                        ib.setHeadTextureValue(headvalue);
                    }
                    if (type.equals(Material.PLAYER_HEAD) && headowner != null && !headowner.isEmpty()) {
                        ib.setHead(headowner);
                    }
                    ib.setGlow(isGlow);
                    if (cmodeldata != -1) ib.setCustomModelData(cmodeldata);
                    ItemStack res = ib.build();
                    ItemMeta itemMeta = res.getItemMeta();
                    if (itemMeta != null) {
                        if (itemMeta instanceof PotionMeta)
                            if (cs.isBoolean("otoEffectPotion")) {
                                if (cs.getBoolean("otoEffectPotion", true)) {
                                    ((PotionMeta) itemMeta).setBasePotionData(new PotionData(PotionType.POISON));
                                }
                            }


                        if (cs.isSet("leather_color")) {
                            if (itemMeta instanceof LeatherArmorMeta) {
                                String[] s = cs.getString("leather_color").split(",");
                                ((LeatherArmorMeta) itemMeta).setColor(Color.fromRGB(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));
                            }
                        }
                    }

                    if (itemMeta != null) {
                        if (cs.isSet("potion_color")) {
                            if (itemMeta instanceof PotionMeta) {
                                String[] s = cs.getString("potion_color").split(",");
                                ((PotionMeta) itemMeta).setColor(Color.fromRGB(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));
                            }
                        }
                    }

                    if (itemMeta != null)
                        if (cs.isBoolean("hideAll")) {
                            if (cs.getBoolean("hideAll", true)) {
                                for (ItemFlag value : ItemFlag.values()) {
                                    itemMeta.addItemFlags(value);
                                }
                            }
                        }


                    res.setItemMeta(itemMeta);

//                if (cs.isConfigurationSection("nbties")) {
//                    for (String key : cs.getConfigurationSection("nbties").getKeys(false)) {
//                        String compName = null;
//                        String keyName = cs.getString("nbties." + key);
//                        String keyType = "STRING";
//                        String[] split = key.split(":");
//                        if ((split.length != 0)) {
//                            compName = split[0];
//                        }
//                        String[] keysValue = Objects.requireNonNull(keyName).split(":");
//                        keyType = keysValue[0].toUpperCase(Locale.ENGLISH);
//
//                        NBTMaker nbtMaker = new NBTMaker(res, true).setCompound(compName);
//                        if (keyType.equals("INTEGER")) {
//                            nbtMaker.setInteger(split[1], Integer.parseInt(keysValue[1]));
//                        } else if (keyType.equals("BOOLEAN")) {
//                            nbtMaker.setBoolean(split[1], Boolean.parseBoolean(keysValue[1]));
//                        } else {
//                            //nbtMaker.setString(split[1], (keysValue[1]));
//                        }
//
//
//                    }
//                }

                    consolecmds = cs.getStringList("consolecmds");
                    playercmds = cs.getStringList("playercmds");

                    NBTItem nbtItem = new NBTItem(res);
                    nbtItem.setString("myutils_cmdexecutor_console", String.join(";;;;", consolecmds));
                    nbtItem.setString("myutils_cmdexecutor_player", String.join(";;;;", playercmds));
                    nbtItem.mergeNBT(res);

                    return res;
                }
            }
            return null;
        }

        public static ItemStack getItemInConfigNullable(String loc, FileConfiguration cf, @Nullable String pln) {
            Material type = null;
            String display = null;
            List<String> lores = null;
            int cmodeldata = -1;
            boolean isGlow = false;

            if (cf.isConfigurationSection(loc)) {
                ConfigurationSection cs = cf.getConfigurationSection(loc);
                if (cs.isString("itemType")) {
                    type = Material.getMaterial(Objects.requireNonNull(cs.getString("itemType")));
                }
                if (cs.isString("displayName")) {
                    display = rcc(Objects.requireNonNull(cs.getString("displayName"))).replace("%player%", pln == null ? "" : pln);
                }
                if (cs.isList("lores")) {
                    List<String> ex = new ArrayList<>();
                    for (String s : cs.getStringList("lores")) {
                        ex.add(rcc(s).replace("%player%", pln == null ? "" : pln));
                    }
                    lores = ex;
                }
                if (cs.isInt("customModelData")) {
                    cmodeldata = cf.getInt("customModelData");
                }
                if (cs.isBoolean("isGlow")) {
                    isGlow = cs.getBoolean("isGlow");
                }

                if (type == null) type = Material.PAPER;
                ItemBuilder ib = new ItemBuilder(type);
                ib.setDisplayName(display);
                ib.setLore(lores);
                ib.setGlow(isGlow);
                if (cmodeldata != -1) ib.setCustomModelData(cmodeldata);
                return ib.build();
            }
            return null;
        }

    }


    public static class papi {

        public static String applyPAPI(OfflinePlayer pl, String str) {
            if (pl == null) return str;
            return PlaceholderAPI.setPlaceholders(pl, PlaceholderAPI.setBracketPlaceholders(pl, str));
        }

        public static String applyPAPI(Player pl, String str) {
            if (pl == null) return str;
            return PlaceholderAPI.setPlaceholders(pl, PlaceholderAPI.setBracketPlaceholders(pl, str));
        }

        public static NBTItem getNBT(ItemStack item) {
            if (item == null || item.getType().isAir()) return null;
            return new NBTItem(item);
        }

        public static void setBooleanNBT(ItemStack item, String bo) {
            NBTItem n = getNBT(item);
            if (item != null) {
                n.setBoolean(bo, true);
                n.mergeNBT(item);
            }
        }

        public static void setStringNBT(ItemStack item, String key, String value) {
            NBTItem n = getNBT(item);
            if (item != null) {
                n.setString(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setIntegerNBT(ItemStack item, String key, Integer integer) {
            NBTItem n = getNBT(item);
            if (item != null) {
                n.setInteger(key, integer);
                n.mergeNBT(item);
            }
        }

        public static boolean hasNBTTag(ItemStack item, String nbttag) {
            if (item != null) {
                return RPNSItems.papi.getNBT(item).hasTag(nbttag);
            }
            return false;
        }

        public static boolean hasNBTTagEqualsString(ItemStack item, String nbttag, String checkingStr) {
            if (item != null) {
                return RPNSItems.papi.getNBT(item).getString(nbttag).equals(checkingStr);
            }
            return false;
        }

        public static boolean hasNBTCompound(ItemStack item, String compstr) {
            if (item != null) {
                return RPNSItems.papi.getNBT(item).getCompound(compstr) != null;
            }
            return false;
        }

        public static <T> T getItemNbtResultInInventory(Inventory in, String nbtKey, Class<T> resultClazz) {
            if (in != null) {
                int i = inventoryutil.getIndexOfItemFromNbtInMenu(nbtKey, in);
                if (i == -1) return null;
                ItemStack item = in.getItem(i);
                if (item != null) {
                    NBTItem nbtItem = getNBT(item);
                    if (nbtItem != null) {
                        if (resultClazz == String.class) {
                            return resultClazz.cast(nbtItem.getString(nbtKey));
                        } else if (resultClazz == Integer.class) {
                            return resultClazz.cast(nbtItem.getInteger(nbtKey));
                        } else if (resultClazz == Boolean.class) {
                            return resultClazz.cast(nbtItem.getBoolean(nbtKey));
                        } else if (resultClazz == Double.class) {
                            return resultClazz.cast(nbtItem.getDouble(nbtKey));
                        } // stringlist
                        else if (resultClazz == List.class) {
                            if (nbtItem.hasTag(nbtKey)) {
                                return resultClazz.cast(nbtItem.getStringList(nbtKey));
                            } else {
                                return resultClazz.cast(new ArrayList<String>());
                            }
                        } else {
                            return null;
                        }

                    }
                }
            }
            return null;
        }


    }


}
