package com.abaddon83.legal.fileDocuments.domainModels

case class DocumentTemplate(
                           val name: String,
                           val body: Array[Byte]
                           ) {
}


/*
sealed trait ChessPiece {def character: Char; def pointValue: Int}
object ChessPiece {
  case object KING extends ChessPiece {val character = 'K'; val pointValue = 0}
  case object QUEEN extends ChessPiece {val character = 'Q'; val pointValue = 9}
  case object BISHOP extends ChessPiece {val character = 'B'; val pointValue = 3}
  case object KNIGHT extends ChessPiece {val character = 'N'; val pointValue = 3}
  case object ROOK extends ChessPiece {val character = 'R'; val pointValue = 5}
  case object PAWN extends ChessPiece {val character = 'P'; val pointValue = 1}
}
*/