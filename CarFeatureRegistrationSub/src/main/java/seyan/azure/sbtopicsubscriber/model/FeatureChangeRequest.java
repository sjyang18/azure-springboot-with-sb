package seyan.azure.sbtopicsubscriber.model;

import java.util.List;

public class FeatureChangeRequest {
    
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

}