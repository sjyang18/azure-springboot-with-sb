package seyan.azure.sbtopicpublisher.model;

import java.util.List;

public class CarRegistrationRequest {
    private String vinNum;
    private List<String> featureSet;

    /**
     * @return the vinNum
     */
    public String getVinNum() {
        return vinNum;
    }
    /**
     * @param vinNum the vinNum to set
     */
    public void setVinNum(String vinNum) {
        this.vinNum = vinNum;
    }

    /**
     * @return the features
     */
    public List<String> getFeatureSet() {
        return featureSet;
    }

    /**
     * @param features the features to set
     */
    public void setFeatureSet(List<String> features) {
        this.featureSet = features;
    }



}