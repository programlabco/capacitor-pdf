import { WebPlugin } from '@capacitor/core';
import { PdfPluginPlugin } from './definitions';

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
}

const PdfPlugin = new PdfPluginWeb();

export { PdfPlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(PdfPlugin);
