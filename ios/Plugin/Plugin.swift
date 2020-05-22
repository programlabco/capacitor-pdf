import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(PdfPlugin)
public class PdfPlugin: CAPPlugin {
    
    @objc func viewPdf(_ call: CAPPluginCall) {
        let link = call.getString("linkPdf") ?? ""
        let annotations = call.getArray("annotations", [PdfAnnotation].self)
        
        PdfPlugin.downloadFile(link: link) { (route) in
            print(link)
        }
        
        call.success()
    }
    
    class func downloadFile(link: String, completionHandler: @escaping (_ route: String) -> ()) {
        guard let url = URL(string: link) else { return }
        let task = URLSession.shared.downloadTask(with: url) { localURL, urlResponse, error in
            if let localURL = localURL {
                if let string = try? String(contentsOf: localURL) {
                    print(string)
                    completionHandler(string)
                }
            }
        }

        task.resume()
    }
    
    func showPdf() {
        
    }
    
    func createAnnotation() {
        
    }
}

class PdfAnnotation {
    var page: Int8 = 0
    var point_x: Double
    var point_y: Double
    var point_link: String
    var point_icon: String
    var point_color_icon: String
    var point_background_icon: String
    
    init(
        _ page: Int8,
        _ point_x: Double,
        _ point_y: Double,
        _ point_link: String,
        _ point_icon: String,
        _ point_color_icon: String,
        _ point_background_icon : String
    ) {
        self.page = page
        self.point_x = point_x
        self.point_y = point_y
        self.point_link = point_link
        self.point_icon = point_icon
        self.point_color_icon = point_color_icon
        self.point_background_icon = point_background_icon
    }
}
