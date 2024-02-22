package com.example.alchemanac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {
    class propertyArray{
        String name;
        boolean used;

        propertyArray(String n, boolean b){
            name = n;
            used = b;
        }

        public String getName(){
            return name;
        }
        public boolean getIsUsed(){
            return used;
        }
        public void setIsUsed(boolean b){
            used = b;
        }
    }
    private ArrayList<AlchemyClass> itemListView = new ArrayList<AlchemyClass>();

    private AlchemyListClass items = new AlchemyListClass();

    private myListAdapter gridviewAdapter = null;
    TextView tex;

    private enum orderTypes{
        name,
        type,
        rarity
    }

    orderTypes currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fulllayout);
        //tex = findViewById(R.id.textView2);
        //Get the main list.
        GridView theView = (GridView) findViewById(R.id.items_list);
        SearchView searchBar = (SearchView) findViewById(R.id.item_search_bar);
        currentOrder = orderTypes.name;

        //Create the items list, then create the list view.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                items.setList(this);
                setListView("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //generateListContent(100);

        //Set the new button.
        ImageButton addBtn = (ImageButton) findViewById(R.id.create_btn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    onButtonShowFormWindowClick(0, true);
                } ;
            }
        });

        //Create the name sort button.
        Button nameBtn = (Button) findViewById(R.id.item_sort_name);
        Button typeBtn = (Button) findViewById(R.id.item_sort_type);
        Button rarityBtn = (Button) findViewById(R.id.item_sort_rarity);

        nameBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentOrder = orderTypes.name;
                    reorderList(currentOrder);
                } ;
            }
        });
        typeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentOrder = orderTypes.type;
                    reorderList(currentOrder);
                } ;
            }
        });
        rarityBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    currentOrder = orderTypes.rarity;
                    reorderList(currentOrder);
                } ;
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setListView(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setListView(newText);
                return false;
            }
        });

    }

    public void onButtonShowPopupWindowClick(int index) throws IOException {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewitem, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(itemView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(itemView, Gravity.CENTER, 0, 0);

        setUpView(itemView, index);

        //Make the 'X' close the window.
        itemView.findViewById(R.id.item_close_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        itemView.findViewById(R.id.item_edit_btn).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                onButtonShowFormWindowClick(index, false);
                popupWindow.dismiss();
            }
        });

        itemView.findViewById(R.id.item_delete_btn).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                onButtonShowConfirmWindowClick(index, popupWindow);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onButtonShowFormWindowClick(int pos, boolean isNew) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.add_alchemy_form, null);

        // create the popup window
        final PopupWindow popupWindow = new PopupWindow(itemView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        // show the popup window
        popupWindow.showAtLocation(itemView, Gravity.CENTER, 0, 0);

        //Now, populate the current spinners.
        Spinner typeSpinner = (Spinner) itemView.findViewById(R.id.dropdown_type);
        typeSpinner.setAdapter(new ArrayAdapter<AlchemyTypes>(this, android.R.layout.simple_spinner_item, AlchemyTypes.values()));
        Spinner raritySpinner = (Spinner) itemView.findViewById(R.id.dropdown_rarity);
        raritySpinner.setAdapter(new ArrayAdapter<AlchemyRarity>(this, android.R.layout.simple_spinner_item, AlchemyRarity.values()));
        Spinner regionSpinner = (Spinner) itemView.findViewById(R.id.dropdown_region);
        regionSpinner.setAdapter(new ArrayAdapter<AlchemyRegion>(this, android.R.layout.simple_spinner_item, AlchemyRegion.values()));
        Spinner locationSpinner = (Spinner) itemView.findViewById(R.id.dropdown_location);
        locationSpinner.setAdapter(new ArrayAdapter<AlchemyLocation>(this, android.R.layout.simple_spinner_item, AlchemyLocation.values()));

        //Create the gridview.
        GridView propertyGrid = (GridView) itemView.findViewById(R.id.propertyGrid);

        //Populate the property array with whether it was used.

        ArrayList<propertyArray> propArr = new ArrayList<propertyArray>();
        for(int i = 0; i < AlchemyProperties.values().length; i++){
            propArr.add(new propertyArray(AlchemyProperties.values()[i].toString(), false));
        }

        //Now, if position is valid (0 or over, then add that stuff to the set.
        if(!isNew){
            typeSpinner.setSelection(AlchemyTypes.valueOf(itemListView.get(pos).getType()).ordinal());
            raritySpinner.setSelection(AlchemyRarity.valueOf(itemListView.get(pos).getRarity()).ordinal());
            regionSpinner.setSelection(AlchemyRegion.valueOf(itemListView.get(pos).getRegion()).ordinal());
            locationSpinner.setSelection(AlchemyLocation.valueOf(itemListView.get(pos).getLocation()).ordinal());

            //Get the property list.
            String[] currentPropArr = new String[itemListView.get(pos).getPropertySize()];
            for(int i = 0; i < currentPropArr.length; i++){
                currentPropArr[i] = itemListView.get(pos).getProperty(i);
            }

            //Now make changes to propArr based on the new list.
            for(int i = 0; i < propArr.size(); i++){
                for(int v = 0; v < currentPropArr.length; v++){
                    if(propArr.get(i).getName().equals(currentPropArr[v])){
                        propArr.get(i).setIsUsed(true);
                    }
                }
            }

            //Complete text sections.
            //Name.
            EditText nam = (EditText) itemView.findViewById(R.id.form_name);
            nam.setText(itemListView.get(pos).getName());
            EditText other = (EditText) itemView.findViewById(R.id.form_special_text);
            other.setText(itemListView.get(pos).getSpecials());
            EditText desc = (EditText) itemView.findViewById(R.id.form_description_text);
            desc.setText(itemListView.get(pos).getDescription());
        }

        myPropertyAdapter propertyAdap = new myPropertyAdapter(this, R.layout.property_item, propArr);
        propertyGrid.setAdapter(propertyAdap);
        propertyGrid.refreshDrawableState();


        //Make the cancel button close the window.
        itemView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //Make the submit button make changes to the items list.
        itemView.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Collect all the information in the form.
                EditText nam = (EditText) itemView.findViewById(R.id.form_name);
                Spinner ty = (Spinner) itemView.findViewById(R.id.dropdown_type);
                Spinner reg = (Spinner) itemView.findViewById(R.id.dropdown_region);
                Spinner rar = (Spinner) itemView.findViewById(R.id.dropdown_rarity);
                Spinner loc = (Spinner) itemView.findViewById(R.id.dropdown_location);

                //Create a list to keep the propBox strings.
                ArrayList<String> propList = new ArrayList<String>();

                int size = propertyGrid.getChildCount();

                for(int i = 0; i < size; i++){
                    //Get the checkbox of the grid.
                    ViewGroup group = (ViewGroup) propertyGrid.getChildAt(i);
                    for(int c = 0; c < group.getChildCount(); c++){
                        if(group.getChildAt(c) instanceof CheckBox){
                            CheckBox box = (CheckBox) group.getChildAt(c);
                            if(box.isChecked()){
                                propList.add(box.getText().toString());
                            }
                        }
                    }
                }

                //Get the 2 layouts.
                /*LinearLayout l1 = (LinearLayout) itemView.findViewById(R.id.PropboxLayout1);
                LinearLayout l2 = (LinearLayout) itemView.findViewById(R.id.PropboxLayout2);
                //Now iterate and get children in each layout.
                for(int i = 0; i < l1.getChildCount(); i++){
                    //Get the checkbox child.
                    CheckBox b = (CheckBox) l1.getChildAt(i);
                    if(b.isChecked()){
                        //Now add the name value as sting.
                        propList.add(b.getText().toString());
                    }
                }
                for(int i = 0; i < l2.getChildCount(); i++){
                    //Get the checkbox child.
                    CheckBox b = (CheckBox) l2.getChildAt(i);
                    if(b.isChecked()){
                        //Now add the name value as sting.
                        propList.add(b.getText().toString());
                    }
                }*/

                //Add all the strings together.
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < propList.size(); i++) {
                    sb.append(propList.get(i));
                    //Ensure a comma is added after ONLY if it's not the final one.
                    if(i < propList.size() - 1){
                        sb.append(",");
                    }
                }

                EditText desc = (EditText) itemView.findViewById(R.id.form_description_text);
                EditText spec = (EditText) itemView.findViewById(R.id.form_special_text);

                String[] specArr = String.valueOf(spec.getText()).split(",");
                AlchemyProperties[] realPropArr;
                if(!sb.toString().equals("")){
                    String[] propArr = sb.toString().split(",");
                    realPropArr = new AlchemyProperties[propArr.length];
                    for(int f = 0; f < realPropArr.length; f++){
                        realPropArr[f] = AlchemyProperties.valueOf(propArr[f]);
                    }
                } else {
                    realPropArr = new AlchemyProperties[1];
                    realPropArr[0] = AlchemyProperties.None;
                }

                int index = items.getItemsSize();
                if(!isNew){
                    index = items.find(itemListView.get(pos).getId());
                }

                //Create new item with the information gathered and id.
                AlchemyClass newItem = new AlchemyClass(index + 1, nam.getText().toString(),
                        ty.getSelectedItem().toString(),
                        reg.getSelectedItem().toString(),
                        rar.getSelectedItem().toString(),
                        AlchemyLocation.valueOf(loc.getSelectedItem().toString()),
                        realPropArr,
                        desc.getText().toString(),
                        specArr);

                if(isNew){
                    items.create(newItem);
                } else {
                    items.edit(index, newItem);
                }

                //Now, create new list.
                try {
                    saveList();
                    setListView("");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                popupWindow.dismiss();
            }
        });
    }

    public void onButtonShowConfirmWindowClick(int index, PopupWindow theWindow) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.confirm_view, null);

        // create the popup window
        final PopupWindow popupWindow = new PopupWindow(itemView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(itemView, Gravity.CENTER, 0, 0);


        //Make the cancel button close window.
        itemView.findViewById(R.id.confirm_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //Make the delete button delete the item.
        itemView.findViewById(R.id.confirm_delete).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                try {
                    int ind = items.find(itemListView.get(index).getId());
                    items.remove(ind);
                    saveList();
                    setListView("");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                theWindow.dismiss();
                popupWindow.dismiss();
            }
        });
    }

    //Load the current list into the list view using the sort order or query.
    private void setListView(String query) {
        //Set the list as the one in items.
        itemListView = new ArrayList<AlchemyClass>();
        for(int i = 0; i < items.getItemsSize(); i++){
            itemListView.add(items.getItemInList(i));
        }

        //Change based on given query.
        changeList(query);
        //Reorder the list.
        reorderList(currentOrder);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUpView(View v, int ind){
        //Get and set the picture.
        ImageView img = v.findViewById(R.id.item_picture);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            img.setImageDrawable(getDrawable(Objects.requireNonNull(itemListView.get(ind)).getIcon()));
        }

        //Get and set the name.
        TextView name = v.findViewById(R.id.item_title);
        name.setText(Objects.requireNonNull(itemListView.get(ind)).getName());

        //Get and set rarity / type.
        TextView type = v.findViewById(R.id.item_type_description);
        TextView rarity = v.findViewById(R.id.item_rarity_description);
        LinearLayout backCol = v.findViewById(R.id.item_view_background);
        type.setText(Objects.requireNonNull(itemListView.get(ind)).getType());
        rarity.setText(Objects.requireNonNull(itemListView.get(ind)).getRarity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Color newCol = Color.valueOf(itemListView.get(ind).getCol());
            int curCol = Color.rgb(newCol.red(), newCol.green(), newCol.blue());
            backCol.setBackgroundColor(curCol);
        }

        //Get the location and region and link them for full location description.
        TextView loc = v.findViewById(R.id.item_location_description);
        loc.setText(Objects.requireNonNull(itemListView.get(ind)).getLocationFull());

        //Get the properties list, including special list.
        TextView prop = v.findViewById(R.id.item_properties_description);
        prop.setText(Objects.requireNonNull(itemListView.get(ind)).getProperties());

        //Get and set description.
        TextView desc = v.findViewById(R.id.item_description);
        desc.setText(Objects.requireNonNull(itemListView.get(ind)).getDescription());
    }

    private void setTheListView(GridView v){
        v.setAdapter(null);
        gridviewAdapter = new myListAdapter(this, R.layout.listitem, itemListView);
        //Now create the new points.
        v.setAdapter(gridviewAdapter);
    }

    //Reorder the list to different variants.
    private void reorderList(orderTypes theOrder){
        //Reorder by name in alphabet
        if(theOrder == orderTypes.name){
            Collections.sort(itemListView, new Comparator<AlchemyClass>() {
                @Override
                public int compare(AlchemyClass c1, AlchemyClass c2) {
                    return c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase());
                }
            });
        }

        //Reorder by type in enum
        if(theOrder == orderTypes.type){
            Collections.sort(itemListView, new Comparator<AlchemyClass>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public int compare(AlchemyClass c1, AlchemyClass c2) {
                    return AlchemyTypes.valueOf(c1.getType()).compareTo(AlchemyTypes.valueOf(c2.getType()));
                }
            });
        }

        //Reorder by rarity in enum
        if(theOrder == orderTypes.rarity){
            Collections.sort(itemListView, new Comparator<AlchemyClass>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public int compare(AlchemyClass c1, AlchemyClass c2) {
                    return AlchemyRarity.valueOf(c1.getRarity()).compareTo(AlchemyRarity.valueOf(c2.getRarity()));
                }
            });
        }

        setTheListView(findViewById(R.id.items_list));
    }


    private void changeList(String query) {
        //Start new list.
        //getNewList();
        ArrayList<AlchemyClass> newList = new ArrayList<AlchemyClass>();

        //Now, add to new list if current list properties contains query.
        for(int i = 0; i < itemListView.size(); i++){
            if(itemListView.get(i).getProperties().toLowerCase().contains(query.toLowerCase())){
                newList.add(itemListView.get(i));
            }
        }

        //Now make items the new list.
        itemListView = new ArrayList<AlchemyClass>();
        itemListView.addAll(newList);

        //setTheList(findViewById(R.id.items_list));
    }

    private void saveList() throws IOException {
        //First, clear the list.
        try (OutputStreamWriter wr = new OutputStreamWriter(this.openFileOutput("AlchemyIngredients.txt", Context.MODE_PRIVATE))) {
            for(int i = 0; i < items.getItemsSize(); i++){
                wr.write(items.getItemInList(i).convetToString() + "\n");
            }
            wr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isInList(String s, String[] properties){
        for(int i = 0; i < properties.length; i++){
            if(s.equals(properties[i])){
                return true;
            }
        }
        return false;
    }

    //Generate the list adapter (Which extends a string list as a premade list.
    private class myListAdapter extends ArrayAdapter<AlchemyClass> {
        //Create a layout object.
        private int layout;
        public myListAdapter(@NonNull Context context, int resource, @NonNull List<AlchemyClass> objects) {
            super(context, resource, objects);
            //Save the resource number to the layout num.
            layout = resource;
        }

        //Create the 'get' method using getview.
        @SuppressLint("UseCompatLoadingForDrawables")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            viewHolder mainView = null;
            if(convertView == null){
                //Create an inflator (essentially it creates a layout of the view as an instance)
                LayoutInflater inflator = LayoutInflater.from(getContext());
                convertView = inflator.inflate(layout, parent, false);

                //Create, set and return the item views.
                viewHolder view = new viewHolder();
                view.button = (Button) convertView.findViewById(R.id.item_btn);
                view.tex = (TextView) convertView.findViewById(R.id.item_name);
                view.img = (ImageView) convertView.findViewById(R.id.item_img);
                view.img.setImageDrawable(getDrawable(Objects.requireNonNull(getItem(position)).getIcon()));
                view.button.setBackgroundColor(Objects.requireNonNull(getItem(position)).getCol());
                view.tex.setText(Objects.requireNonNull(getItem(position)).getName());
                //set listener.
                view.button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        //changeName(view.button.getText().toString());
                        try {
                            onButtonShowPopupWindowClick(position);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                convertView.setTag(view);
            } else {
                mainView = (viewHolder) convertView.getTag();
                //Set name of the mainView.
                mainView.button.setBackgroundColor(Objects.requireNonNull(getItem(position)).getCol());
                mainView.tex.setText(Objects.requireNonNull(getItem(position)).getName());
                mainView.img.setImageDrawable(getDrawable(Objects.requireNonNull(getItem(position)).getIcon()));
            }

            return convertView;
        }
    }

    //Create a constructor class to hold the provided button.\
    public class viewHolder {
        Button button;
        ImageView img;
        TextView tex;
    }

    //Generate the list adapter (Which extends a string list as a premade list.
    private class myPropertyAdapter extends ArrayAdapter<propertyArray> {
        //Create a layout object.
        private int layout;
        private String[] itemChecked;
        public myPropertyAdapter(@NonNull Context context, int resource, @NonNull List<propertyArray> objects) {
            super(context, resource, objects);
            //Save the resource number to the layout num.
            layout = resource;
        }

        //Create the 'get' method using getview.
        @SuppressLint("UseCompatLoadingForDrawables")
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            viewHolderPropertiesGrid mainView = null;
            if(convertView == null){
                //Create an inflator (essentially it creates a layout of the view as an instance)
                LayoutInflater inflator = LayoutInflater.from(getContext());
                convertView = inflator.inflate(layout, parent, false);

                //Create, set and return the item views.
                viewHolderPropertiesGrid view = new viewHolderPropertiesGrid();
                view.checkBox = (CheckBox) convertView.findViewById(R.id.property_checkbox);
                view.checkBox.setText(getItem(position).getName());
                view.checkBox.setChecked(getItem(position).getIsUsed());

                convertView.setTag(view);
            } else {
                mainView = (viewHolderPropertiesGrid) convertView.getTag();
                //Set name of the mainView.
                mainView.checkBox.setText(getItem(position).getName());
                mainView.checkBox.setChecked(getItem(position).getIsUsed());
            }

            return convertView;
        }
    }

    //Create a constructor class to hold the provided button.\
    public class viewHolderPropertiesGrid {
        CheckBox checkBox;
    }
}