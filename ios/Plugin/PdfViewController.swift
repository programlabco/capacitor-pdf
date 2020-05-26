//
//  PdfViewController.swift
//  Plugin
//
//  Created by OCAMPO PALAU on 22/05/20.
//  Copyright © 2020 Max Lynch. All rights reserved.
//
import UIKit
import PDFKit

class PdfViewController: UIViewController {
    
    
    @IBOutlet weak var pdfViewContainer: PDFView!
    
    
    var link: String?
    var annotations: [PdfAnnotation]?
    var vSpinner: UIView?
    
    init() {
        super.init(nibName: nil, bundle: nil)
    }
    
    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)!;
    }
    
    override open func loadView() {
        super.loadView()
        
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.setup()
        // Do any additional setup after loading the view.
    }
    

    
    
    func setup() {
        self.showSpinner(onView: self.view)
        PdfViewController.downloadFile(link: self.link ?? "") { (route) in
            if let route = route {
                print(route)
                self.removeSpinner()
                self.renderPdf(uri: route)
            }
            
        }
        
    }
    
    func renderPdf(uri: URL) {
        if let pdfDocument = PDFDocument(url: uri) {
            self.pdfViewContainer?.displayMode = .singlePage
            self.pdfViewContainer?.displayDirection = .horizontal
            self.pdfViewContainer?.usePageViewController(true)
            self.pdfViewContainer?.autoScales = true
            self.pdfViewContainer?.minScaleFactor = 0.4
            // pdfView.displayDirection = .horizontal
            self.pdfViewContainer?.document = pdfDocument
            
            //pdfView.go(to: (pdfDocument.page(at: 3) ?? pdfDocument.page(at: 1))!)
            
        }
    }
    

    class func downloadFile(link: String, completionHandler: @escaping (_ route: URL?) -> ()) {
        
        if let url = URL(string: link) {
            let fileName = String((url.lastPathComponent)) as NSString
            let documentsUrl:URL =  (FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first as URL?)!
            let destinationFileUrl = documentsUrl.appendingPathComponent("\(fileName)")
            let task = URLSession.shared.downloadTask(with: url) { localURL, urlResponse, error in

                
                if let tempLocalUrl = localURL, error == nil {
                    // Success
                    if let statusCode = (urlResponse as? HTTPURLResponse)?.statusCode {
                        print("Successfully downloaded. Status code: \(statusCode)")
                    }
                    
                    do {
                        try FileManager.default.removeItem(at: destinationFileUrl)
                    } catch let error as NSError {
                        print("Error: \(error.domain)")
                    }
                    
                    do {
                        try FileManager.default.copyItem(at: tempLocalUrl, to: destinationFileUrl)
                        do {
                            DispatchQueue.main.async {
                              completionHandler(destinationFileUrl)
                            }
                            
                        }
                    } catch CocoaError.fileWriteFileExists {
                        DispatchQueue.main.async {
                          completionHandler(destinationFileUrl)
                        }
                    } catch let error {
                        // If the operation failed again, abort for real.
                        print("Other error: \(error)")
                    }
                } else {
                    print("Error took place while downloading a file. Error description: \(error?.localizedDescription ?? "")")
                }
                
                
            }
            task.resume()
        } else {
            print("No existe url")
        }
        

        
    }
    
    func showPdf() {
        
    }
    
    func createAnnotation() {
        
    }
    
    func showSpinner(onView : UIView) {
        let spinnerView = UIView.init(frame: onView.bounds)
        spinnerView.backgroundColor = UIColor.init(red: 0.5, green: 0.5, blue: 0.5, alpha: 0.5)
        let ai = UIActivityIndicatorView.init(style: .whiteLarge)
        ai.startAnimating()
        ai.center = spinnerView.center
        
        DispatchQueue.main.async {
            spinnerView.addSubview(ai)
            onView.addSubview(spinnerView)
        }
        
        vSpinner = spinnerView
    }
    
    func removeSpinner() {
        DispatchQueue.main.async {
            self.vSpinner?.removeFromSuperview()
        }
    }
    

}
