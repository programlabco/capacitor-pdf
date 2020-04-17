declare module "@capacitor/core" {
  interface PluginRegistry {
    PdfPlugin: PdfPluginPlugin;
  }
}

export interface PdfPluginPlugin {
  echo(options: { value: string }): Promise<{value: string}>;
}
