package raj.jvkhu.internetspeedprogressbar.utilities;

public class SpeedCarrier {

    public static final int BITS = 0;
    public static final int K_BITS = 1;
    public static final int M_BITS = 2;

    private double upSpeed = 0;
    private double dlSpeed = 0;
    private int upType = 0;
    private int dlType = 0;

    public SpeedCarrier() {
    }

    public double getUpSpeed() {
        return upSpeed;
    }

    public void setUpSpeed(double upSpeed) {
        this.upSpeed = upSpeed;
    }

    public double getDlSpeed() {
        return dlSpeed;
    }

    public void setDlSpeed(double dlSpeed) {
        this.dlSpeed = dlSpeed;
    }

    public int getUpType() {
        return upType;
    }

    public void setUpType(int upType) {
        this.upType = upType;
    }

    public int getDlType() {
        return dlType;
    }

    public void setDlType(int dlType) {
        this.dlType = dlType;
    }

    public String TypeToString(int type) {
        if (type == BITS) {
            return "B/s";
        }
        if (type == K_BITS) {
            return "kB/s";
        } else return "mB/s";

    }
}
