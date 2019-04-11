package seyan.azure.carfeatureregistrationbislogictier.datamodel;

import java.util.ArrayList;
import java.util.List;

public class FeatureUpdateRequest {
    
    public enum UpdateType {
        extend,
        overwrite
    };
    
    private UpdateType updateType;
    private List<String> featureSet;

    public UpdateType getUpdateType() {
        return this.updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public List<String> getFeatureSet() {
        return this.featureSet;
    }

    public void setFeatureSet(List<String> featureSet) {
        this.featureSet = featureSet;
    }
    
    public List<FeatureActivation> composeActivations() {
        List<FeatureActivation> lst = new ArrayList<>();

        for(String fname: featureSet){
            FeatureActivation fa = new FeatureActivation();
            fa.setFeature_name(fname);
            lst.add(fa);
        }
        return lst;
    }
}