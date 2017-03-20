package org.taitasciore.android.fmtest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roberto on 17/03/17.
 */

public class ApiResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        @SerializedName("metro")
        @Expose
        private List<Metro> metro = null;

        public List<Metro> getMetro() {
            return metro;
        }

        public void setMetro(List<Metro> metro) {
            this.metro = metro;
        }
    }

    public static class Metro {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("line")
        @Expose
        private String line;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("accessibility")
        @Expose
        private String accessibility;
        @SerializedName("zone")
        @Expose
        private String zone;
        @SerializedName("connections")
        @Expose
        private String connections;
        @SerializedName("lat")
        @Expose
        private double lat;
        @SerializedName("lon")
        @Expose
        private double lon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAccessibility() {
            return accessibility;
        }

        public void setAccessibility(String accessibility) {
            this.accessibility = accessibility;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public String getConnections() {
            return connections;
        }

        public void setConnections(String connections) {
            this.connections = connections;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }
}
