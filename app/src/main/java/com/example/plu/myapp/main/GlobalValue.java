package com.example.plu.myapp.main;

/**
 * 全局的key
 */
public class GlobalValue {

    /**
     * auth参数
     */
    public static String AUTH_PARAM = "auth";

    /**
     * 接口端约定的KEY
     */
    public static String AUTH_KEY = "4059F31A05E642AEA14A8482BB070336";

    /**
     * 礼物的地址
     */
    public static final String GIFT_HEAD_URL = "http://img.plures.net/live/props/";

    /**
     * 礼物的地址
     */
    public static final String GIFT_MIDDLE_URL = "/gift-control-b-";


    /**
     * 礼物的地址
     */
    public static final String GIFT_END_URL = ".png";



    public static final String FEED_URL = "http://www2.53kf.com/webCompany.php?arg=10081468&style=1";


    public static final int sendDelayed = 200;

//	测试账号：
//	2696583165
//	密码1qasw23e

    /**
     * 搜索内容
     */
    public static String EXTRA_KEYWORD = "extra_keyword";

    /**
     *
     */
    public static String FIRST_LOGIN = "first_login_v3.5";

    public static String IMAGE_LOADER_CACHE_DIR = "imageloader/Cache";

    /*******************
     * 分享的url
     ***************************/
    public static String URL_SHARE_SUIPAI = "http://star.longzhu.com/m/";
    public static String URL_SHARE_LIVE = "http://star.longzhu.com/m/";
    public static String URL_SHARE_VIDEO = "http://v.longzhu.com/";


    public static String SHARE_PREF_DATE_KEY = "PREF_DATE";
    public static String SHARE_PREF_AES_KEY = "PREF_AES";

    //音乐节Umeng版本号
    public static final int Umeng_Music_Festival_No=80;

    public interface Key {

        //播放器类型
        String CAMERALIVEROOM = "cameralive_room";//进入随拍房间提示语

        //聊天室类型，0.使用接口；1.使用websocket
        String CHATROOM_TYPE = "chatroom_type";//进入随拍房间提示语

        String DATA_GIFT_CONFIG = "key_gift_config";


        String CAMERA = "key_camera";//随拍名单状态

        String CAN_LIVE = "can_live"; //是否可推流

        //播放器类型
        String PLAYER_TYPE = "player_type";
        //游戏直播播放器类型
        String PLAYER_GAME_TYPE = "player_game_type";

        String PLAYER_GAME_FORMAT = "player_game_format";

        String SUIPAI_NOTIFY_LEVEL = "suipai_notify_level"; //随拍通知级别

        String MASTER_ID = "masterId";//主播id
        String PLAY_ID = "playId";//主播id


        String PLAY_MODE = "player_mode_3.6.1";//播放模式/3.6.1版本重置
        String TARGET_URL = "url";      //地址
        String LOCATION_STATUS = "location_status";    //是否开启定位
        String TARGET_TITLE = "title";   //标题

        String MSGPACK_ENABLE = "msgpack_enable";   //是否使用msgpack
        String COMMON_PLAY_MODE = "common_play_mode";//普通房间默认播放模式
    }
}

