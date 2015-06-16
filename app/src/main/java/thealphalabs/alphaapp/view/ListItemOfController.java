package thealphalabs.alphaapp.view;


import android.app.LauncherActivity;

public class ListItemOfController {
    private int iconResId;
    private String title;
    private String desc;
    private boolean toggleValue;
    private boolean expanded;

    public ListItemOfController() {
        this.iconResId = 0;
        this.title = "Title";
        this.desc = "Desc";
        this.toggleValue = false;
        this.expanded = false;
    }
    public ListItemOfController(String title) {
        this.title = title;
    }

    public ListItemOfController(int iconResId, String title, String desc, boolean toggleValue) {
        this.iconResId = iconResId;
        this.title = title;
        this.desc = desc;
        this.toggleValue = toggleValue;
    }

    // Getter and setter
    public int getIconResId() {return this.iconResId;}
    public String getTitle() {return this.title;}
    public String getDesc() {return this.desc;}
    public boolean getToggleValue() {return this.toggleValue;}
    public boolean getExpaned() {return this.expanded;}
    public void setIconResId(int resId) {this.iconResId = resId;}
    public void setTitle(String title) {this.title = title;}
    public void setDesc(String desc) {this.desc = desc;}
    public void setToggleValue(boolean toggleValue) {this.toggleValue = toggleValue;}
    public void setExpanded(boolean expanded) {this.expanded = expanded;}
}
