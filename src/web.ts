import { WebPlugin } from '@capacitor/core';
import { PdfPluginPlugin, PdfOptions } from './definitions';

export class PdfPluginWeb extends WebPlugin implements PdfPluginPlugin {
  constructor() {
    super({
      name: 'PdfPlugin',
      platforms: ['web']
    });
  }

  async echo(options: { value: string }): Promise<{value: string}> {
    console.log('ECHO', options);
    return options;
  }

  async viewPdf(value: PdfOptions): Promise<any>{
    console.log('VIEW_PDF', value);
    return value;
  }
}

const PdfPlugin = new PdfPluginWeb();

export { PdfPlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(PdfPlugin);
