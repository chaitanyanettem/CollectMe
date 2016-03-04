package chaitanya.im.collectme;

public class AssetListDataModel {

    String itemName;
    String id;
    String extraInfo;
    String category;

    public AssetListDataModel() {}

    public AssetListDataModel(String itemName, String extraInfo, String category, String id) {
        this.itemName = itemName;
        this.id = id;
        this.category = category;
        this.extraInfo = extraInfo;
    }

    public String getItemName() { return itemName; }

    public String getId() { return id; }

    public String getCategory() { return category; }

    public String getExtraInfo() { return extraInfo; }

    @Override
    public String toString() { return "Asset{itemName='"+itemName+"', extraInfo='"+extraInfo+"'}";}
}
