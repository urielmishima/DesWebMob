/**
 * Uriel Henrique Marques Farias Mishima RA: 81717300
 */

package br.usjt.ciclodevidagpsemapas;

import java.io.Serializable;

public class Localizacao implements Serializable {

    private int id;
    private Double latitude;
    private Double longitude;

    public Localizacao(double latitude, double longitude){
        this.latitude = longitude;
        this.longitude = longitude;
    }

    public Localizacao(int id, double latitude, double longitude) {
        this(latitude, longitude);
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Lat: " + latitude + ", Long: " + longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
