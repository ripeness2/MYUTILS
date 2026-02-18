package org.ripeness.myutils.utils;

import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.ripeness.myutils.muc;
import org.ripeness.myutils.utils.inventoryies.inventoryutil;
import org.ripeness.myutils.utils.items.ItemBuilder;
import org.ripeness.myutils.utils.nbt.NBTMaker;

import javax.annotation.Nullable;
import java.util.*;

import static org.ripeness.myutils.utils.RPNSItems.papi.applyPAPI;
import static org.ripeness.myutils.utils.chat.tt.rcc;

public class RPNSItems {


    public static class config {

        public static ItemStack getItemInConfig(String loc, FileConfiguration cf, @Nullable OfflinePlayer pl, Plugin plugin) {
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
                                ((LeatherArmorMeta) itemMeta).setColor(Color.fromRGB(Integer.parseInt(s[0].trim()), Integer.parseInt(s[1].trim()), Integer.parseInt(s[2].trim())));
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
                    if (plugin != null) {

                        consolecmds = cs.getStringList("consolecmds");
                        playercmds = cs.getStringList("playercmds");

                        NBTMaker nbtItem = new NBTMaker(res, true);
                        nbtItem.setString(plugin.getName() + "+myutils_cmdexecutor_console", String.join(";;;;", consolecmds));
                        nbtItem.setString(plugin.getName() + "+myutils_cmdexecutor_player", String.join(";;;;", playercmds));

                        ConfigurationSection nbtSection = cs.getConfigurationSection("nbties");
                        if (nbtSection != null) {

                            for (String rawKey : nbtSection.getKeys(false)) {

                                String rawValue = nbtSection.getString(rawKey);
                                if (rawValue == null || rawValue.isEmpty()) continue;

                                // ---- COMPOUND AYRIŞTIRMA ( ;; ) ----
                                String compoundName = null;
                                String keyName = rawKey;

                                if (rawKey.contains(";;")) {
                                    String[] compoundSplit = rawKey.split(";;", 2);
                                    if (compoundSplit.length != 2) continue;

                                    compoundName = compoundSplit[0].trim();
                                    keyName = compoundSplit[1].trim();

                                    if (compoundName.isEmpty() || keyName.isEmpty()) continue;
                                }

                                // ---- TYPE & VALUE AYRIŞTIRMA ----
                                String[] valueSplit = rawValue.split(";", 2);
                                if (valueSplit.length != 2) continue;

                                String typee1 = valueSplit[0].toUpperCase(Locale.ENGLISH).trim();
                                String value = valueSplit[1].trim();
                                value = RPNSItems.papi.applyPAPI(pl, value);

                                NBTMaker nbtMaker = new NBTMaker(res, true);
                                if (compoundName != null) {
                                    nbtMaker.setCompound(compoundName);
                                }

                                try {

                                    if (typee1.equals("INTEGER")) {
                                        nbtMaker.setInteger(keyName, Integer.parseInt(value));

                                    } else if (typee1.equals("BOOLEAN")) {
                                        nbtMaker.setBoolean(keyName, Boolean.parseBoolean(value));
//
//                            } else if (type.equals("DOUBLE")) {
//                                nbtMaker.setDouble(keyName, Double.parseDouble(value));
//
//                            } else if (type.equals("LONG")) {
//                                nbtMaker.setLong(keyName, Long.parseLong(value));

                                    } else if (typee1.equals("STRING")) {
                                        nbtMaker.setString(keyName, value);

                                    } else {
                                        // Bilinmeyen type → güvenli fallback
                                        nbtMaker.setString(keyName, value);
                                    }

                                } catch (Exception ex) {
                                    Bukkit.getLogger().warning(
                                            "[NBT] Hatalı NBT girişi: key=" + rawKey + " value=" + rawValue
                                    );
                                }
                            }
                        }
                    }

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

        public static void replaceMetaItem(ItemStack item, List<muc.replaceData> replaceInfos) {
            if (item == null) return;
            if (replaceInfos == null) return;
            if (replaceInfos.isEmpty()) return;
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) return;
            if (itemMeta.hasDisplayName()) {
                String displayName = itemMeta.getDisplayName();
                for (muc.replaceData ri : replaceInfos) {
                    displayName = displayName.replace(ri.getOldChar(), ri.getNewChar());
                }
                itemMeta.setDisplayName(displayName);
            }
            if (itemMeta.hasLore()) {
                List<String> lore = itemMeta.getLore();
                if (lore != null) {
                    List<String> newLore = new ArrayList<>();
                    for (String s : lore) {
                        for (muc.replaceData ri : replaceInfos) {
                            s = s.replace(ri.getOldChar(), ri.getNewChar());
                        }
                        newLore.add(s);
                    }
                    itemMeta.setLore(newLore);
                }
            }
            item.setItemMeta(itemMeta);
        }

