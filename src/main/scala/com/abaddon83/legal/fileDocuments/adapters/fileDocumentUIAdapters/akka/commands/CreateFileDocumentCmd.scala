package com.abaddon83.legal.fileDocuments.adapters.fileDocumentUIAdapters.akka.commands

import com.abaddon83.legal.shares.contracts.Format


case class CreateFileDocumentCmd(
                             documentTemplateName: String,
                             documentDetails: Map[String,String],
                             format: Format
                             ) {

}
