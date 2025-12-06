package org.ripeness.myutils.utils.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ripeness.myutils.utils.chat.tt.rcc;

public class ItemStringer {

    public static class Options {
        private boolean includeAmount;
        private boolean includeModelData;
        private boolean includeFlags;
        private boolean includeDisplay;
        private boolean includeLore;
        private boolean includeBannerMeta;
        private boolean includePotionData;

        public Options(boolean includeAmount, boolean includeModelData, boolean includeFlags, boolean includeDisplay, boolean includeLore, boolean includeBannerMeta, boolean includePotionData) {
            this.includeAmount = includeAmount;
            this.includeModelData = includeModelData;
            this.includeFlags = includeFlags;
            this.includeDisplay = includeDisplay;
            this.includeLore = includeLore;
            this.includeBannerMeta = includeBannerMeta;
            this.includePotionData = includePotionData;
        }

        public boolean isIncludeAmount() {
            return includeAmount;
        }

        public void setIncludeAmount(boolean includeAmount) {
            this.includeAmount = includeAmount;
        }

        public boolean isIncludeModelData() {
            return includeModelData;
        }

        public void setIncludeModelData(boolean includeModelData) {
            this.includeModelData = includeModelData;
        }

        public boolean isIncludeFlags() {
            return includeFlags;
        }

        public void setIncludeFlags(boolean includeFlags) {
            this.includeFlags = includeFlags;
        }

        public boolean isIncludeDisplay() {
            return includeDisplay;
        }

        public void setIncludeDisplay(boolean includeDisplay) {
            this.includeDisplay = includeDisplay;
        }

        public boolean isIncludeLore() {
            return includeLore;
        }

        public void setIncludeLore(boolean includeLore) {
            this.includeLore = includeLore;
        }

        public boolean isIncludeBannerMeta() {
            return includeBannerMeta;
        }

        public void setIncludeBannerMeta(boolean includeBannerMeta) {
            this.includeBannerMeta = includeBannerMeta;
        }

        public boolean isIncludePotionData() {
            return includePotionData;
        }

        public void setIncludePotionData(boolean includePotionData) {
            this.includePotionData = includePotionData;
        }

        public String toString() {
            return "Options{" +
                    "includeAmount=" + includeAmount +
                    ", includeModelData=" + includeModelData +
                    ", includeFlags=" + includeFlags +
                    ", includeDisplay=" + includeDisplay +
                    ", includeLore=" + includeLore +
                    ", includeBannerMeta=" + includeBannerMeta +
                    ", includePotionData=" + includePotionData +
                    '}';
        }

        public String toString2() {
            return "includeAmount=" + includeAmount +
                    ", includeModelData=" + includeModelData +
                    ", includeFlags=" + includeFlags +
                    ", includeDisplay=" + includeDisplay +
                    ", includeLore=" + includeLore +
                    ", includeBannerMeta=" + includeBannerMeta +
                    ", includePotionData=" + includePotionData;
        }
    }

    public static class resultClass {
        ItemStack result;
        Options options;

        public resultClass(ItemStack result, Options options) {
            this.result = result;
            this.options = options;
        }

        public ItemStack getResult() {
            return result;
        }

        public Options getOptions() {
            return options;
        }

        //set
        public void setResult(ItemStack result) {
            this.result = result;
        }

        public void setOptions(Options options) {
            this.options = options;
        }
    }

    public static String itemToString(ItemStack item, Options options) {
        if (item == null) return "{type:::AIR}";
        if (item.getType().isAir()) return "{type:::AIR}";
        StringBuilder b = new StringBuilder();
        b.append("{type:::").append(item.getType().name()).append("}");

        int amount = item.getAmount();
        b.append("{amount:::").append(amount).append("}");
        ItemMeta meta = item.getItemMeta();

        if (options.isIncludeModelData() && meta != null) {
            int modeldata = meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
            b.append("{custom_model_data:::").append(modeldata).append("}");
        }

        // display name
        if (meta != null && options.isIncludeDisplay() && meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            if (!displayName.isEmpty()) {
                b.append("{display_name:::").append(displayName).append("}");
            }
        }

        //lores
        if (meta != null && options.isIncludeLore() && meta.hasLore()) {
            List<String> lores = meta.getLore();
            if (lores != null && !lores.isEmpty()) {
                b.append("{lore:::");
                for (int i = 0; i < lores.size(); i++) {
                    String lore = lores.get(i);
                    b.append(lore);
                    if (i != lores.size() - 1) {
                        b.append(",;,");
                    }
                }
                b.append("}");
            }
        }

        if (meta instanceof BannerMeta && options.isIncludeBannerMeta()) {
            BannerMeta m = (BannerMeta) meta;
            List<org.bukkit.block.banner.Pattern> patterns = m.getPatterns();
            if (!patterns.isEmpty()) b.append("{patterns:::");
            for (int i = 0; i < patterns.size(); i++) {
                org.bukkit.block.banner.Pattern p = patterns.get(i);
                try {
                    b.append(p.getPattern().name()).append("-").append(p.getColor().name());
                } catch (Exception e) {
                    b.append("ERROR");
                }
                if (i != patterns.size() - 1) {
                    b.append(",;,");
                }
            }
            /*for (Pattern p : patterns) {
                try {
                    b.append(p.getPattern().name()).append("-").append(p.getColor().name()).append(",;,");
                } catch (Exception e) {
                    b.append("ERROR,;,");
                }
            }*/
            if (!patterns.isEmpty()) b.append("}");
        }
        if (meta instanceof PotionMeta && options.isIncludePotionData()) {
            PotionMeta pm = (PotionMeta) meta;
            PotionData basePotionData = pm.getBasePotionData();
            b.append("{potion_base:::").append(basePotionData.getType().name()).append("}");
            List<PotionEffect> customEffects = pm.getCustomEffects();
            if (!customEffects.isEmpty()) {
                b.append("{potion_effects:::");
                for (int i = 0; i < customEffects.size(); i++) {
                    PotionEffect pe = customEffects.get(i);
                    try {
                        b.append(pe.getType().getName()).append("-").append(pe.getAmplifier()).append("-").append(pe.getDuration());
                    } catch (Exception e) {
                        b.append("ERROR");
                    }
                    if (i != customEffects.size() - 1) {
                        b.append(",;,");
                    }
                }
                b.append("}");
            }
            Color color = pm.getColor();
            if (color != null) {
                b.append("{potion_color:::").append(color.asRGB()).append("}");
            }
        }

        String optionsString = options.toString2();
        b.append("{options:::").append(optionsString).append("}");

        return b.toString();
    }

