package seyan.azure.carfeatureregistrationdatatier.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name= "featureactivations")
public class FeatureActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="feature_name")
    private String feature_name; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="carregistrations_id")
    private CarRegistration carRegId;

	public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeature_name() {
        return this.feature_name;
    }

    public void setFeature_name(String feature_name) {
        this.feature_name = feature_name;
    }


}