package com.example.plu.myapp.main;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 * 礼物
 */
public class Gifts implements Parcelable, Cloneable {

    private static final long serialVersionUID = -2963585563278950147L;
    private int id;// 自增id
    private int giftId;// 礼物id
    private String name;// 礼物名称
    private String startTime;// 其实有效期
    private String endTime;// 礼物
    private String title;// 礼物名称
    private int maxLimit;// 该礼物的最大可赠送数量
    private double moneyCost;// 礼物价格（单位元宝）
    private double beanCost;// 礼物价格（单位元宝）
    private int onlineTimeCost;// 礼物的生成时间，标识礼物需要用户在线多少时间可生成一个，鲜花这样的免费道具设有这个参数
    private boolean isSelect;
    private int isOptions;//是否显示多选
    private int experience;//经验值


    private int contribution;//贡献值
    private int kind;

    private int type;
    private String img;
    private String newBannerIcon;
    private List<Options> optionses;//礼物配置数据
    private int costType;   //花费类型: 1龙币、2龙豆、3在线时长、4库存
    private int costValue;  //花费值:
    private int comboInteval;  //连击时限(秒): 大于0就代表是连击道具
    private String backgroundAppIcon2;  //礼物zip包Crc32码
    private String backgroundAppIcon2Url; //礼物zip包下载路径
    private String consumeAppIcon; //App端消费时动态礼物Crc32码
    private String consumeAppIconUrl; //App端消费时动态礼物zip包下载路径
    private int freeGiftNum;   //免费礼物的个数

    public String getGiftIcon() {
        String s = "";
        if (!TextUtils.isEmpty(backgroundAppIcon2) &&
                !TextUtils.isEmpty(backgroundAppIcon2Url)) {
            s = backgroundAppIcon2;
        } else if (!TextUtils.isEmpty(consumeAppIcon) &&
                !TextUtils.isEmpty(consumeAppIconUrl)) {
            s = consumeAppIcon;
        }
        return s;
    }

    public String getGiftIconUrl() {
        String url = "";
        if (!TextUtils.isEmpty(backgroundAppIcon2) &&
                !TextUtils.isEmpty(backgroundAppIcon2Url)) {
            url = backgroundAppIcon2Url;
        } else if (!TextUtils.isEmpty(consumeAppIcon) &&
                !TextUtils.isEmpty(consumeAppIconUrl)) {
            url = consumeAppIconUrl;
        }
        return url;
    }

    public String getNewBannerIcon() {
        return newBannerIcon;
    }

