package ca.projectpc.projectpc.ui;

/**
 * Created by Rameet on 12/4/2017.
 */

public class Item {
    private int id;
    private String title;
    private String date;
    private double distance;
    private double price;
    private int image;

    public Item(int id, String title, String date, double distance, double price, int image) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.distance = distance;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public double getDistance() {
        return distance;
    }

    public double getPrice() {
        return price;
    }

    public int getImage() {
        return image;
    }
}
