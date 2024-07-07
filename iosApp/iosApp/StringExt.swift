//
//  StringExt.swift
//  iosApp
//
//  Created by hehua2008 on 2024/7/8.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

extension String {
    func htmlToAttributedString() -> NSAttributedString? {
        do {
            return try NSAttributedString(
                data: self.data(using: String.Encoding.utf8, allowLossyConversion: true)!,
                options: [
                    NSAttributedString.DocumentReadingOptionKey.documentType: NSAttributedString.DocumentType.html,
                    NSAttributedString.DocumentReadingOptionKey.characterEncoding: String.Encoding.utf8.rawValue
                ],
                documentAttributes: nil
            )
        } catch {
            return nil
        }
    }

    func htmlToString() -> String {
        return htmlToAttributedString()?.string ?? self
    }
}
