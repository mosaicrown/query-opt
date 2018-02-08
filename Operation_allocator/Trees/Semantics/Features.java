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
     * Data confidentiality feature
     */
    PUBLIC,             //data visible to everyone
    RESERVED,           //data visible to trusted provider
    CONFIDENTIAL,       //data visible if encrypted to trusted provider
    SECRET,             //data visible only to data owner
    /**
     * No specific
     */
    NOACTION


}
