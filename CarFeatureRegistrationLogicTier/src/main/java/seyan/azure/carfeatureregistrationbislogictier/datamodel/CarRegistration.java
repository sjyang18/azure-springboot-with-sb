package seyan.azure.carfeatureregistrationbislogictier.datamodel;

import java.util.ArrayList;
import java.util.List;

public class CarRegistration {
    private Long id;
    private String vinNum;
    private List<FeatureActivation> featureSet = new ArrayList<>();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getVinNum() {
        return this.vinNum;
    }

    public void setVinNum(String vinNum) {
        this.vinNum = vinNum;
    }

    public List<FeatureActivation> getFeatureSet() {
        return this.featureSet;
    }

    public void setFeatureSet(List<FeatureActivation> featureSet) {
        this.featureSet = featureSet;
    }

    public void addFeature(FeatureActivation feature){
        this.featureSet.add(feature);
    }
    public void addFeature(String feature_name){
        FeatureActivation fa = new FeatureActivation();
        fa.setFeature_name(feature_name);
        this.addFeature(fa);
    }

    public void removeFeature(FeatureActivation feature){
        this.featureSet.remove(feature);
    }
    public void removeFeature(String feature_name){
        FeatureActivation feature2Remove = null;
        for(FeatureActivation fa: this.featureSet){
            if(fa.getFeature_name().equals(feature_name)){
                feature2Remove  =fa;
                break;
            }
        }
        if(feature2Remove != null) {
            removeFeature(feature2Remove);
        }
    }

    public Boolean hasFeature(String feature_name){
        Boolean search = false;
        for(FeatureActivation fa: this.featureSet){
            if(fa.getFeature_name().equals(feature_name)){
                search = true;
                break;
            }
        }

        return search;
    } 

    @Override
    public String toString() {
        return "{" +
            ", vinNum='" + getVinNum() + "'" +
            ", featureSet='" + getFeatureSet() + "'" +
            "}";
    }

}