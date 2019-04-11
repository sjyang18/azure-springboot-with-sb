package seyan.azure.carfeatureregistrationbislogictier.datamodel;

import java.util.List;

public class FeatureDeleteRequest {
    
    public enum DeleteMode {
        all,
        selective
    };
    
    private DeleteMode deleteMode;
    private List<FeatureActivation> featuresToDelete;

    public DeleteMode getDeleteMode() {
        return this.deleteMode;
    }

    public void setDeleteMode(DeleteMode deleteMode) {
        this.deleteMode = deleteMode;
    }

    public List<FeatureActivation> getFeaturesToDelete() {
        return this.featuresToDelete;
    }

    public void setFeaturesToDelete(List<FeatureActivation> featuresToDelete) {
        this.featuresToDelete = featuresToDelete;
    }


}