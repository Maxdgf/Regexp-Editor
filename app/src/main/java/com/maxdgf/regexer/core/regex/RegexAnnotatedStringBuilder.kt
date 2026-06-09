package com.maxdgf.regexer.core.regex

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import com.maxdgf.regexer.core.regex.data_models.RegexMatch
import java.util.regex.PatternSyntaxException

import com.maxdgf.regexer.ui.data_management.view_models.AppState

class RegexAnnotatedStringBuilder(private val appState: AppState) {
    /**
     * Creates an additional line with the regular expression exception marking at the string index.
     *
     * @param exceptionIndex exception index in test text.
     * @param pattern regexp pattern.
     *
     * @return exception message string.
     * */
    private fun setExceptionPatternMarkByIndex(
        exceptionIndex: Int,
        pattern: String
    ): String {
        val result = StringBuilder()
        val patternLength = pattern.length

        result.append(pattern + "\n") // adding pattern with line feed

        // creating a exception mark string
        for (i in 0..patternLength) {
            if (i == exceptionIndex) {
                result.append("^--(!)") // exception mark
                break
            }
            result.append(' ') //space
        }

        return result.toString()
    }

    /**
     * Creates and transforms an annotated string from the test text with marked regular expression matches,
     * taking into account the user-set regular expression flags and the search state (global, local),
     * and returns an exception in case of an incorrectly entered regular expression.
     *
     * @param material input annotated string.
     * @param inputRegexPattern input regexp pattern.
     * @param matchColor color for select matches.
     * @param flagsList list of needed regexp flags.
     * @param globalSearchState global match search state(true - all, false - first match).
     *
     * @return styled annotated string.
     */
    fun setStyleOnMatchedWords(
        material: AnnotatedString, // input text
        inputRegexPattern: String, // input regexp pattern
        matchColor: Color, // regexp match color
        flagsList: Set<RegexOption>, // enabled regexp flags collection
        globalSearchState: Boolean, // global search state
    ): AnnotatedString {
        val resultAnnotatedString = AnnotatedString.Builder() // result annotated string builder
        val allMatchesList = mutableListOf<RegexMatch>()

        try {
            allMatchesList.clear()
            appState.updateRegexExceptionMessageState(null) // set null to regexp exception message state

            if (inputRegexPattern.isNotEmpty()) {
                val regex = Regex(inputRegexPattern, flagsList) // kotlin Regex object with -> user regexp pattern and flags list
                val allMatches = when (globalSearchState) { // global match search state handling
                    true -> regex.findAll(material) // true (global) -> multiple matches
                    false -> regex.find(material) // false (local) -> first match
                }
                val matchesCount = when (allMatches) { // processing matches count
                    is Sequence<*> -> allMatches.count() // matches list(global search), getting list size as matches count
                    is MatchResult? -> { // first match(local search), getting 1 or 0(match is exists or not)
                        allMatches?.let { match -> // handling isNotEmpty() state
                            if (match.value.isNotEmpty())  1
                            else 0
                        } ?: 0 // return 0
                    } else -> 0 // return 0
                }

                if (matchesCount > 0) { // if there is a match ->
                    var lastIndex = 0                    // last index of last match
                    val materialLength = material.length // input text length

                    appState.updateMatchesCount(matchesCount) // updating matches count state

                    val matchSpanStyle = SpanStyle(background = matchColor) // match span style configuration

                    if (allMatches is Sequence<*>) { // global search(multiple matches)
                        allMatchesList.addAll(
                            allMatches.map {
                                if (it is MatchResult) {
                                    RegexMatch(
                                        match = it.value,
                                        start = it.range.first,
                                        end = it.range.last + 1
                                    )
                                } else RegexMatch(match = "", start = 0, end = 0)
                            }
                        )

                        allMatches.forEach { match ->
                            if (match is MatchResult) {
                                val matchStart = match.range.first // match start index
                                val matchEnd = match.range.last + 1 // match end index

                                // configuring text part with match
                                val beforeMatchPart = material.substring(lastIndex, matchStart) // before match text part
                                val matchPart = material.substring(matchStart, matchEnd) // match part
                                val partLength = (beforeMatchPart + matchPart).length // all part length

                                resultAnnotatedString.append(beforeMatchPart)                         // adding before match part
                                resultAnnotatedString.withStyle(matchSpanStyle) { append(matchPart) } // adding match part with span style
                                lastIndex += partLength                                               // updating last index of last match
                            } else return@forEach // skip current iteration forEach if match not is MatchResult
                        }
                    } else if (allMatches is MatchResult?) {
                        allMatches?.let { match ->
                            val matchStart = match.range.first  // match start index
                            val matchEnd = match.range.last + 1 // match end index

                            // configuring text part with match
                            val beforeMatchPart = material.substring(lastIndex, matchStart) // before match text part
                            val matchPart = material.substring(matchStart, matchEnd)        // match part
                            val partLength = (beforeMatchPart + matchPart).length           // all part length

                            resultAnnotatedString.append(beforeMatchPart)
                            resultAnnotatedString.withStyle(matchSpanStyle) { append(matchPart) }

                            lastIndex += partLength // updating last index of last match

                            allMatchesList.add(
                                RegexMatch(
                                    match = match.value,
                                    start = matchStart,
                                    end = matchEnd
                                )
                            )
                        }
                    }

                    // last match index check
                    if (lastIndex < materialLength) {
                        val otherText = material.substring(lastIndex) // remaining text after the last match
                        resultAnnotatedString.append(otherText)       // adding remaining text after the last match
                    }
                } else {
                    resultAnnotatedString.append(material) // adding text without style
                    appState.updateMatchesCount(0)         // updating matches count to 0
                }
            } else {
                resultAnnotatedString.append(material) // adding text without style
                appState.updateMatchesCount(0)         // updating matches count to 0
            }
        } catch (e: PatternSyntaxException) {
            // handling pattern syntax exception ->

            allMatchesList.clear()
            resultAnnotatedString.append(material) // adding text without style

            appState.updateMatchesCount(0) // updating matches count to 0

            val exceptionMessage = e.description + "\n" + "-> Index: ${e.index}" + "\n" +
                    setExceptionPatternMarkByIndex(
                        e.index - 1, inputRegexPattern
                    ) // configuring regexp exception message with exception mark by index
            appState.updateRegexExceptionMessageState(exceptionMessage) // setting exception message
        } finally {
            appState.updateAllMatchesByRegexpList(allMatchesList) // set all matches list
        }

        return resultAnnotatedString.toAnnotatedString()
    }
}