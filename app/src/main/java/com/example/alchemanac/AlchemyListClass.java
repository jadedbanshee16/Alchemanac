package com.example.alchemanac;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;

public class AlchemyListClass {

    private ArrayList<AlchemyClass> items = new ArrayList<AlchemyClass>();

    AlchemyListClass(){
        items = new ArrayList<AlchemyClass>();
    }

    //Create a new item in the list.
    public void create(AlchemyClass newItem){
        items.add(newItem);
    }

    //Replace an item in the list.
    public void edit(int ind, AlchemyClass newItem){
        items.set(ind, newItem);
    }

    //Remove an item from the list.
    public void remove(int ind){
        items.remove(ind);
        //Now, changed item id to match new list.
        for(int i = 0; i < items.size(); i++){
            items.get(i).setId(i + 1);
        }
    }

    //Find an item in the list using the id.
    public int find(int id){
        for(int i = 0; i < items.size(); i++){
            if(items.get(i).getId() == id){
                return i;
            }
        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setList(Context context) throws IOException {
        //Starting string.
        String str = "";
        //Read the file.
        File f = new File(context.getFilesDir(), "AlchemyIngredients.txt");

        //If the file doesn't exist, then create a new one and add stuff from assets folder.
        if(!f.exists()){
            String str1 = "";
            //Load the assets file into the new file.
            InputStream f1 = context.getAssets().open("AlchemyIngredients.txt");
            BufferedReader r1 = new BufferedReader(new InputStreamReader(f1));
            //Read each line into the internal database
            while((str1 = r1.readLine()) != null){
                try (OutputStreamWriter wr = new OutputStreamWriter(context.openFileOutput("AlchemyIngredients.txt", Context.MODE_APPEND))) {
                    wr.write(str1 + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            r1.close();
        }

        //Create a new reader.
        BufferedReader r = new BufferedReader(new InputStreamReader(Files.newInputStream(f.toPath())));

        //Clear the items.
        items = new ArrayList<AlchemyClass>();
        //Read every line.
        while((str = r.readLine()) != null){
            //Seperate for information.
            String[] stringArr = str.split(":");

            //Get all the properties linked to the item.
            String[] propArr = stringArr[6].split(",");
            String[] specArr = stringArr[8].split(",");

            AlchemyProperties[] newPropArr = new AlchemyProperties[propArr.length];
            for(int i = 0; i < newPropArr.length; i++){
                newPropArr[i] = AlchemyProperties.valueOf(propArr[i]);
            }

            //Create the items.
            create(new AlchemyClass(Integer.parseInt(stringArr[0]), stringArr[1], stringArr[2],
                    stringArr[3], stringArr[4],
                    AlchemyLocation.valueOf(stringArr[5]), newPropArr, stringArr[7], specArr));
        }
        r.close();
    }


    public AlchemyClass getItemInList(int ind){
        return items.get(ind);
    }

    public int getItemsSize(){
        return items.size();
    }
}
