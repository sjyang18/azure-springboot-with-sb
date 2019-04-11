package seyan.azure.carfeatureregistrationbislogictier.datamodel;

public class FeatureActivation {

    private Long id;

    private String feature_name; 

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getFeature_name() {
        return this.feature_name;
    }

    public void setFeature_name(String fname) {
        this.feature_name = fname;
    }


}