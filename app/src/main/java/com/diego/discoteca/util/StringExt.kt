package com.diego.discoteca.util

import java.text.Normalizer

/* Discogs, remove "(digit)" after artist/group name Discogs Api
   Booba (2) -> Booba */
fun String.removeParenthesesDigit() = this.replace("[(][\\d][)]".toRegex(), "").trim()

// Remove diacritical marks
fun String.normalize() = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("\\p{Mn}+".toRegex(), "")

/* Delete whitespace before/after a simple quote
   L' envers du décor -> L'envers du décor
   L 'envers du décor -> L'envers du décor
   L  '  envers du décor -> L'envers du décor */
fun String.noSimpleQuoteWhiteSpace() =
    this.replace("['][\\s]+".toRegex(), "'")
        .replace("[\\s]+[']".toRegex(), "'")

/* Delete dot(.)
   Mr. Oizo -> Mr Oizo */
fun String.noDot() =
    this.replace("\\.".toRegex(), "")

fun String.stringProcess() =
    this.noSimpleQuoteWhiteSpace()
        .replace("\\s{2,}".toRegex(), " ").trim()

fun String.notNonWordCharacter() =
    this.replace("[\\W]".toRegex(), " ").trim()

fun String.stringNormalizeDatabase() =
    this.noDot().normalize().notNonWordCharacter().lowercase()