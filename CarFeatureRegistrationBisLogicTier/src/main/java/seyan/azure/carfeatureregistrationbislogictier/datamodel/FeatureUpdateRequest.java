package seyan.azure.carfeatureregistrationbislogictier.datamodel;

import java.util.List;

public class FeatureUpdateRequest {
    
    public enum UpdateType {
        append,
        overwrite
    };
    
    private UpdateType updateType;
    private List<FeatureActivation> featureSet;

    public UpdateType getUpdateType() {
        return this.updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public List<FeatureActivation> getFeatureSet() {
        return this.featureSet;
    }

    public void setFeatureSet(List<FeatureActivation> featureSet) {
        this.featureSet = featureSet;
    }

}