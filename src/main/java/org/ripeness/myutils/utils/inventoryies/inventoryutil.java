package org.ripeness.myutils.utils.inventoryies;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static org.ripeness.myutils.utils.RPNSItems.papi.getNBT;


public class inventoryutil {


    public static void fillWithItem(Inventory inventory, ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, item);
        }
    }

    public static void addItemAllBorders(Inventory inventory, ItemStack redGlass) {

        int size = inventory.getSize();

        // Sağ kenar
        for (int i = 0; i < size; i += 9) {
            inventory.setItem(i, redGlass);
        }

        // Sol kenar
        for (int i = 8; i < size; i += 9) {
            inventory.setItem(i, redGlass);
        }

        // Üst kenar
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, redGlass);
        }

        // Alt kenar
        for (int i = size - 9; i < size; i++) {
            inventory.setItem(i, redGlass);
        }
    }

    public static void addItemBorders(Inventory inventory, ItemStack redGlass) {
        // Sağ kenar
        int size = inventory.getSize();
        for (int i = 0; i < size; i += 9) {
            inventory.setItem(i, redGlass);
        }

        // Sol kenar
        for (int i = 8; i < size; i += 9) {
            inventory.setItem(i, redGlass);
        }
    }

    public static void addItemBordersUP(Inventory inventory, ItemStack redGlass) {
        // Üst kenar
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, redGlass);
        }
    }

    public static void addItemBordersDown(Inventory inventory, ItemStack redGlass) {
        // Alt kenar
        int size = inventory.getSize();
        for (int i = size - 9; i < size; i++) {
            inventory.setItem(i, redGlass);
        }
    }

    public static void fillWithItemIsAir(Inventory in, ItemStack item) {
        for (int i = 0; i < in.getSize(); i++) {
            ItemStack currentItem = in.getItem(i);
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                in.setItem(i, item);
            }
        }
    }

    public static void setItems(Inventory in, int[] ints, ItemStack item) {
        for (int anInt : ints) {
            if ((in.getSize() - 1) <= anInt) {
                if (!(anInt < 0)) {
                    if (item != null) {
                        in.setItem(anInt, item);
                    }
                }
            }
        }

    }

    public static void setItems(Inventory in, List<Integer> ints, ItemStack item) {
        for (int anInt : ints) {
            if (anInt <= (in.getSize() - 1)) {
                if (!(anInt < 0)) {
                    if (item != null) {
                        in.setItem(anInt, item);
                    }
                }
            }
        }

    }

    public static void setItems(Inventory in, List<Integer> ints, ItemStack item, boolean isRandom) {
        for (int anInt : ints) {
            if (anInt <= (in.getSize() - 1)) {
                if (!(anInt < 0)) {
                    if (item != null) {
                        if (isRandom) {
//                                List<String> randomWords = RandomWordsGenerator.generateRandomWords(2);
//                                Random r = new Random();
//                                int rrr = r.nextInt(999999999);
//                                String rn = "random_" + rrr;
                            String ranwords = "random_" + anInt;
//                                for (String word : randomWords) {
//                                    ranwords = ranwords + word;
//                                }
                            NBTItem n = new NBTItem(item, true);
                            n.setString(ranwords, ranwords);

                        }

                        in.setItem(anInt, item);
                    }
                }
            }
        }

    }

    public static boolean isEmpty(Player pl) {

        for (int i = 0; i < pl.getInventory().getStorageContents().length; i++) {
            if (pl.getInventory().getItem(i) == null || pl.getInventory().getItem(i).getType() == Material.AIR) {
                return true;
            }
        }

        return false;
    }

    public static boolean isEmpty(Player pl, int reqMinAIR) {
        int totall = 0;
        for (int i = 0; i < pl.getInventory().getStorageContents().length; i++) {
            if (pl.getInventory().getItem(i) == null || pl.getInventory().getItem(i).getType() == Material.AIR) {
                totall++;
            }
        }
        return totall >= reqMinAIR;
    }

    public static boolean isEmpty(Inventory pl) {

        for (int i = 0; i < pl.getStorageContents().length; i++) {
            if (pl.getItem(i) == null || pl.getItem(i).getType() == Material.AIR) {
                return true;
            }
        }

        return false;
    }

    public static boolean isEmpty(Inventory pl, int reqMinAIR) {
        int totall = 0;
        for (int i = 0; i < pl.getStorageContents().length; i++) {
            if (pl.getItem(i) == null || pl.getItem(i).getType() == Material.AIR) {
                totall++;
            }
        }
        return totall >= reqMinAIR;
    }

    public static int getIndexOfItemFromNbtInMenu(String nbt, Inventory i) {

        if (i != null) {
            for (int i1 = 0; i1 < i.getStorageContents().length; i1++) {
                ItemStack storageContent = i.getStorageContents()[i1];
                if (storageContent != null)
                    if (!storageContent.getType().isAir()) {
                        NBTItem n = getNBT(storageContent);
                        boolean ishas = n.hasTag(nbt);
                        if (ishas) {
                            return i1;
                        }
                    }
            }
        }

        return -1;
    }

    public static int getIndexOfItemFromCompNbtInMenu(String compname, String nbt, Inventory i) {

        if (i != null) {
            for (int i1 = 0; i1 < i.getStorageContents().length; i1++) {
                ItemStack storageContent = i.getStorageContents()[i1];
                if (!storageContent.getType().isAir()) {
                    NBTItem n = getNBT(storageContent);
                    NBTCompound c = n.getCompound(compname);
                    if (c == null) return -1;
                    return c.hasTag(nbt) ? i1 : -1;
                }
            }
        }

        return -1;
    }

}
