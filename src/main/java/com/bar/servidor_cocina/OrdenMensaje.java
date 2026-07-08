package com.bar.servidor_cocina;

import java.util.List;

import com.ItemMensaje;

public class OrdenMensaje {
    private Integer mesa;
    private String platillo;
    private List<ItemMensaje> items;

    public Integer getMesa() { return mesa; }
    public void setMesa(Integer mesa) { this.mesa = mesa; }
    
    public String getPlatillo() { return platillo; }
    public void setPlatillo(String platillo) { this.platillo = platillo; }
    
    public List<ItemMensaje> getItems() { return items; }
    public void setItems(List<ItemMensaje> items) { this.items = items; }
}