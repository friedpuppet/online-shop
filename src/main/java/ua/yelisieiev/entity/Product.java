package ua.yelisieiev.entity;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

public class Product {
    private Id id;
    private String name;
    private double price;
    private String description;
    private Date modifyDate;

    public Product(String name, double price) {
        this(null, name, price);
    }

    public Product(Id id, String name, double price) {
        this(id, name, price, null, null);
    }

    public Product(String name, double price, String description) {
        this(null, name, price, description, null);
    }

    public Product(Id id, String name, double price, String description) {
        this(id, name, price, description, null);
    }

    public Product(String name, double price, String description, Date modifyDate) {
        this(null, name, price, description, modifyDate);
    }

    public Product(Id id, String name, double price, String description, Date modifyDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.modifyDate = modifyDate;
    }


    public void setId(Id id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Id getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Override
    public String toString() {
        return "Product{id=%s, name='%s', price=%s, description='%s', modifyDate=%s}".formatted(id, name, price, description, modifyDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 && id.equals(product.id) && name.equals(product.name) && Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, description);
    }

    public static class Id {
        private final int value;

        public Id(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id1 = (Id) o;
            return value == id1.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
