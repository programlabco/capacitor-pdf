import { WebPlugin } from '@capacitor/core';
import { PdfPluginPlugin, PdfOptions } from './definitions';
export declare class PdfPluginWeb extends WebPlugin implements PdfPluginPlugin {
    constructor();
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    viewPdf(value: PdfOptions): Promise<any>;
}
declare const PdfPlugin: PdfPluginWeb;
export { PdfPlugin };
