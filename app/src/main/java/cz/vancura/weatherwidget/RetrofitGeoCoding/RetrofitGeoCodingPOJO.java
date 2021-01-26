package cz.vancura.weatherwidget.RetrofitGeoCoding;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

// Class structure based on JSON returned from API

public class RetrofitGeoCodingPOJO {

    // JSON level 1

    @SerializedName("plus_code")
    @Expose
    public PlusCode plusCode;

    @SerializedName("results")
    @Expose
    public List<Result> results = null;

    @SerializedName("status")
    @Expose
    public String status;

    // JSON level 2

    public class PlusCode {

        @SerializedName("compound_code")
        @Expose
        public String compoundCode;

        @SerializedName("global_code")
        @Expose
        public String globalCode;

    }

    public class Result {

        @SerializedName("address_components")
        @Expose
        public List<AddressComponent> addressComponents = null;

        @SerializedName("formatted_address")
        @Expose
        public String formattedAddress;
        @SerializedName("geometry")

        @Expose
        public Geometry geometry;
        @SerializedName("place_id")

        @Expose
        public String placeId;
        @SerializedName("plus_code")

        @Expose
        public PlusCode plusCode;

        @SerializedName("types")
        @Expose
        public List<String> types = null;

    }

    // JSON level 3

    public class AddressComponent {

        @SerializedName("long_name")
        @Expose
        public String longName;

        @SerializedName("short_name")
        @Expose
        public String shortName;

        @SerializedName("types")
        @Expose
        public List<String> types = null;

    }

    public class Geometry {

        @SerializedName("location")
        @Expose
        public Location location;

        @SerializedName("location_type")
        @Expose
        public String locationType;

        @SerializedName("viewport")
        @Expose
        public Viewport viewport;

        @SerializedName("bounds")
        @Expose
        public Bounds bounds;

    }


    // JSON level 4

    public class Location {

        @SerializedName("lat")
        @Expose
        public Double lat;

        @SerializedName("lng")
        @Expose
        public Double lng;

    }

    public class Viewport {

        @SerializedName("northeast")
        @Expose
        public Northeast northeast;

        @SerializedName("southwest")
        @Expose
        public Southwest southwest;

    }

    public class Bounds {

        @SerializedName("northeast")
        @Expose
        public Northeast northeast;

        @SerializedName("southwest")
        @Expose
        public Southwest southwest;

    }

    // JSON level 5

    public class Northeast {

        @SerializedName("lat")
        @Expose
        public Double lat;

        @SerializedName("lng")
        @Expose
        public Double lng;

    }


    public class Southwest {

        @SerializedName("lat")
        @Expose
        public Double lat;

        @SerializedName("lng")
        @Expose
        public Double lng;

    }

}
