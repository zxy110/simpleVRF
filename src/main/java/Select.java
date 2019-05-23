
public class Select {
    private Vrf vrf;
    public byte[] hash;
    public byte[] proof;
    public byte[] pk;

    public Select(){
        vrf = new Vrf();
    }

    public int sortition(byte[] sk, byte[] seed, int t, int w, int W){
        vrf.run(sk,seed);
        hash = vrf.getHash().toBytes();
        proof = vrf.getProof().toBytes();
        pk = vrf.getPk().toBytes();


        // 计算被选中的sub-users的值
        double p = (double)t/W;
        int k=0;
        double sum = Utils.binomial(k,w,p);
        double test = Utils.bytesToDouble(hash)/Math.pow(2,hash.length);
        while(test>sum){
            sum += Utils.binomial(++k,w,p);
        }
        return k;
    }

    public boolean verifySort(byte[] pk, byte[] hash, byte[] seed, int t, int w, int W, int j){
        // 校验vrf
        if(!vrf.verify(hash,proof,pk,seed))  return  false;

        // 计算被选中的sub-users的值
        double p = (double)t/W;
        int k=0;
        double sum = Utils.binomial(k,w,p);
        double test = Utils.bytesToDouble(hash)/Math.pow(2,hash.length);
        while(test>sum){
            sum += Utils.binomial(++k,w,p);
        }

        // 判断是否被抽中
        if(k==j){
            System.out.println("Verify Success!");
            return true;
        }else{
            System.out.println("Verify Fail!");
            return false;
        }
    }

}