        public static ItemStack applyPAPItem(OfflinePlayer player, ItemStack item) {
            // Eşya veya Meta verisi boşsa direkt geri dön (NullPointerException koruması)
            if (item == null || !item.hasItemMeta()) {
                return item;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;

            // 1. Display Name (Görünen İsim) İşlemi
            if (meta.hasDisplayName()) {
                String updatedName = RPNSItems.papi.applyPAPI(player, meta.getDisplayName());
                meta.setDisplayName(rcc(updatedName));
            }

            // 2. Lore (Açıklama) İşlemi
            if (meta.hasLore()) {
                List<String> updatedLore = new ArrayList<>();
                //noinspection DataFlowIssue
                for (String line : meta.getLore()) {
                    // Her bir satıra tek tek placeholder'ları uygula
                    updatedLore.add(rcc(PlaceholderAPI.setPlaceholders(player, line)));
                }
                meta.setLore(updatedLore);
            }

            // Meta'yı eşyaya geri yükle
            item.setItemMeta(meta);

            return item;
        }


//        public static boolean isMatchingItem(ItemStack mainItem, ItemStack otherItem) {
//            // 1. Null ve Temel Kontroller
//            if (mainItem == null || otherItem == null) return false;
//
//            // 2. Type (Material) Kontrolü
//            // Config'deki String'i Material'e çeviriyoruz (Büyük/küçük harf duyarsız olması için toUpperCase)
//            Material configMaterial = otherItem.getType();
//
//            // Eğer configdeki material geçersizse veya eşleşmiyorsa false
//            if (mainItem.getType() != configMaterial) {
//                return false;
//            }
//
//            // Meta verisini alıyoruz (Display ve ModelData için gerekli)
//            ItemMeta meta = mainItem.getItemMeta();
//            ItemMeta otherMeta = otherItem.getItemMeta();
//            if (otherMeta == null && meta == null) return true;
//            if (otherMeta == null) return false;
//            if (meta == null) return false;
//            String displayName = otherMeta.getDisplayName();
//
//            // 3. Display Name Kontrolü
//            // KURAL: Display null değilse VE boş string ("") değilse kontrol et.
//            if (!displayName.isEmpty()) {
//                // Configde isim istenmiş ama eşyanın metası veya ismi yoksa -> false
//                if (!meta.hasDisplayName()) {
//                    return false;
//                }
//
//                boolean nameMatches = isNameMatches(displayName, meta);
//
//                if (!nameMatches) return false;
//            }
//
//            // 4. Custom Model Data Kontrolü
//            // KURAL: Modeldata -1 değilse kontrol et.
//            if (config.getModeldata() != -1) {
//                // Configde model istenmiş ama eşyanın model datası yoksa -> false
//                if (meta == null || !meta.hasCustomModelData()) {
//                    return false;
//                }
//
//                // Sayısal değer eşit değilse -> false
//                if (meta.getCustomModelData() != config.getModeldata()) {
//                    return false;
//                }
//            }
//
//            // Tüm engelleri aştıysa eşleşmiştir
//            return true;
//        }


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
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setBoolean(bo, true);
                n.mergeNBT(item);
            }
        }

        public static void setStringNBT(ItemStack item, String key, String value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setString(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setIntegerNBT(ItemStack item, String key, Integer integer) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setInteger(key, integer);
                n.mergeNBT(item);
            }
        }

        public static void setDoubleNBT(ItemStack item, String key, Double value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setDouble(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setByteNBT(ItemStack item, String key, Byte value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setByte(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setByteArrayNBT(ItemStack item, String key, byte[] value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setByteArray(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setObjectNBT(ItemStack item, String key, Object value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setObject(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setFloatNBT(ItemStack item, String key, Float value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setFloat(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setItemStackNBT(ItemStack item, String key, ItemStack value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setItemStack(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setLongNBT(ItemStack item, String key, Long value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setLong(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setShortNBT(ItemStack item, String key, Short value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setShort(key, value);
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
