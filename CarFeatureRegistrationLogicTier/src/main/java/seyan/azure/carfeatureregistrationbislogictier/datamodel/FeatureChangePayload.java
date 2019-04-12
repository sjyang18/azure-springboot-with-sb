package seyan.azure.carfeatureregistrationbislogictier.datamodel;

import java.util.ArrayList;
import java.util.List;

public class FeatureChangePayload {
    
    public enum ChangeType {
        extend,
        overwrite,
        delete_selective,
        delete_all
    };
    private String vinNum;
    private ChangeType changeType;
    private List<String> featureSet;

    public String getVinNum() {
        return this.vinNum;
    }

    public void setVinNum(String vinNum) {
        this.vinNum = vinNum;
    }

    public ChangeType getChangeType() {
        return this.changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public List<String> getFeatureSet() {
        return this.featureSet;
    }

    public void setFeatureSet(List<String> featureSet) {
        this.featureSet = featureSet;
    }
    
    public List<FeatureActivation> composeInternalFeatureSetFormat() {
        List<FeatureActivation> lst = new ArrayList<>();

        for(String fname: featureSet){
            FeatureActivation fa = new FeatureActivation();
            fa.setFeature_name(fname);
            lst.add(fa);
        }
        return lst;
    }

}