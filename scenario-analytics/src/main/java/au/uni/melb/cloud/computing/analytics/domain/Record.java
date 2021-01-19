package au.uni.melb.cloud.computing.analytics.domain;

import java.util.List;

public class Record {
    private List<Coord> coordinates;
    private Stat properties;

    public List<Coord> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Coord> coordinates) {
        this.coordinates = coordinates;
    }

    public Stat getProperties() {
        return properties;
    }

    public void setProperties(Stat properties) {
        this.properties = properties;
    }

    public String getFeatureName() {
        return this.properties.getFeatureName();
    }

    public boolean inside(double lat, double lon) {
        boolean result = false;
        for (int i = 0, j = coordinates.size() - 1; i < coordinates.size(); j = i++) {
            if ((coordinates.get(i).getLon() > lon) != (coordinates.get(j).getLon() > lon)
                    && (lat < (coordinates.get(j).getLat() - coordinates.get(i).getLat())
                    * (lon - coordinates.get(i).getLon())
                    / (coordinates.get(j).getLon() - coordinates.get(i).getLon())
                    + coordinates.get(i).getLat())) {
                result = !result;
            }
        }
        return result;
    }
}
