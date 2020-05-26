//
//  PdfAnnotation.swift
//  Plugin
//
//  Created by OCAMPO PALAU on 22/05/20.
//  Copyright © 2020 Max Lynch. All rights reserved.
//

import Foundation
public class PdfAnnotation{
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
