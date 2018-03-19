package model

import Config.CLUBS
import Config.DIAMONDS
import Config.HEARTS
import Config.SPADES

/**
 * Created by Nezneika on 3/18/18.
 */

// game model is now singleton / static
object GameModel {

    val deck = Deck()
    // is this the temp pile ? must it have a maximum slot?
    // no, wastePile is the cards which already went through, it is later put back to model.Deck
    val wastePile: MutableList<Card> = mutableListOf()

    val foundationPiles = arrayOf(
            FoundationPile(DIAMONDS),
            FoundationPile(HEARTS),
            FoundationPile(CLUBS),
            FoundationPile(SPADES)
    )

    val tableauPiles = Array(7, { TableauPile() })

    /**
     * Reset model.Deck, wastePile, 4 foundationPiles, and 7 tableauPiles
     */
    fun resetGame() {
        wastePile.clear()
        foundationPiles.forEach { it.reset() }
        deck.reset()

        tableauPiles.forEachIndexed { i, tableauPile ->
            val cardsInPile: MutableList<Card> = Array(i + 1, { deck.drawCard() }).toMutableList()
            tableauPiles[i] = TableauPile(cardsInPile)
        }
    }

    /**
     * Tap to open last model.Card in model.Deck
     * if model.Deck is empty, move all from wastePile like a loop
     */
    fun onDeckTap() {
        if (deck.cardsInDeck.size > 0) {
            val card = deck.drawCard()
            card.faceUp = true
            wastePile.add(card)
        } else {
            deck.cardsInDeck = wastePile.toMutableList()
            wastePile.clear()
        }
    }

    fun onWasteTap() {
        if (wastePile.size > 0) {
            val card = wastePile.last()
            if (playCard(card)) {
                wastePile.remove(card)
            }
        }
    }

    fun onFoundationTap(foundationIndex: Int) {
        val foundationPile = foundationPiles[foundationIndex]
        if (foundationPile.cards.size > 0) {
            val card = foundationPile.cards.last()
            if (playCard(card)) {
                foundationPile.removeCard(card)
            }
        }
    }

    fun onTableauTap(tableauIndex: Int, cardIndex: Int) {
        val tableauPile = tableauPiles[tableauIndex]
        if (tableauPile.cards.size > 0) {
            val cards = tableauPile.cards.subList(cardIndex, tableauPile.cards.lastIndex + 1)
            if (playCards(cards)) {
                tableauPile.removeCards(cardIndex)
            }
        }
    }

    private fun playCards(cards: MutableList<Card>): Boolean {
        if (cards.size == 1) {
            return playCard(cards.first())
        } else {
            tableauPiles.forEach {
                if (it.addCards(cards)) {
                    return true
                }
            }
        }
        return false
    }

    private fun playCard(card: Card): Boolean {
        foundationPiles.forEach {
            if (it.addCard(card)) {
                return true
            }
        }

        tableauPiles.forEach {
            if (it.addCards(mutableListOf(card))) {
                return true
            }
        }
        return false
    }

}