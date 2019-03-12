package Operation_allocator.Trees.Semantics;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class MetaChoke<E> extends MetaDecorator implements Serializable {

    private List<Features> features;
    private E attachment;

    public MetaChoke() {
        attachment = null;
        features = new LinkedList<>();
    }

    public MetaChoke(E e) {
        attachment = e;
        features = new LinkedList<>();
    }

    public void addFeature(Features f) {
        boolean contained = false;
        for (Features fi : features
                ) {
            if (fi == f)
                contained = true;
        }
        if (!contained)
            features.add(f);
    }

    public boolean hasFeature(Features f) {
        for (Features fi : features
                ) {
            if (fi == f)
                return true;
        }
        return false;
    }

    public void removeFeature(Features f) {
        features.remove(f);
    }

    public List<Features> holds() {
        return features;
    }

    public List<Features> getFeatures() {
        return features;
    }

    public void setFeatures(List<Features> features) {
        this.features = features;
    }

    public E getAttachment() {
        return attachment;
    }

    public void setAttachment(E attachment) {
        this.attachment = attachment;
    }
}
