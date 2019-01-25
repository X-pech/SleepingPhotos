package study.itmo.xpech.mdft.pic;

public class PicturesContent {

    public static Pic createPicItem(int position, String description, String srcUrl) {
        return new Pic(String.valueOf(position), description, srcUrl);
    }

    public static class Pic {
        public final String id;
        public final String description;
        public final String srcUrl;

        public Pic(String id, String description, String srcUrl) {
            this.id = id;
            this.description = description;
            this.srcUrl = srcUrl;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
