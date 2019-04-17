package seyan.azure.customernotifier.datamodel;

import java.util.ArrayList;
import java.util.List;

public class CarRegistrationPayload {

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

    public void addFeature(String feature_name){
        if(!hasFeature(feature_name)){
            this.featureSet.add(feature_name);
        }  
    }


    public void removeFeature(String feature_name){
        if(hasFeature(feature_name))
        {
            this.featureSet.remove(feature_name);
        }
    }

    public Boolean hasFeature(String feature_name){
        Boolean search = false;
        for(String fa: this.featureSet){
            if(fa.equals(feature_name)){
                search = true;
                break;
            }
        }

        return search;
    } 

}