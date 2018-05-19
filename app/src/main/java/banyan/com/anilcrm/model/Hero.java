package banyan.com.anilcrm.model;

/**
 * Created by Jo on 12/28/2017.
 */

public class Hero {

    String id, group_id, price, name, qty;

    public Hero(String id, String group_id, String price, String name, String qty) {
        this.id = id;
        this.group_id = group_id;
        this.price = price;
        this.name = name;
        this.qty = qty;
    }

    public String getId() {
        return id;
    }
    public String getgroupId() {
        return group_id;
    }
    public String getPrice() {
        return price;
    }
    public String getName() {
        return name;
    }
    public String getQty() {
        return qty;
    }

}
