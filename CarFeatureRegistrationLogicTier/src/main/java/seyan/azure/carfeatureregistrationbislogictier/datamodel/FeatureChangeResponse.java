package seyan.azure.carfeatureregistrationbislogictier.datamodel;

import java.util.List;

public class FeatureChangeResponse {
    
    public enum ChangeType {
        extend,
        overwrite,
        delete_selective,
        delete_all
    };
    private String vinNum;
    private ChangeType requestType;
    private List<String> featureSet;

    public String getVinNum() {
        return this.vinNum;
    }

    public void setVinNum(String vinNum) {
        this.vinNum = vinNum;
    }

    public ChangeType getUpdateType() {
        return this.requestType;
    }

    public void setUpdateType(ChangeType requestType) {
        this.requestType = requestType;
    }

    public List<String> getFeatureSet() {
        return this.featureSet;
    }

    public void setFeatureSet(List<String> featureSet) {
        this.featureSet = featureSet;
    }

}