package org.ripeness.myutils.utils.textutil;

import org.bukkit.entity.Player;

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
        list.replaceAll(s -> s.replace(s1, s2));
    }

    public void send(Player player) {
        if (!getList().isEmpty()) {
            for (String s : getList()) {
                player.sendMessage(rcc(s));
            }
        }
    }
}