    public static String itemToString(ItemStack item) {
        return itemToString(item,
                new Options(
                        true,
                        true,
                        true,
                        true,
                        true,
                        true,
                        true
                )
        );
    }

    public static String itemToStringNotDisplays(ItemStack item) {
        return itemToString(item,
                new Options(
                        false,
                        true,
                        true,
                        false,
                        false,
                        true,
                        true
                )
        );
    }

    public static resultClass stringToItem(String s) {
        // String control
        if (s == null || s.isEmpty()) return null;
        // use regex to get the values between { and }
        //type
//        Pattern TYPEpattern1 = Pattern.compile("\\{type(.*?)}");
//        Pattern pattern = Pattern.compile("\\{(.*?)}");
        Pattern TYPECompile = Pattern.compile("\\{type:::(.*?)}");
        Matcher TYPEmatcher = TYPECompile.matcher(s);
        Material type = Material.AIR;
        if (TYPEmatcher.find()) {
            String typeStr = TYPEmatcher.group(1);
            try {
                type = Material.valueOf(typeStr);
            } catch (Exception e) {
                type = Material.AIR;
            }
            ItemStack item = new ItemStack(type);
        }
        ItemBuilder itemBuilder = new ItemBuilder(type);
        //amount
        Pattern AMOUNTCompile = Pattern.compile("\\{amount:::(.*?)}");
        Matcher AMOUNTmatcher = AMOUNTCompile.matcher(s);
        if (AMOUNTmatcher.find()) {
            String amountStr = AMOUNTmatcher.group(1);
            try {
                int amount = Integer.parseInt(amountStr);
                itemBuilder.setAmount(amount);
            } catch (Exception e) {
                //ignore
            }
        }
        //custom_model_data
        Pattern CMDCompile = Pattern.compile("\\{custom_model_data:::(.*?)}");
        Matcher CMDmatcher = CMDCompile.matcher(s);
        if (CMDmatcher.find()) {
            String cmdStr = CMDmatcher.group(1);
            try {
                int cmd = Integer.parseInt(cmdStr);
                itemBuilder.setCustomModelData(cmd);
            } catch (Exception e) {
                //ignore
            }
        }
        //display_name
        Pattern DNCompile = Pattern.compile("\\{display_name:::(.*?)}");
        Matcher DNmatcher = DNCompile.matcher(s);
        if (DNmatcher.find()) {
            String displayName = DNmatcher.group(1);
            itemBuilder.setDisplayName(rcc(displayName));
        }

        //lores
        if (s.contains("{lore:::")) {
            Pattern LORECompile = Pattern.compile("\\{lore:::(.*?)}");
            Matcher LOREmatcher = LORECompile.matcher(s);
            if (LOREmatcher.find()) {
                String loresStr = LOREmatcher.group(1);
                String[] loresArray = loresStr.split(",;,");
                List<String> objects = new ArrayList<>();
                for (String lore : loresArray) objects.add(rcc(lore));
                itemBuilder.setLore(objects);
            }
        }


        ItemStack devam = itemBuilder.build();

        //banner patterns
        if (s.contains("{patterns:::")) {
            Pattern PATTERNCompile = Pattern.compile("\\{patterns:::(.*?)}");
            Matcher PATTERNmatcher = PATTERNCompile.matcher(s);
            if (PATTERNmatcher.find()) {
                String patternsStr = PATTERNmatcher.group(1);
                String[] patternsArray = patternsStr.split(",;,");
                List<org.bukkit.block.banner.Pattern> patterns = new ArrayList<>();
                for (String patternStr : patternsArray) {
                    String[] parts = patternStr.split("-");
                    if (parts.length == 2) {
                        try {
                            org.bukkit.block.banner.PatternType patternType = org.bukkit.block.banner.PatternType.valueOf(parts[0]);
                            org.bukkit.DyeColor color = org.bukkit.DyeColor.valueOf(parts[1]);
                            org.bukkit.block.banner.Pattern pattern = new org.bukkit.block.banner.Pattern(color, patternType);
                            patterns.add(pattern);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                }
                try {
                    BannerMeta bannerMeta = (BannerMeta) devam.getItemMeta();
                    assert bannerMeta != null;
                    bannerMeta.setPatterns(patterns);
                    devam.setItemMeta(bannerMeta);
                } catch (Exception e) {
                    //ignore
                }
            }
        }

        // potion data
        if (s.contains("{potion_base:::")) {
            Pattern POTIONBASECompile = Pattern.compile("\\{potion_base:::(.*?)}");
            Matcher POTIONBASEmatcher = POTIONBASECompile.matcher(s);
            if (POTIONBASEmatcher.find()) {
                String potionBaseStr = POTIONBASEmatcher.group(1);
                try {
                    PotionData potionData = new PotionData(org.bukkit.potion.PotionType.valueOf(potionBaseStr));
                    PotionMeta potionMeta = (PotionMeta) devam.getItemMeta();
                    assert potionMeta != null;
                    potionMeta.setBasePotionData(potionData);
                    devam.setItemMeta(potionMeta);
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        if (s.contains("{potion_effects:::")) {
            Pattern POTIONEFFECTSCompile = Pattern.compile("\\{potion_effects:::(.*?)}");
            Matcher POTIONEFFECTSmatcher = POTIONEFFECTSCompile.matcher(s);
            if (POTIONEFFECTSmatcher.find()) {
                String potionEffectsStr = POTIONEFFECTSmatcher.group(1);
                String[] potionEffectsArray = potionEffectsStr.split(",;,");
                List<PotionEffect> potionEffects = new ArrayList<>();
                for (String effectStr : potionEffectsArray) {
                    String[] parts = effectStr.split("-");
                    if (parts.length == 3) {
                        try {
                            org.bukkit.potion.PotionEffectType effectType = org.bukkit.potion.PotionEffectType.getByName(parts[0]);
                            int amplifier = Integer.parseInt(parts[1]);
                            int duration = Integer.parseInt(parts[2]);
                            PotionEffect potionEffect = new PotionEffect(effectType, duration, amplifier);
                            potionEffects.add(potionEffect);
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                }
                try {
                    PotionMeta potionMeta = (PotionMeta) devam.getItemMeta();
                    assert potionMeta != null;
                    for (PotionEffect pe : potionEffects) {
                        potionMeta.addCustomEffect(pe, true);
                    }
                    devam.setItemMeta(potionMeta);
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        // potion color
        if (s.contains("{potion_color:::")) {
            Pattern POTIONCOLORCompile = Pattern.compile("\\{potion_color:::(.*?)}");
            Matcher POTIONCOLORmatcher = POTIONCOLORCompile.matcher(s);
            if (POTIONCOLORmatcher.find()) {
                String potionColorStr = POTIONCOLORmatcher.group(1);
                try {
                    int rgb = Integer.parseInt(potionColorStr);
                    Color color = Color.fromRGB(rgb);
                    PotionMeta potionMeta = (PotionMeta) devam.getItemMeta();
                    assert potionMeta != null;
                    potionMeta.setColor(color);
                    devam.setItemMeta(potionMeta);
                } catch (Exception e) {
                    //ignore
                }
            }
        }
        // final return

        Options def = new Options(false
                , false
                , false
                , false
                , false
                , false
                , false);

        Pattern OPTIONSCompile = Pattern.compile("\\{options:::(.*?)}");
        Matcher OPTIONSmatcher = OPTIONSCompile.matcher(s);
        if (OPTIONSmatcher.find()) {
            String optionsStr = OPTIONSmatcher.group(1);
            String[] optionsArray = optionsStr.split(", ");
            for (String option : optionsArray) {
                String[] keyValue = option.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    boolean boolValue = Boolean.parseBoolean(value);
                    switch (key) {
                        case "includeAmount":
                            def.setIncludeAmount(boolValue);
                            break;
                        case "includeModelData":
                            def.setIncludeModelData(boolValue);
                            break;
                        case "includeFlags":
                            def.setIncludeFlags(boolValue);
                            break;
                        case "includeDisplay":
                            def.setIncludeDisplay(boolValue);
                            break;
                        case "includeLore":
                            def.setIncludeLore(boolValue);
                            break;
                        case "includeBannerMeta":
                            def.setIncludeBannerMeta(boolValue);
                            break;
                        case "includePotionData":
                            def.setIncludePotionData(boolValue);
                            break;
                    }
                }
            }
        }

        return new resultClass(devam, def);
    }

}
