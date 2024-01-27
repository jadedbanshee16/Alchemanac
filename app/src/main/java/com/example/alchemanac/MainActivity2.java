package com.example.alchemanac;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.List;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {

    private ArrayList<AlchemyClass> items = new ArrayList<AlchemyClass>();

    private myListAdapter gridviewAdapter = null;
    TextView tex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fulllayout);
        //tex = findViewById(R.id.textView2);
        //Get the main list.
        GridView theView = (GridView) findViewById(R.id.items_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                getNewList(theView);
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
                    onButtonShowFormWindowClick();
                } ;
            }
        });
    }

    public void onButtonShowPopupWindowClick(int index) {

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

        // dismiss the popup window when touched
        //DEBUG ONLY
        /*itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onButtonShowFormWindowClick() {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.add_alchemy_form, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(itemView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
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

        //Make the 'X' close the window.
        itemView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        itemView.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText nam = (EditText) itemView.findViewById(R.id.form_name);
                Spinner ty = (Spinner) itemView.findViewById(R.id.dropdown_type);
                Spinner reg = (Spinner) itemView.findViewById(R.id.dropdown_region);
                Spinner rar = (Spinner) itemView.findViewById(R.id.dropdown_rarity);
                Spinner loc = (Spinner) itemView.findViewById(R.id.dropdown_location);

                //Create a list to keep the propBox strings.
                ArrayList<String> propList = new ArrayList<String>();
                //Get the 2 layouts.
                LinearLayout l1 = (LinearLayout) itemView.findViewById(R.id.PropboxLayout1);
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
                }

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

                try {
                    addNewIngredient(items.size() + 1,
                                    nam.getText().toString(),
                                    ty.getSelectedItem().toString(),
                                    reg.getSelectedItem().toString(),
                                    rar.getSelectedItem().toString(),
                                    loc.getSelectedItem().toString(),
                                    sb.toString(),
                                    desc.getText().toString(),
                                    spec.getText().toString());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                //Now, create new list.
                try {
                    getNewList(findViewById(R.id.items_list));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                popupWindow.dismiss();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUpView(View v, int ind){
        //Get and set the picture.
        ImageView img = v.findViewById(R.id.item_picture);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            img.setImageDrawable(getDrawable(Objects.requireNonNull(items.get(ind)).getIcon()));
        }

        //Get and set the name.
        TextView name = v.findViewById(R.id.item_title);
        name.setText(Objects.requireNonNull(items.get(ind)).getName());

        //Get and set rarity / type.
        TextView type = v.findViewById(R.id.item_type_description);
        TextView rarity = v.findViewById(R.id.item_rarity_description);
        LinearLayout backCol = v.findViewById(R.id.item_view_background);
        type.setText(Objects.requireNonNull(items.get(ind)).getType());
        rarity.setText(Objects.requireNonNull(items.get(ind)).getRarity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            backCol.setBackgroundColor(Objects.requireNonNull(items.get(ind)).getCol());
        }

        //Get the location and region and link them for full location description.
        TextView loc = v.findViewById(R.id.item_location_description);
        loc.setText(Objects.requireNonNull(items.get(ind)).getLocationFull());

        //Get the properties list, including special list.
        TextView prop = v.findViewById(R.id.item_properties_description);
        prop.setText(Objects.requireNonNull(items.get(ind)).getProperties());

        //Get and set description.
        TextView desc = v.findViewById(R.id.item_description);
        desc.setText(Objects.requireNonNull(items.get(ind)).getDescription());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNewIngredient(int ind, String name, String type, String region, String rarity, String location, String prop, String desc, String spec) throws FileNotFoundException {
        //Now, add this to the ingredients list.
        String str = "";
        StringBuffer sb = new StringBuffer();

        //Add everything above into a one line string.
        sb.append(ind);
        sb.append(":");
        if(name.equals("")){
            name = "Unknown";
        }
        sb.append(name);
        sb.append(":");
        sb.append(AlchemyTypes.valueOf(type));
        sb.append(":");
        sb.append(AlchemyRegion.valueOf(region));
        sb.append(":");
        sb.append(AlchemyRarity.valueOf(rarity));
        sb.append(":");
        sb.append(location);
        sb.append(":");
        if(prop.equals("")){
            prop = "None";
        }
        sb.append(prop);
        sb.append(":");
        if(desc.equals("")){
            desc = "None";
        }
        sb.append(desc);
        sb.append(":");
        if(spec.equals("")){
            spec = "None";
        }
        sb.append(spec);

        try (OutputStreamWriter wr = new OutputStreamWriter(this.openFileOutput("AlchemyIngredients.txt", Context.MODE_APPEND))) {
            wr.write(sb.toString() + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*private void generateListContent(int num){
        for(int i = 1; i < num; i++){
            String s = "Name " + Integer.toString(i);
            names.add(s);
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getNewList(GridView v) throws IOException {
        //Starting string.
        String str = "";
        //Read the file.
        File f = new File(this.getFilesDir(), "AlchemyIngredients.txt");

        //If the file doesn't exist, then create a new one and add stuff from assets folder.
        if(!f.exists()){
            Log.d("debug", "does not exist");
            String str1 = "";
            //Load the assets file into the new file.
            InputStream f1 = getAssets().open("AlchemyIngredients.txt");
            Log.d("debug", "file created");
            BufferedReader r1 = new BufferedReader(new InputStreamReader(f1));
            while((str1 = r1.readLine()) != null){
                Log.d("debug", "Add line: " + str1);
                try (OutputStreamWriter wr = new OutputStreamWriter(this.openFileOutput("AlchemyIngredients.txt", Context.MODE_APPEND))) {
                    wr.write(str1 + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        BufferedReader r = new BufferedReader(new InputStreamReader(Files.newInputStream(f.toPath())));

        //Now read it.
        if(r != null){
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
                items.add(new AlchemyClass(Integer.parseInt(stringArr[0]), stringArr[1], stringArr[2],
                                           stringArr[3], stringArr[4],
                                           AlchemyLocation.valueOf(stringArr[5]), newPropArr, stringArr[7], specArr));
            }
        }

        r.close();

        v.setAdapter(null);
        gridviewAdapter = new myListAdapter(this, R.layout.listitem, items);
        //Now create the new points.
        v.setAdapter(gridviewAdapter);
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
                view.img = (ImageView) convertView.findViewById(R.id.item_img);
                view.img.setImageDrawable(getDrawable(Objects.requireNonNull(getItem(position)).getIcon()));
                view.button.setBackgroundColor(Objects.requireNonNull(getItem(position)).getCol());
                view.button.setText(Objects.requireNonNull(getItem(position)).getName());
                //set listener.
                view.button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        //changeName(view.button.getText().toString());
                        onButtonShowPopupWindowClick(position);
                    }
                });
                convertView.setTag(view);
            } else {
                mainView = (viewHolder) convertView.getTag();
                //Set name of the mainView.
                mainView.button.setBackgroundColor(Objects.requireNonNull(getItem(position)).getCol());
                mainView.button.setText(Objects.requireNonNull(getItem(position)).getName());
                mainView.img.setImageDrawable(getDrawable(Objects.requireNonNull(getItem(position)).getIcon()));
            }

            return convertView;
        }
    }

    //Create a constructor class to hold the provided button.\
    public class viewHolder {
        Button button;
        ImageView img;
    }

    private void removeChildren(GridView v){
        v.removeAllViews();
    }

    private void changeName(String name){
        String s = "Hello " + name;
        tex.setText(s);

        Log.d("BUTTONS", "User tapped the Supabutton");
    }
}