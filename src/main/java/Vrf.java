import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;

/**
 * This is VRF realization which was proposed by Yevgeniy Dodis
 * Run:
 *      PK = P^sk
 *      y = Fsk(x) = e(P,P)^(1/(x+sk))
 *      proof = psk(x) = P^(1/(x+sk))
 * Verify:
 *      e(P^x*PK,proof) = e(P,P)
 *      e(P,proof) = y
 *
 */
public class Vrf extends BilinearPairing {
    private static Element seed;           //系统轮次
    private static Element sk;             //sk
    private static Element Hash;           //VRF哈希
    private static Element Proof;          //VRF零知识证明

    public Vrf(){
        super();
        P = G1.newRandomElement().getImmutable();
    }

    public Element getHash() {
        return Hash;
    }

    public Element getProof() { return Proof; }

    public Element getPk() {
        return (P.duplicate().powZn(sk.duplicate()).getImmutable());    //PK=P^sk
    }

    /**
     * 运行VRF算法, 计算VRF哈希和VRF零知识证明
     * VRF_Hash = e(P,P)^(1/(seed+sk))
     * VRF_Proof = P^(1/(seed+sk))
     */
    public void run(byte[] sk0, byte[] seed0){
        seed = Zr.newElement();
        sk = Zr.newElement();
        Proof = G1.newElement().getImmutable();
        Hash = GT.newElement().getImmutable();

        //seed,sk,sks初始化
        seed.setFromBytes(seed0);
        sk.setFromBytes(sk0);
        Element sks = seed.add(sk.duplicate()).getImmutable();

        // 计算VRF哈希
        Hash = pairing.pairing(P.duplicate(), P.duplicate()).getImmutable(); //计算e(P,P)
        Hash = Hash.powZn(sks.duplicate().invert()).getImmutable();          //计算e(P,P)^(1/(sk+seed))

        // 计算VRF零知识证明
        Proof = P.duplicate().powZn(sks.duplicate().invert()).getImmutable();//计算P^(1/(sk+seed))
    }

    /**
     * VRF校验
     * VerifyProof = e(P^x*PK,VRF_Proof) = e(P,P)
     * VerifyHash = e(P,VRF_Proof) = VRF_Hash
     */
    public boolean verify(byte[] hash, byte[] proof, byte[] pk, byte[] seed0) {
        Element vrfHash = GT.newElement();
        Element vrfProof = G1.newElement();
        Element vrfPk = G1.newElement();
        vrfHash.setFromBytes(hash);
        vrfProof.setFromBytes(proof);
        vrfPk.setFromBytes(pk);
        seed.setFromBytes(seed0);

        Element VerifyHash = pairing.pairing(P.duplicate(), vrfProof.duplicate());  //计算e(P,VRF_Proof)
        Element VerifyProof = P.duplicate().powZn(seed).mul(vrfPk);                 // P^seed*PK
        VerifyProof = pairing.pairing(VerifyProof, vrfProof.duplicate());           //计算e(P^seed*PK,VRF_Proof)
        Element vp = pairing.pairing(P.duplicate(),P.duplicate());


        if (VerifyHash.isEqual(vrfHash) && VerifyProof.isEqual(vp)){
            System.out.println("VRF Verify Success!");
            return true;
        }else{
            System.out.println("VRF Verify Fail!");
            return false;
        }
    }

    public double simulate(byte[] seed0){
        seed = Zr.newElement();
        seed.setFromBytes(seed0);
        sk = Zr.newRandomElement();
        Element sks = seed.add(sk.duplicate()).getImmutable();

        Proof = G1.newElement().getImmutable();
        Hash = GT.newElement().getImmutable();

        // 计算VRF哈希
        Hash = pairing.pairing(P.duplicate(), P.duplicate()).getImmutable(); //计算e(P,P)
        Hash = Hash.powZn(sks.duplicate()).getImmutable();                   //计算e(P,P)^(sk+seed)

        // 计算hash/(2^hashlen)
        /*
        // test
        byte[] b = Hash.toBytes();                          //66
        byte[] a = Hash.toBigInteger().toByteArray();       //33
        System.out.println(Hash.getLengthInBytes());
        System.out.println(Hash);
        System.out.println(Utils.bytesToDouble(b));
        System.out.println(Hash.toBigInteger());
        */
        double hash = Hash.toBigInteger().doubleValue();
        double r = hash/(Math.pow(2,Hash.toBigInteger().bitLength()));  // r值在0.5～1.0之间
        return r;
    }

}
