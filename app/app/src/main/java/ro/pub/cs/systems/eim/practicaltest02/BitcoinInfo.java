package ro.pub.cs.systems.eim.practicaltest02;

public class BitcoinInfo {
    String updatedTime = new String();
    String USDvalue = new String();
    String EURvalue = new String();

    public BitcoinInfo(){};

    public BitcoinInfo(String updatedTime, String USDvalue, String EURvalue) {
        this.USDvalue = USDvalue;
        this.EURvalue = EURvalue;
        this.updatedTime = updatedTime;
    }
}
