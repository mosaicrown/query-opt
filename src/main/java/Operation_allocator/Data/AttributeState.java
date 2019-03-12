package Operation_allocator.Data;

import java.io.Serializable;

public enum AttributeState implements Serializable{
    /**
     * In this enum is contained a demonstrative set of attribute state
     * A state is defined as the wrapping technique application to an attribute
     */

    PLAINTEXT,          //attribute is in plaintext

    RANDSYMENC,         //attribute is encrypted with randomized symmetric encryption

    DETSYMENC,          //attribute is encrypted with deterministic symmetric encryption

    PALCRYPTENC,        //attribute is encrypted with Paillier crypto-system (so for example, addition is evaluable...)

    OPESCHENC           //attribute is encrypted with OPE scheme

}
