package com.maxdgf.regexer.core.regex

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

class RegexpSyntaxAnnotatedStringBuilder {
    /*
    regexp syntax collection ->
    ([1]-Regex, [2]-Color)
    -1 - regexp for current syntax part detection
    -2 - color for colorize part of the syntax
    */
    private val syntaxCollection = setOf(
        Regex("(?<!\\\\)[\\^$]") to Color(0xFF962501),                                    // end, start (^$)
        Regex("\\\\w|\\\\d|\\\\s", RegexOption.IGNORE_CASE) to Color(0xFFFF5722), // \w \W \d \D \s \S
        Regex("(?<!\\\\)[()]") to Color(0xFF4CAF50),                                      // ()
        Regex("(?<!\\\\)[\\[\\].]") to Color(0xFFFFC107),                                 // [].
        Regex("(?<!\\\\)[{}?!*+]") to Color(0xFF2196F3),                                  // {} ?!*+
        Regex("(?<!\\\\)\\|") to Color(0xFF2196F3)                                        // |
    )

    /**
     * Creates and transforms an annotated string from regular expression input by marking certain parts of the syntax in a specific color,
     * and in the case of a regular expression exception, colors the entire text red and gives it an underlined style.
     *
     * @param material input annotated string.
     * @param exceptionMessage exception message.
     * @param isLiteralFlagEnabled state of literal regexp flag(enabled or not).
     *
     * @return styled annotated string.
     */
    fun setRegexpSyntaxStyleOnRegexStringPattern(
        material: AnnotatedString, // input text
        exceptionMessage: String? = null, // exception message
        isLiteralFlagEnabled: Boolean = false // is literal regexp flag enabled state
    ): AnnotatedString {
        val resultAnnotatedString = AnnotatedString.Builder() // result annotated string builder

        if (material.isNotEmpty()) {
            val errorStyle = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = Color.Red
            )

            if (exceptionMessage == null) {
                if (!isLiteralFlagEnabled) {
                    var lastIndex = 0 // last index of last match

                    val materialLength = material.length // input text length
                    val matchesList = mutableListOf<MatchResult>() // all matches list

                    syntaxCollection.forEach { syntax ->
                        val matches = syntax.first.findAll(material).toList() // all matches by current syntax regexp
                        val matchesCount = matches.size // matches count

                        if (matchesCount > 0)
                            // add matches to all matches list
                            matches.forEach { match ->
                                matchesList.add(match)
                            }
                    }

                    val sortedMatchesList = matchesList.sortedBy { it.range.first } // sort matches by detection order

                    sortedMatchesList.forEach { match ->
                        val matchStart = match.range.first  // match start index
                        val matchEnd = match.range.last + 1 // match end index

                        // configuring text part with match
                        val beforeMatchPart = material.substring(lastIndex, matchStart)  // before match text part
                        val matchPart = material.substring(matchStart, matchEnd) // match part
                        val partLength = (beforeMatchPart + matchPart).length // all part length

                        // configuring match style
                        val matchStyle = SpanStyle(
                            color = syntaxCollection.find {
                                it.first.matches(matchPart)
                            }?.second ?: Color.Black // default color
                        )

                        resultAnnotatedString.append(beforeMatchPart)                     // adding before match part
                        resultAnnotatedString.withStyle(matchStyle) { append(matchPart) } // adding match part with span style

                        lastIndex += partLength // updating last index of last match
                    }

                    // last match index check
                    if (lastIndex < materialLength) {
                        val otherText = material.substring(lastIndex) // remaining text after the last match

                        resultAnnotatedString.append(otherText) // adding remaining text after the last match
                    }
                } else resultAnnotatedString.append(material) // adding text without style
            } else resultAnnotatedString.withStyle(errorStyle) { append(material) }
        } else resultAnnotatedString.append(material) // adding text without style

        return resultAnnotatedString.toAnnotatedString()
    }
}