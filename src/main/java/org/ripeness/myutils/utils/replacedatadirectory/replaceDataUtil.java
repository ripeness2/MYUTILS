package org.ripeness.myutils.utils.replacedatadirectory;

import org.ripeness.myutils.muc;

import java.util.ArrayList;
import java.util.List;

public class replaceDataUtil {
    private List<muc.replaceData> replaceDataList;

    public replaceDataUtil() {
        replaceDataList = new ArrayList<>();
    }

    public replaceDataUtil(List<muc.replaceData> replaceDataList) {
        this.replaceDataList = replaceDataList;
    }


    public static void a(String oldChar, String newChar) {
        muc.replaceData rd = new muc.replaceData(oldChar, newChar);
        replaceDataList.add(rd);
    }

    public List<muc.replaceData> getReplaceDataList() {
        return replaceDataList;
    }

    public void setReplaceDataList(List<muc.replaceData> replaceDataList) {
        this.replaceDataList = replaceDataList;
    }
}
