package org.ripeness.myutils.utils.textutil;

import org.bukkit.entity.Player;

import java.util.List;

import static org.ripeness.myutils.utils.chat.tt.rcc;

public class listed {

    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public listed(List<String> list) {
        this.list = list;
    }

    public void send(Player player) {
        if (!getList().isEmpty()) {
            for (String s : getList()) {
                player.sendMessage(rcc(s));
            }
        }
    }
}
