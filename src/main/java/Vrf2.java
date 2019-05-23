import it.unisa.dia.gas.jpbc.Element;

/**
 * This is VRF realization which was proposed by zxy
 * Run:
 *      PK = sk*P
 *      y = Fsk(x) = e(P,P)^(x+sk)
 *      proof = psk(x) = (x+sk)*P
 * Verify:
 *      y = e(P,proof)
 *      proof = PK + x*P
 *
 */
public class Vrf2 extends BilinearPairing {
    private static Element seed;           //种子x
    private static Element sk;             //私钥sk
    private static Element Hash;           //VRF哈希
    private static Element Proof;          //VRF零知识证明

    public Vrf2(){
        super();
        P = G1.newRandomElement().getImmutable();
    }

    public Element getHash() {
        return Hash;
    }

    public Element getProof() { return Proof; }

    public Element getPk() { return (P.duplicate().mulZn(sk.duplicate()).getImmutable()); }

    /**
     * 运行VRF算法, 计算VRF哈希和VRF零知识证明
     * VRF_Hash = e(P,P)^(sk+seed)
     * VRF_Proof = (sk+seed)*P
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
        Hash = Hash.powZn(sks.duplicate()).getImmutable();                   //计算e(P,P)^(sk+seed)

        // 计算VRF零知识证明
        Proof = P.duplicate().mulZn(sks.duplicate()).getImmutable();         //计算(sk+seed)*P
    }

    /**
     * VRF校验
     * VerifyHash = e(P,VRF_Proof)
     * VerifyProof = pk+seed*P
     * @param hash
     * @param proof
     * @param pk
     * @return
     */
    public boolean verify(byte[] hash, byte[] proof, byte[] pk, byte[] seed0) {
        Element vrfHash = GT.newElement();
        Element vrfProof = G1.newElement();
        Element vrfPk = G1.newElement();
        vrfHash.setFromBytes(hash);
        vrfProof.setFromBytes(proof);
        vrfPk.setFromBytes(pk);
        seed.setFromBytes(seed0);


        Element VerifyHash = pairing.pairing(P.duplicate(), vrfProof.duplicate());          //计算e(P,VRF_Proof)
        Element VerifyProof = P.duplicate().mulZn(seed).getImmutable();                     //计算seed*P
        VerifyProof = vrfPk.duplicate().add(VerifyProof.duplicate()).getImmutable();        //计算VRF_Pk+seed*P


        if (VerifyHash.isEqual(vrfHash) && VerifyProof.isEqual(vrfProof)){
            System.out.println("VRF Verify Success!");
            return true;
        }else{
            System.out.println("VRF Verify Fail!");
            return false;
        }
    }
}
