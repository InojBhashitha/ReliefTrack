package com.relieftrack.model;

public class Warehouse {

    private int warehouseId;
    private String name;
    private String district;
    private String address;

    public Warehouse() {
    }

    public Warehouse(int warehouseId, String name, String district, String address) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.district = district;
        this.address = address;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseId=" + warehouseId +
                ", name='" + name + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}