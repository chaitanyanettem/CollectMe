package chaitanya.im.collectme.DataModel;

public class AssetListDataModel {

    public String itemName;
    public String id;
    public String extraInfo;
    public String category;
    public String dateCreated;
    public double latitude;
    public double longitude;

    public AssetListDataModel() {}

    public AssetListDataModel(String itemName, String extraInfo, String category, String id, String dateCreated,
                              double latitude, double longitude) {
        this.itemName = itemName;
        this.id = id;
        this.category = category;
        this.extraInfo = extraInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateCreated = dateCreated;
    }

    public String getItemName() { return itemName; }

    public String getId() { return id; }

    public String getCategory() { return category; }

    public String getExtraInfo() { return extraInfo; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getDateCreated() {return dateCreated;}

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
