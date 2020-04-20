package com.programlab.capacitor_pdf.models;

public class PdfAnnotations {
    private int point_x;
    private int point_y;
    private String point_link;
    private String point_icon;
    private String point_color_icon;
    private String point_background_icon;

    public PdfAnnotations(){}

    public PdfAnnotations(int point_x, int point_y, String point_link, String point_icon, String point_color_icon, String point_background_icon) {
        this.point_x = point_x;
        this.point_y = point_y;
        this.point_link = point_link;
        this.point_icon = point_icon;
        this.point_color_icon = point_color_icon;
        this.point_background_icon = point_background_icon;
    }

    public int getPoint_x() {
        return point_x;
    }

    public void setPoint_x(int point_x) {
        this.point_x = point_x;
    }

    public int getPoint_y() {
        return point_y;
    }

    public void setPoint_y(int point_y) {
        this.point_y = point_y;
    }

    public String getPoint_link() {
        return point_link;
    }

    public void setPoint_link(String point_link) {
        this.point_link = point_link;
    }

    public String getPoint_icon() {
        return point_icon;
    }

    public void setPoint_icon(String point_icon) {
        this.point_icon = point_icon;
    }

    public String getPoint_color_icon() {
        return point_color_icon;
    }

    public void setPoint_color_icon(String point_color_icon) {
        this.point_color_icon = point_color_icon;
    }

    public String getPoint_background_icon() {
        return point_background_icon;
    }

    public void setPoint_background_icon(String point_background_icon) {
        this.point_background_icon = point_background_icon;
    }
}
