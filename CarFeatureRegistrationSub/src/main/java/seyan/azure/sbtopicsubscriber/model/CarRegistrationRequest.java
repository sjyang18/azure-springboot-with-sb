package seyan.azure.sbtopicsubscriber.model;

import java.util.List;

public class CarRegistrationRequest {
    private String vinNum;
    private List<String> features;

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
    public List<String> getFeatures() {
        return features;
    }

    /**
     * @param features the features to set
     */
    public void setFeatures(List<String> features) {
        this.features = features;
    }
    
    @Override
    public String toString() {
        return super.toString();
    }



}