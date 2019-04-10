package seyan.azure.carfeatureregistrationdatatier.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name= "carregistrations")
public class CarRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vinNum;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER, targetEntity=FeatureActivation.class)
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
    public void removeFeature(FeatureActivation feature){
        this.featureSet.remove(feature);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", vinNum='" + getVinNum() + "'" +
            ", featureSet='" + getFeatureSet() + "'" +
            "}";
    }

}