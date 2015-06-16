package thealphalabs.alphaapp;

import android.graphics.Color;
import android.os.Bundle;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class GlassAppMain extends MaterialNavigationDrawer {
    private final String TAG = "GlassAppMain";

    @Override
    public void init(Bundle savedInstanceState) {

        // set the header image
        this.setDrawerHeaderImage(R.drawable.mat2);

        // create sections
        this.addSection(newSection("Home", R.drawable.ic_home_black_48dp,
                new FragmentHome()).setSectionColor(Color.parseColor("#48a0b2")));
        this.addSection(newSection("Controller", R.drawable.ic_mouse_black_48dp,
                new FragmentController()).setSectionColor(Color.parseColor("#ccbb14")));
        this.addSection(newSection("Appstore", R.drawable.ic_shopping_basket_black_48dp,
                new FragmentAppstore()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("Setting", R.drawable.ic_settings_black_48dp,
                new FragmentSetting()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("About", R.drawable.ic_help_outline_black_48dp ,
                new FragmentAbout()).setSectionColor(Color.parseColor("#03a9f4")));

        setDefaultSectionLoaded(1);
        // create bottom section
//        this.addBottomSection(newSection("Homepage", R.drawable.ic_settings_black_24dp,new Intent(this,Settings.class)));
    }
}