    public void setNewBannerIcon(String backgroundAppIcon) {
        this.newBannerIcon = backgroundAppIcon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getKind() {
        return kind;
    }

    public int getContribution() {
        return contribution;
    }

    public void setContribution(int contribution) {
        this.contribution = contribution;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public int getOnlineTimeCost() {
        return onlineTimeCost;
    }

    public void setOnlineTimeCost(int onlineTimeCost) {
        this.onlineTimeCost = onlineTimeCost;
    }

    public double getMoneyCost() {
        return moneyCost;
    }

    public void setMoneyCost(double moneyCost) {
        this.moneyCost = moneyCost;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(int maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public double getBeanCost() {
        return beanCost;
    }

    public void setBeanCost(double beanCost) {
        this.beanCost = beanCost;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int isOptions() {
        return isOptions;
    }

    public void setOptions(int options) {
        isOptions = options;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }


    public List<Options> getOptionses() {
        return optionses;
    }

    public void setOptionses(List<Options> optionses) {
        this.optionses = optionses;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getCostType() {
        return costType;
    }

    public void setCostType(int costType) {
        this.costType = costType;
    }

    public int getCostValue() {
        return costValue;
    }

    public void setCostValue(int costValue) {
        this.costValue = costValue;
    }

    public int getComboInteval() {
        return comboInteval;
    }

    public void setComboInteval(int comboInteval) {
        this.comboInteval = comboInteval;
    }

    public String getBackgroundAppIcon2() {
        return backgroundAppIcon2;
    }

    public void setBackgroundAppIcon2(String backgroundAppIcon2) {
        this.backgroundAppIcon2 = backgroundAppIcon2;
    }

    public String getBackgroundAppIcon2Url() {
        return backgroundAppIcon2Url;
    }

    public void setBackgroundAppIcon2Url(String backgroundAppIcon2Url) {
        this.backgroundAppIcon2Url = backgroundAppIcon2Url;
    }

    public String getConsumeAppIcon() {
        return consumeAppIcon;
    }

    public void setConsumeAppIcon(String consumeAppIcon) {
        this.consumeAppIcon = consumeAppIcon;
    }

    public String getConsumeAppIconUrl() {
        return consumeAppIconUrl;
    }

    public void setConsumeAppIconUrl(String consumeAppIconUrl) {
        this.consumeAppIconUrl = consumeAppIconUrl;
    }

    public int getFreeGiftNum() {
        return freeGiftNum;
    }

    @Override
    public String toString() {
        return "Gifts{" +
                "id=" + id +
                ", giftId=" + giftId +
                ", name='" + name + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", title='" + title + '\'' +
                ", maxLimit=" + maxLimit +
                ", moneyCost=" + moneyCost +
                ", beanCost=" + beanCost +
                ", onlineTimeCost=" + onlineTimeCost +
                ", isSelect=" + isSelect +
                ", isOptions=" + isOptions +
                ", experience=" + experience +
                ", kind=" + kind +
                ", type=" + type +
                ", img='" + img + '\'' +
                ", newBannerIcon='" + newBannerIcon + '\'' +
                ", optionses=" + optionses +
                ", costType=" + costType +
                ", costValue=" + costValue +
                ", comboInteval=" + comboInteval +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.giftId);
        dest.writeString(this.name);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.title);
        dest.writeInt(this.maxLimit);
        dest.writeDouble(this.moneyCost);
        dest.writeDouble(this.beanCost);
        dest.writeInt(this.onlineTimeCost);
        dest.writeByte(isSelect ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isOptions);
        dest.writeInt(this.experience);
        dest.writeInt(this.kind);
        dest.writeInt(this.type);
        dest.writeString(this.img);
        dest.writeString(this.newBannerIcon);
        dest.writeTypedList(optionses);
        dest.writeInt(costType);
        dest.writeInt(costValue);
        dest.writeInt(comboInteval);
        dest.writeString(backgroundAppIcon2);
        dest.writeString(backgroundAppIcon2Url);
        dest.writeString(consumeAppIcon);
        dest.writeString(consumeAppIconUrl);
    }

    public Gifts() {
    }

    protected Gifts(Parcel in) {
        this.id = in.readInt();
        this.giftId = in.readInt();
        this.name = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.title = in.readString();
        this.maxLimit = in.readInt();
        this.moneyCost = in.readDouble();
        this.beanCost = in.readDouble();
        this.onlineTimeCost = in.readInt();
        this.isSelect = in.readByte() != 0;
        this.isOptions = in.readInt();
        this.experience = in.readInt();
        this.kind = in.readInt();
        this.type = in.readInt();
        this.img = in.readString();
        this.newBannerIcon = in.readString();
        this.optionses = in.createTypedArrayList(Options.CREATOR);
        this.costType = in.readInt();
        this.costValue = in.readInt();
        this.comboInteval = in.readInt();
        this.backgroundAppIcon2 = in.readString();
        this.backgroundAppIcon2Url = in.readString();
        this.consumeAppIcon = in.readString();
        this.consumeAppIconUrl = in.readString();
    }

    public static final Creator<Gifts> CREATOR = new Creator<Gifts>() {
        public Gifts createFromParcel(Parcel source) {
            return new Gifts(source);
        }

        public Gifts[] newArray(int size) {
            return new Gifts[size];
        }
    };

    @Override
    public Gifts clone() {
        Gifts g = null;
        try {
            g = (Gifts) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return g;
    }

    public void setFreeGiftNum(int num) {
        this.freeGiftNum = num;
    }
}
