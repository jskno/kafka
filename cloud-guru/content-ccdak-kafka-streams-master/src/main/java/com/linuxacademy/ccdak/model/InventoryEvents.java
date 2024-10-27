package com.linuxacademy.ccdak.model;

import java.util.ArrayList;
import java.util.List;

public class InventoryEvents {

    List<Inventory> inventoryList = new ArrayList<>();

    public InventoryEvents() {
    }

    public List<Inventory> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public InventoryEvents add(Inventory inventory) {
        this.getInventoryList().add(inventory);
        return this;
    }
}
