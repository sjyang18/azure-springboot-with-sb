package seyan.azure.carfeatureregistrationbislogictier.datamodel;

import java.util.ArrayList;
import java.util.List;

public class CarRegistrationResponse {

    private String vinNum;
    private List<String> featureSet = new ArrayList<>();

    public String getVinNum() {
        return this.vinNum;
    }

    public void setVinNum(String vinNum) {
        this.vinNum = vinNum;
    }

    public List<String> getFeatureSet() {
        return this.featureSet;
    }

    public void setFeatureSet(List<String> featureSet) {
        this.featureSet = featureSet;
    }

    public void setFeatureSetFromFeatureActivations(List<FeatureActivation> featureSet) {
        this.featureSet = new ArrayList<>();

        for(FeatureActivation fa: featureSet){
            this.featureSet.add(fa.getFeature_name());
        }
    }
}