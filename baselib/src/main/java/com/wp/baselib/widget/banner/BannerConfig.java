package com.wp.baselib.widget.banner;


public class BannerConfig {
    /**
     * indicator style
     *
     *
     * BannerConfig.NOT_INDICATOR	不显示指示器和标题	setBannerStyle
     BannerConfig.CIRCLE_INDICATOR	显示圆形指示器	setBannerStyle
     BannerConfig.NUM_INDICATOR	显示数字指示器	setBannerStyle
     BannerConfig.NUM_INDICATOR_TITLE	显示数字指示器和标题	setBannerStyle
     BannerConfig.CIRCLE_INDICATOR_TITLE	显示圆形指示器和标题（垂直显示）	setBannerStyle
     BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE	显示圆形指示器和标题（水平显示）	setBannerStyle
     BannerConfig.LEFT	指示器居左	setIndicatorGravity
     BannerConfig.CENTER	指示器居中	setIndicatorGravity
     BannerConfig.RIGHT	指示器居右	setIndicatorGravity
     */
    public static final int NOT_INDICATOR = 0;
    public static final int CIRCLE_INDICATOR = 1;
    public static final int NUM_INDICATOR = 2;
    public static final int NUM_INDICATOR_TITLE = 3;
    public static final int CIRCLE_INDICATOR_TITLE = 4;
    public static final int CIRCLE_INDICATOR_TITLE_INSIDE = 5;
    /**
     * indicator gravity
     */
    public static final int LEFT = 5;
    public static final int CENTER = 6;
    public static final int RIGHT = 7;

    /**
     * banner
     */
    public static final int PADDING_SIZE = 5;
    public static final int TIME = 2000;
    public static final int DURATION = 800;
    public static final boolean IS_AUTO_PLAY = true;
    public static final boolean IS_SCROLL = true;

    /**
     * title style
     */
    public static final int TITLE_BACKGROUND = -1;
    public static final int TITLE_HEIGHT = -1;
    public static final int TITLE_TEXT_COLOR = -1;
    public static final int TITLE_TEXT_SIZE = -1;

}
