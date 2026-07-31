package com.relieftrack.model;

public class Inventory {

    private int inventoryId;
    private Warehouse warehouse;
    private ReliefItem reliefItem;
    private int quantity;
    private int minimumStock;

    public Inventory() {
    }

    public Inventory(int inventoryId,
                     Warehouse warehouse,
                     ReliefItem reliefItem,
                     int quantity,
                     int minimumStock) {

        this.inventoryId = inventoryId;
        this.warehouse = warehouse;
        this.reliefItem = reliefItem;
        this.quantity = quantity;
        this.minimumStock = minimumStock;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public ReliefItem getReliefItem() {
        return reliefItem;
    }

    public void setReliefItem(ReliefItem reliefItem) {
        this.reliefItem = reliefItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryId=" + inventoryId +
                ", warehouse=" + warehouse.getName() +
                ", reliefItem=" + reliefItem.getName() +
                ", quantity=" + quantity +
                ", minimumStock=" + minimumStock +
                '}';
    }
}