import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(PdfPlugin)
public class PdfPlugin: CAPPlugin {
    
    @objc func viewPdf(_ call: CAPPluginCall) {
        
        let _link = call.getString("linkPdf") ?? ""
        let _annotations = call.getArray("annotations", PdfAnnotation.self) ?? []
    
        DispatchQueue.main.async {
            let storyboard = UIStoryboard(name: "Pdf", bundle: Bundle(for: Self.self))
            // It is instance of  `NewViewController` from storyboard
            let vc = storyboard.instantiateViewController(withIdentifier: "pdfViewController") as! PdfViewController
            vc.link = _link
            vc.annotations = _annotations
            
            self.bridge.viewController.present(vc, animated: true, completion: nil)
        }
        
        call.success()
    }
    
    
}


