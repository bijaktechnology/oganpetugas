package com.bijak.techno.oganlopian.model;

public class MenuModel {
    private String menu_id,menu_name,menu_icon,menu_urutan,alias;
    private int icone;

    public void setAlias(String activiti){ this.alias=activiti;}
    public void setMenu_id(String menuid){ this.menu_id=menuid;}
    public void setMenu_name(String menunama){this.menu_name=menunama;}
    public void setMenu_icon(String menuicon){this.menu_icon=menuicon;}
    public void setMenu_urutan(String urutan){ this.menu_urutan = urutan;}
    public void setIcone(int icons){this.icone=icons;}
    public int getIcone(){ return this.icone;}
    public String getMenu_id(){ return this.menu_id;}
    public String getMenu_name(){ return this.menu_name;}
    public String getMenu_icon(){ return this.menu_icon;}
    public String getMenu_urutan(){ return this.menu_urutan;}
    public String getAlias(){ return this.alias;}

}
