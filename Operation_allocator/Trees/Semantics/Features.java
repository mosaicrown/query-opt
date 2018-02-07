package Trees.Semantics;

public enum Features {

    /**
     * Cost features
     */
    EXPDATADOMCOST,     //the price of moving is greater than compunting
    EXPCPUDOMCOST,      //the price of compunting is greater than moving
    /**
     * Encryption features
     */
    ENCRYPTEDHOM,       //data is ciphered homomorphic
    ENCRYPTEDSYM,       //data is ciphered symmetric
    NOTENCRYPTED,       //data is readable
    /**
     * No specific
     */
    NOACTION


}
