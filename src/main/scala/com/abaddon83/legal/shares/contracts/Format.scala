package com.abaddon83.legal.shares.contracts

sealed trait Format{
  val format: String
}

object Format {
  def valueOf(format:String): Format ={
    format.toLowerCase match {
      case "pdf" => PDF
      case "doc" => DOC
      case "jpg" => JPG
      case _ => throw new IllegalArgumentException(s"Format ${format} not recognised")
    }
  }

  case object PDF extends Format {
    override val format: String = "PDF"
  }
  case object DOC extends Format{
    override val format: String = "DOC"
  }
  case object JPG extends Format
  {
    override val format: String = "JPG"
  }
}



/*sealed trait ChessPiece {def character: Char; def pointValue: Int}
object ChessPiece {
  case object KING extends ChessPiece {val character = 'K'; val pointValue = 0}
  case object QUEEN extends ChessPiece {val character = 'Q'; val pointValue = 9}
  case object BISHOP extends ChessPiece {val character = 'B'; val pointValue = 3}
  case object KNIGHT extends ChessPiece {val character = 'N'; val pointValue = 3}
  case object ROOK extends ChessPiece {val character = 'R'; val pointValue = 5}
  case object PAWN extends ChessPiece {val character = 'P'; val pointValue = 1}
}

 */