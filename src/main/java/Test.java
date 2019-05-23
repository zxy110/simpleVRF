import java.math.BigInteger;

public class Test {
    public static void main(String[] args){

        byte[] sk = "sk".getBytes();
        byte[] seed = "seed".getBytes();
        int t=1, w=2, W=3;
        /*
        Select s = new Select();
        int j = s.sortition(sk,seed,t,w,W);
        s.verifySort(s.pk, s.hash, seed, t, w, W, j);
         */

        //计算hash/(2^hashlen)分布
        Vrf vrf = new Vrf();
        int num = 10;
        int[] arr = new int[num];
        for(int i=0;i<num;i++){
            arr[i]=0;
        }
        int sum=0;
        for(int j=0;j<10;j++) {
            for (int i = 0; i < 10000; i++) {
                double r = (vrf.simulate(seed)-0.5)*2;
                int p = 0;
                while (r > (double) (p + 1) / 10 && p<10)   p+=1;
                arr[p]++;
            }

            for (int i = 0; i < num; i++) {
                System.out.print(arr[i] + " ");
            }
            System.out.println();
        }
    }
}

//2：2：2：2：2：1：1：1：1：1
//1：1：1：1：1：1：1：1：1：0.5