package seyan.azure.carfeatureregistrationbislogictier.datamodel;

import java.util.ArrayList;
import java.util.List;

public class FeatureDeleteRequest {
    
    public enum DeleteMode {
        all,
        selective
    };
    
    private DeleteMode deleteMode;
    private List<String> featuresToDelete;

    public DeleteMode getDeleteMode() {
        return this.deleteMode;
    }

    public void setDeleteMode(DeleteMode deleteMode) {
        this.deleteMode = deleteMode;
    }

    public List<String> getFeaturesToDelete() {
        return this.featuresToDelete;
    }

    public void setFeaturesToDelete(List<String> featuresToDelete) {
        this.featuresToDelete = featuresToDelete;
    }

    public List<FeatureActivation> composeDeactivations() {
        List<FeatureActivation> lst = new ArrayList<>();

        for(String fname: featuresToDelete){
            FeatureActivation fa = new FeatureActivation();
            fa.setFeature_name(fname);
            lst.add(fa);
        }
        return lst;
    }




}