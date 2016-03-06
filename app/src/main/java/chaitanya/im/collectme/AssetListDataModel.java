package chaitanya.im.collectme;

public class AssetListDataModel {

    String itemName;
    String id;
    String extraInfo;
    String category;
    double latitude;
    double longitude;

    public AssetListDataModel() {}

    public AssetListDataModel(String itemName, String extraInfo, String category, String id,
                              double latitude, double longitude) {
        this.itemName = itemName;
        this.id = id;
        this.category = category;
        this.extraInfo = extraInfo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getItemName() { return itemName; }

    public String getId() { return id; }

    public String getCategory() { return category; }

    public String getExtraInfo() { return extraInfo; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    @Override
    public String toString() {
        return "Asset{itemName='"+itemName
            +"', extraInfo='"+extraInfo
            +"', category='"+category
            +"', ID='"+id
            +"', latitude='"+latitude
            +"', longitude='"+longitude;
    }
}
