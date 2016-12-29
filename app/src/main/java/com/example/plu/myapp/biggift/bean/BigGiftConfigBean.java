package com.example.plu.myapp.biggift.bean;

import java.io.Serializable;

/**
 * 大礼物动画文件配置信息
 * <p>
 * 包含路径，文件名，动画配置，运动配置等信息
 * <p>
 * Created by plu on 2016/11/1.
 */
public class BigGiftConfigBean implements Serializable {

    private String path; // 路径

    private String name; // 文件名

    private boolean isDefault; // 默认动画，在asset包下


    /**
     * displayFrame : {"height":{"multiby":0.8,"offset":0},"width":{"multiby":0.8,"offset":0}}
     * edges : {"bottom":{"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0},"centerX":{"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0},"centerY":{"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0},"left":{"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0},"right":{"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0},"top":{"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0}}
     * orignFram : {"height":100,"width":100}
     * random : false
     * textFontSize : 40
     * textImgName : ship_14.png
     */

    private DisplayFrameBean displayFrame;
    private EdgesBean edges;
    private OrignFramBean orignFram;
    private boolean random;
    private String textFontSize;
    private String textImgName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public DisplayFrameBean getDisplayFrame() {
        return displayFrame;
    }

    public void setDisplayFrame(DisplayFrameBean displayFrame) {
        this.displayFrame = displayFrame;
    }

    public EdgesBean getEdges() {
        return edges;
    }

    public void setEdges(EdgesBean edges) {
        this.edges = edges;
    }

    public OrignFramBean getOrignFram() {
        return orignFram;
    }

    public void setOrignFram(OrignFramBean orignFram) {
        this.orignFram = orignFram;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean random) {
        this.random = random;
    }

    public String getTextSize() {
        return textFontSize;
    }

    public void setTextSize(String textFontSize) {
        this.textFontSize = textFontSize;
    }

    public String getTextImgName() {
        return textImgName;
    }

    public void setTextImgName(String textImgName) {
        this.textImgName = textImgName;
    }

    public static class DisplayFrameBean {
        /**
         * height : {"multiby":0.8,"offset":0}
         * width : {"multiby":0.8,"offset":0}
         */

        private HeightBean height;
        private WidthBean width;

        public HeightBean getHeight() {
            return height;
        }

        public void setHeight(HeightBean height) {
            this.height = height;
        }

        public WidthBean getWidth() {
            return width;
        }

        public void setWidth(WidthBean width) {
            this.width = width;
        }

        public static class HeightBean {
            /**
             * multiby : 0.8
             * offset : 0
             */

            private double multiby;
            private int offset;

            public double getMultiby() {
                return multiby;
            }

            public void setMultiby(double multiby) {
                this.multiby = multiby;
            }

            public int getOffset() {
                return offset;
            }

            public void setOffset(int offset) {
                this.offset = offset;
            }
        }

        public static class WidthBean {
            /**
             * multiby : 0.8
             * offset : 0
             */

            private double multiby;
            private int offset;

            public double getMultiby() {
                return multiby;
            }

            public void setMultiby(double multiby) {
                this.multiby = multiby;
            }

            public int getOffset() {
                return offset;
            }

            public void setOffset(int offset) {
                this.offset = offset;
            }
        }
    }

    public static class EdgesBean {
        /**
         * bottom : {"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0}
         * centerX : {"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0}
         * centerY : {"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0}
         * left : {"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0}
         * right : {"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0}
         * top : {"landMultiby":0,"landOffset":0,"portMultiby":0,"portOffset":0}
         */

        private SizeBean bottom;
        private SizeBean centerX;
        private SizeBean centerY;
        private SizeBean left;
        private SizeBean right;
        private SizeBean top;

        public SizeBean getBottom() {
            return bottom;
        }

        public void setBottom(SizeBean bottom) {
            this.bottom = bottom;
        }

        public SizeBean getCenterX() {
            return centerX;
        }

        public void setCenterX(SizeBean centerX) {
            this.centerX = centerX;
        }

        public SizeBean getCenterY() {
            return centerY;
        }

        public void setCenterY(SizeBean centerY) {
            this.centerY = centerY;
        }

        public SizeBean getLeft() {
            return left;
        }

        public void setLeft(SizeBean left) {
            this.left = left;
        }

        public SizeBean getRight() {
            return right;
        }

        public void setRight(SizeBean right) {
            this.right = right;
        }

        public SizeBean getTop() {
            return top;
        }

        public void setTop(SizeBean top) {
            this.top = top;
        }

        public static class SizeBean {
            private double landMultiby;
            private int landOffset;
            private double portMultiby;
            private int portOffset;

            public double getLandMultiby() {
                return landMultiby;
            }

            public void setLandMultiby(double landMultiby) {
                this.landMultiby = landMultiby;
            }

            public int getLandOffset() {
                return landOffset;
            }

            public void setLandOffset(int landOffset) {
                this.landOffset = landOffset;
            }

            public double getPortMultiby() {
                return portMultiby;
            }

            public void setPortMultiby(double portMultiby) {
                this.portMultiby = portMultiby;
            }

            public int getPortOffset() {
                return portOffset;
            }

            public void setPortOffset(int portOffset) {
                this.portOffset = portOffset;
            }
        }
    }

    public static class OrignFramBean {
        /**
         * height : 100
         * width : 100
         */

        private int height;
        private int width;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }
}
