package com.example.alchemanac;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class AlchemyClass {
    int id;
    String name;
    AlchemyTypes type;
    AlchemyRegion region;
    AlchemyRarity rarity;
    AlchemyLocation location;
    AlchemyProperties[] propertiesList;
    String description;
    String[] specialProperties;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AlchemyClass(int i, String s, String t, String r, String ra, AlchemyLocation l, AlchemyProperties[] p, String des, String[] spec){
        id = i;
        name = s;
        type = AlchemyTypes.valueOf(t);
        region = AlchemyRegion.valueOf(r);
        rarity = AlchemyRarity.valueOf(ra);;
        location = l;

        propertiesList = new AlchemyProperties[p.length];
        System.arraycopy(p, 0, propertiesList, 0, p.length);

        description = des;

        specialProperties = new String[spec.length];
        System.arraycopy(spec, 0, specialProperties, 0, spec.length);
    }

    public int getId() { return id; }
    public void setId(int ind){ id = ind; }
    public String getName(){
        return name;
    }
    public String getType() { return type.name(); }
    public String getRarity() { return rarity.name(); }
    public String getLocationFull() { return region.name() + ", " + location.name();}
    public String getRegion(){ return region.name(); }
    public String getLocation() { return location.name(); }
    public String getProperties() {
        //Convert properties into a string list.
        String[] p = new String[propertiesList.length];
        for(int i = 0; i < propertiesList.length; i++){
            p[i] = propertiesList[i].name();
        }
        String[] p2 = new String[specialProperties.length];
        for(int i = 0; i < p2.length; i++){
            p2[i] = specialProperties[i];
        }

        return String.join(", ", p) + ", " + String.join(", ", p2);
    }

    public String getProperty(int ind){ return propertiesList[ind].name(); }

    public String getProp() {
        String[] p = new String[propertiesList.length];
        for(int i = 0; i < propertiesList.length; i++){
            p[i] = propertiesList[i].name();
        }

        return String.join(",", p);
    }
    public int getPropertySize() { return propertiesList.length; }

    public String getDescription() { return description; }

    public String getSpecials() {
        String[] p2 = new String[specialProperties.length];
        for(int i = 0; i < p2.length; i++){
            p2[i] = specialProperties[i];
        }

        return String.join(",", p2);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getCol(){
        return rarity.col;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getIcon(){
        return type.img;
    }

    public String convetToString(){
        String str = "";
        StringBuffer sb = new StringBuffer();

        //Add everything above into a one line string.
        sb.append(getId());
        sb.append(":");
        if(getName().equals("")){
            name = "Unknown";
        }
        sb.append(getName());
        sb.append(":");
        sb.append(getType());
        sb.append(":");
        sb.append(getRegion());
        sb.append(":");
        sb.append(getRarity());
        sb.append(":");
        sb.append(getLocation());
        sb.append(":");
        if(getProp().equals("")){
            propertiesList = new AlchemyProperties[]{AlchemyProperties.None};
        }
        sb.append(getProp());
        sb.append(":");
        if(getDescription().equals("")){
            description = "None";
        }
        sb.append(getDescription());
        sb.append(":");
        if(getSpecials().equals("")){
            specialProperties = new String[]{"None"};
        }
        sb.append(getSpecials());

        return sb.toString();
    }


}
