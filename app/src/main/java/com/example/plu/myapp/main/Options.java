package com.example.plu.myapp.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @ClassName: Options
 * @Description: 礼物具体规则
 * @author: zxl
 * @date: 2015年1月14日 下午1:30:17
 */
public class Options implements Serializable, Parcelable {

	@Override
	public String toString() {
		return "Options [id=" + id + ", giftId=" + giftId + ", num=" + num
				+ ", name=" + name + ", note=" + note + ", canInput="
				+ canInput + "]";
	}

	/**
	 * 
	 * @fieldName: serialVersionUID
	 * 
	 * @fieldType: long
	 * 
	 * @Description:礼物具体规则
	 * */
	private static final long serialVersionUID = 6420955952815147865L;
	private int id;// 自增id
	private int giftId;// 礼物id
	private int num;// 数量
	private String name;// 礼物名称
	private String note;// 档位的文案，如“本房间通知 挖掘机+3”
	private boolean canInput;// 该档位是否要显示为可输入的状态

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGiftId() {
		return giftId;
	}

	public void setGiftId(int giftId) {
		this.giftId = giftId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isCanInput() {
		return canInput;
	}

	public void setCanInput(boolean canInput) {
		this.canInput = canInput;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeInt(this.giftId);
		dest.writeInt(this.num);
		dest.writeString(this.name);
		dest.writeString(this.note);
		dest.writeByte(canInput ? (byte) 1 : (byte) 0);
	}

	public Options() {
	}

	protected Options(Parcel in) {
		this.id = in.readInt();
		this.giftId = in.readInt();
		this.num = in.readInt();
		this.name = in.readString();
		this.note = in.readString();
		this.canInput = in.readByte() != 0;
	}

	public static final Creator<Options> CREATOR = new Creator<Options>() {
		public Options createFromParcel(Parcel source) {
			return new Options(source);
		}

		public Options[] newArray(int size) {
			return new Options[size];
		}
	};
}
