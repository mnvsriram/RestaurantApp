package app.resta.com.restaurantapp.model;

import java.io.Serializable;

public class MenuCardAction implements Serializable {
    private long id;
    private long buttonId;
    private long menuTypeId;
    private String menuTypeName;
    private int layoutId;
    private String layoutName;
    private int position;

    public String getMenuTypeName() {
        return menuTypeName;
    }

    public void setMenuTypeName(String menuTypeName) {
        this.menuTypeName = menuTypeName;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public long getButtonId() {
        return buttonId;
    }

    public void setButtonId(long buttonId) {
        this.buttonId = buttonId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(long menuTypeId) {
        this.menuTypeId = menuTypeId;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuCardAction that = (MenuCardAction) o;

        if (menuTypeId != that.menuTypeId) return false;
        return layoutId == that.layoutId;

    }

    @Override
    public int hashCode() {
        int result = (int) (menuTypeId ^ (menuTypeId >>> 32));
        result = 31 * result + (int) (layoutId ^ (layoutId >>> 32));
        return result;
    }
}
