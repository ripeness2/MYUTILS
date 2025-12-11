package org.ripeness.myutils;

public class muc {

    public static class replaceData {
        private String oldChar;
        private String newChar;

        public replaceData(String oldChar, String newChar) {
            this.oldChar = oldChar;
            this.newChar = newChar;
        }

        public String getOldChar() {
            return oldChar;
        }

        public String getNewChar() {
            return newChar;
        }

        public void setNewChar(String newChar) {
            this.newChar = newChar;
        }

        public void setOldChar(String oldChar) {
            this.oldChar = oldChar;
        }
    }

}
