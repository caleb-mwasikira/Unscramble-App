package com.example.android.unscramble

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * Implementing [AndroidViewModel] instead of ViewModel
 * allows us to access the activity's context
 */
class GameViewModel(application: Application): AndroidViewModel(application) {
    private var _score = 0
    val score get() = _score

    private var _currentWordCount = 0
    val currentWordCount get() = _currentWordCount

    private lateinit var _currentWord: String
    private lateinit var _currentScrambledWord: String
    val currentScrambledWord get() = _currentScrambledWord

    private var words: List<String> =
        getApplication<Application>().resources.getStringArray(R.array.words).toList()
    private var viewedWords: MutableList<String> = mutableListOf()

    companion object {
        const val MAX_NO_OF_WORDS = 10
        const val SCORE_INCREASE = 20
    }

    init {
        getNextWord()
    }

    private fun getNextWord() {
        _currentWord = words.random()
        if(viewedWords.contains(_currentWord)) {
            getNextWord()
        }

        val tempWord = _currentWord.toCharArray()
        tempWord.shuffle()
        while(String(tempWord) == _currentWord) {
            tempWord.shuffle()
        }
        _currentScrambledWord = String(tempWord)
        _currentWordCount++
        viewedWords.add(_currentWord)
    }

    fun nextWord(): Boolean {
        return if(_currentWordCount < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    fun isUsersGuessCorrect(usersGuess: String): Boolean {
        return if(usersGuess == _currentWord) {
            _score += SCORE_INCREASE
            true
        } else false
    }

    fun reinitializeData() {
        _score = 0
        _currentWordCount = 0
        viewedWords.clear()
        getNextWord()
    }
}