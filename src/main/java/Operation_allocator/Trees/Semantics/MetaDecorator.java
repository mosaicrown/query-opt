package Operation_allocator.Trees.Semantics;

import java.io.Serializable;
import java.util.List;

public abstract class MetaDecorator implements Serializable{

    public abstract List<Features> holds();
    public abstract boolean hasFeature(Features f);

}
