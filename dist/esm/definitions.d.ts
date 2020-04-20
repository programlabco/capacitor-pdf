declare module "@capacitor/core" {
    interface PluginRegistry {
        PdfPlugin: PdfPluginPlugin;
    }
}
export interface PdfAnnotation {
    page: Number;
    point_x: Number;
    point_y: Number;
    point_link: String;
    point_icon: String;
    point_color_icon: String;
    point_background_icon: String;
}
export interface PdfOptions {
    linkPdf: String;
    annotations: PdfAnnotation[];
}
export interface PdfPluginPlugin {
    viewPdf(options: PdfOptions): Promise<{
        value: string;
    }>;
}
