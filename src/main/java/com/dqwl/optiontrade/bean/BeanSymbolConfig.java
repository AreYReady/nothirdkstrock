package com.dqwl.optiontrade.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xjunda
 * @date 2016-09-12
 * 服务器配置的保证金，到期时间，派息比例，等
 * @link TradeIndexActivity # onGetSymbolShow   sticky = true
 */
public class BeanSymbolConfig {

    /**
     * count : 10
     * msg_type : 320
     * step_min : 20
     * symbols : [{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55},{"cycle":3600,"desc":"1小时","percent":55}],"desc":"黄金/美元","step_min":20,"symbol":"XAUUSDbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":300,"desc":"5分钟","percent":55}],"desc":"欧元/日元","step_min":20,"symbol":"EURJPYbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55},{"cycle":3600,"desc":"1小时","percent":55}],"desc":"欧元/美元","step_min":20,"symbol":"EURUSDbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":300,"desc":"5分钟","percent":55},{"cycle":3600,"desc":"1小时","percent":55}],"desc":"英镑/美元","step_min":20,"symbol":"GBPUSDbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55}],"desc":"美元/日元","step_min":20,"symbol":"USDJPYbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55}],"desc":"澳元/美元","step_min":20,"symbol":"AUDUSDbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55}],"desc":"欧元/英镑","step_min":20,"symbol":"EURGBPbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55}],"desc":"英国/原油","step_min":20,"symbol":"UKOilbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55}],"desc":"美国/原油","step_min":20,"symbol":"USOilbo","vol_max":100,"vol_min":10},{"cycles":[{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55}],"desc":"香港/恒生","step_min":20,"symbol":"HK50bo","vol_max":100,"vol_min":10}]
     * vol_max : 100
     * vol_min : 10
     */

    private int count;
    private int msg_type;
    private int step_min;
    private int vol_max;
    private int vol_min;
    /**
     * cycles : [{"cycle":60,"desc":"60秒","percent":85},{"cycle":120,"desc":"120秒","percent":75},{"cycle":180,"desc":"180秒","percent":65},{"cycle":300,"desc":"5分钟","percent":55},{"cycle":3600,"desc":"1小时","percent":55}]
     * desc : 黄金/美元
     * step_min : 20
     * symbol : XAUUSDbo
     * vol_max : 100
     * vol_min : 10
     */

    private ArrayList<SymbolsBean> symbols;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public int getStep_min() {
        return step_min;
    }

    public void setStep_min(int step_min) {
        this.step_min = step_min;
    }

    public int getVol_max() {
        return vol_max;
    }

    public void setVol_max(int vol_max) {
        this.vol_max = vol_max;
    }

    public int getVol_min() {
        return vol_min;
    }

    public void setVol_min(int vol_min) {
        this.vol_min = vol_min;
    }

    public ArrayList<SymbolsBean> getSymbols() {
        return symbols;
    }

    public void setSymbols(ArrayList<SymbolsBean> symbols) {
        this.symbols = symbols;
    }

    /**
     * @link MinaTimeChartActivity #onGetAllSymbol  sticky = true
     * @Link TradeIndexActivity #onGetSymbolShow
     */
    public static class SymbolsBean {
        private String desc;
        private int step_min;
        private String symbol;
        private int vol_max;
        private int vol_min;
//        private int tz_delta;

//        public int getTz_delta() {
//            return tz_delta;
//        }
//
//        public void setTz_delta(int tz_delta) {
//            this.tz_delta = tz_delta;
//        }

        private int flag;
        private float productPrice;

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public float getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(float productPrice) {
            if (productPrice > this.productPrice) {
                flag = 2;
            } else if (productPrice < this.productPrice) {
                flag = 1;
            } else {
                flag = 3;
            }
            this.productPrice = productPrice;
        }

        /**
         * cycle : 60
         * desc : 60秒
         * percent : 85
         */

        private List<CyclesBean> cycles;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getStep_min() {
            return step_min;
        }

        public void setStep_min(int step_min) {
            this.step_min = step_min;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public int getVol_max() {
            return vol_max;
        }

        public void setVol_max(int vol_max) {
            this.vol_max = vol_max;
        }

        public int getVol_min() {
            return vol_min;
        }

        public void setVol_min(int vol_min) {
            this.vol_min = vol_min;
        }

        public List<CyclesBean> getCycles() {
            return cycles;
        }

        public void setCycles(List<CyclesBean> cycles) {
            this.cycles = cycles;
        }

        public static class CyclesBean {
            private int cycle;
            private String desc;
            private int percent;
//            private Times times;
//
//            public Times getTimes() {
//                return times;
//            }
//
//            public void setTimes(Times times) {
//                this.times = times;
//            }

            public int getCycle() {
                return cycle;
            }

            public void setCycle(int cycle) {
                this.cycle = cycle;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public int getPercent() {
                return percent;
            }

            public void setPercent(int percent) {
                this.percent = percent;
            }

//            public static class Times {
//                private String b;
//                private String e;
//
//                public String getB() {
//                    return b;
//                }
//
//                public void setB(String b) {
//                    this.b = b;
//                }
//
//                public String getE() {
//                    return e;
//                }
//
//                public void setE(String e) {
//                    this.e = e;
//                }
//            }
        }

    }
}
