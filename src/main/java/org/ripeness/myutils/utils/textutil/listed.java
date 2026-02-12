package org.ripeness.myutils.utils.textutil;

import org.bukkit.entity.Player;
import org.ripeness.myutils.muc;
import org.ripeness.myutils.utils.RPNSItems;

import java.util.ArrayList;
import java.util.List;

import static org.ripeness.myutils.utils.chat.tt.rcc;

public class listed {

    private List<String> list = new ArrayList<>();

    public List<String> getList() {
        return list;
    }

    public listed(List<String> list) {
        this.list = list;
    }

    public void replaceAllList(String s1, String s2) {
        List<String> l = new ArrayList<>();
        for (String s : new ArrayList<>(list)) {
            s = s.replace(s1, s2);
            l.add(s);
        }
        list = l;
    }

    public void replaceAllList(List<muc.replaceData> replaceDataList) {
        List<String> l = new ArrayList<>();
        for (String s : new ArrayList<>(list)) {
            for (muc.replaceData replaceData : replaceDataList) {
                String newChar = replaceData.getNewChar();
                String oldChar = replaceData.getOldChar();
                s = s.replace(oldChar, newChar);
            }
            l.add(s);
        }
        list = l;
    }

    public void send(Player player) {
        if (!getList().isEmpty()) {
            for (String s : getList()) {
                player.sendMessage(rcc(s));
            }
        }
    }

    public void sendPlaceholder(Player player) {
        if (!getList().isEmpty()) {
            for (String s : getList()) {
                player.sendMessage(rcc(RPNSItems.papi.applyPAPI(player, s)));
            }
        }
    }
}
