public class Utils {
    /**
     * 二项分布
     * @param k 抽取的元素个数
     * @param w 抽取元素总数
     * @param p 抽中的概率
     */
    public static double binomial(int k, int w, double p){
        double r=1;
        int i=w;
        while(i!=0){
            if(i>k)
                r*=i;
            else
                r*=p;
            i--;
        }
        i=w-k;
        while(i!=0){
            r/=(i--);
            r*=(1-p);
        }
        return r;
    }

    public static double bytesToDouble(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }
}
