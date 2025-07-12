package fr.inventory.emballmois.ui.utils

import org.apache.commons.text.similarity.LevenshteinDistance


fun normalizeReferenceName(name: String): String {
    return name.lowercase()
        .replace(Regex("[^\\w\\s]"), "")
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .sorted()
        .joinToString(" ")
}

fun findSimilarStrings(reference: String, listToCompare: List<String>, maxDistanceThreshold: Int): Set<String> {
    val normalizedNewRef = normalizeReferenceName(reference)
    val levenshtein = LevenshteinDistance()
    val similarMatches = mutableSetOf<String>()

    for (existingName in listToCompare) {
        val normalizedExisting = normalizeReferenceName(existingName)
        if (normalizedExisting.isBlank() && normalizedNewRef.isBlank()) {
            continue
        }
        if (normalizedExisting.isBlank() || normalizedNewRef.isBlank()) {
            continue
        }

        val distance = levenshtein.apply(normalizedNewRef, normalizedExisting)
        if (distance <= maxDistanceThreshold) {
            similarMatches.add(existingName)
        }
        if(normalizedExisting.contains(normalizedNewRef)){
            similarMatches.add(existingName)
        }
    }
    return similarMatches
}
