package Data;

import java.io.Serializable;

public enum AttributeState implements Serializable{

    PLAINTEXT,          //attribute is in plaintext

    RANDSYMENC,         //attribute is encrypted with randomized symmetric encryption

    DETSYMENC,          //attribute is encrypted with deterministic symmetric encryption

    PALCRYPTENC,        //attribute is encrypted with Pallier crypto-system

    OPESCHENC           //attribute is encrypted with OPE scheme

}
