/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;

/**
 *models the properties, whether it is for nodes or edges.
 * @author amina
 */
public class Property {
    private String feature;
    private String value;

    public Property() {
    }
    
    public Property(String feature) {
        this.feature = feature;
    }


    public Property(String feature, String value) {
        this.feature = feature;
        this.value = value;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }


    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getFeature() {
        return feature;
    }
    
}
