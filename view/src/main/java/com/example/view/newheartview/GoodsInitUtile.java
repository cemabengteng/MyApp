package com.example.view.newheartview;


import com.example.view.R;

public class GoodsInitUtile {

    private static int goodType1 = R.drawable.ic_qipao_fen_1;
    private static int goodType2 = R.drawable.ic_qipao_fen_2;
    private static int goodType3 = R.drawable.ic_qipao_fen_3;
    private static int goodType4 = R.drawable.ic_qipao_hong_1;
    private static int goodType5 = R.drawable.ic_qipao_hong_2;
    private static int goodType6 = R.drawable.ic_qipao_hong_3;
    private static int goodType7 = R.drawable.ic_qipao_huang_1;
    private static int goodType8 = R.drawable.ic_qipao_huang_2;
    private static int goodType9 = R.drawable.ic_qipao_huang_3;
    private static int goodType10 = R.drawable.ic_qipao_ju_1;
    private static int goodType11 = R.drawable.ic_qipao_ju_2;
    private static int goodType12 = R.drawable.ic_qipao_ju_3;
    private static int goodType13 = R.drawable.ic_qipao_lan_1;
    private static int goodType14 = R.drawable.ic_qipao_lan_2;
    private static int goodType15 = R.drawable.ic_qipao_lan_3;
    private static int goodType16 = R.drawable.ic_qipao_lv_1;
    private static int goodType17 = R.drawable.ic_qipao_lv_2;
    private static int goodType18 = R.drawable.ic_qipao_lv_3;
    private static int goodType19 = R.drawable.ic_qipao_zi_1;
    private static int goodType20 = R.drawable.ic_qipao_zi_2;
    private static int goodType21 = R.drawable.ic_qipao_zi_3;

    private static int TYPECOUNT = 21;


    /*
     * 获取点赞的类型
     */
    public static int getGoodsType(int type) {
        int rType = goodType1;
        switch (type) {
            case 1:
                rType = goodType1;
                break;
            case 2:
                rType = goodType2;
                break;
            case 3:
                rType = goodType3;
                break;
            case 4:
                rType = goodType4;
                break;
            case 5:
                rType = goodType5;
                break;
            case 6:
                rType = goodType6;
                break;
            case 7:
                rType = goodType7;
                break;
            case 8:
                rType = goodType8;
                break;
            case 9:
                rType = goodType9;
                break;
            case 10:
                rType = goodType10;
                break;
            case 11:
                rType = goodType11;
                break;
            case 12:
                rType = goodType12;
                break;
            case 13:
                rType = goodType13;
                break;
            case 14:
                rType = goodType14;
                break;
            case 15:
                rType = goodType15;
                break;
            case 16:
                rType = goodType16;
                break;
            case 17:
                rType = goodType17;
                break;
            case 18:
                rType = goodType18;
                break;
            case 19:
                rType = goodType19;
                break;
            case 20:
                rType = goodType20;
                break;
            case 21:
                rType = goodType21;
                break;
        }
        return rType;
    }

}
