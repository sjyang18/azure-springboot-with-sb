package seyan.azure.carfeatureregistrationbislogictier.datamodel;

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

    public CarRegistration composeCarRegistrationRecord()
    {
        CarRegistration cr = new CarRegistration();
        cr.setVinNum(this.vinNum);
        List<FeatureActivation> lst = new ArrayList<>();
        for(String fname: this.featureSet){
            FeatureActivation fa = new FeatureActivation();
            fa.setFeature_name(fname);
            lst.add(fa);
        }
        cr.setFeatureSet(lst);
        return cr;
    }
    
    public void setFeatureSetFromFeatureActivations(List<FeatureActivation> featureSet) {
        this.featureSet = new ArrayList<>();

        for(FeatureActivation fa: featureSet){
            this.featureSet.add(fa.getFeature_name());
        }
    }

}